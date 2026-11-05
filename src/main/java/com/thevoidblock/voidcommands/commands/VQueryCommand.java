package com.thevoidblock.voidcommands.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.thevoidblock.voidcommands.ChunkTracker;
import dev.xpple.clientarguments.arguments.CEntityArgument;
import dev.xpple.clientarguments.arguments.CResourceArgument;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import static com.thevoidblock.voidcommands.VoidCommands.CLIENT;
import static com.thevoidblock.voidcommands.VoidCommands.COMMAND_PREFIX;
import static com.thevoidblock.voidcommands.VoidCommandsStyler.ERROR_FORMATTING;
import static java.lang.String.format;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.*;
import static net.minecraft.network.chat.Component.translatable;
import static java.lang.Math.*;

@Environment(EnvType.CLIENT)
public class VQueryCommand {

    private static final String ENTITY_QUERY_NAME = "entities";
    private static final String BLOCK_QUERY_NAME = "blocks";

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, buildContext) -> dispatcher.register(
                        literal(format("%squery", COMMAND_PREFIX)).then(
                                literal(ENTITY_QUERY_NAME).then(
                                        argument("entities", CEntityArgument.entities()).executes(
                                                context -> executeEntities(context, CLIENT.options.getEffectiveRenderDistance())
                                        ).then(
                                                argument("distance", IntegerArgumentType.integer(0, CLIENT.options.getEffectiveRenderDistance())).executes(
                                                        context -> executeEntities(
                                                                context,
                                                                IntegerArgumentType.getInteger(context, "distance")
                                                        )
                                                )
                                        )
                                )
                        ).then(
                                literal(BLOCK_QUERY_NAME).then(
                                        argument("blocks", CResourceArgument.resource(buildContext, Registries.BLOCK)).executes(
                                                context -> executeBlocks(
                                                        context,
                                                        CResourceArgument.getResource(context, "blocks", Registries.BLOCK),
                                                        CLIENT.options.getEffectiveRenderDistance()
                                                )
                                        ).then(
                                                argument("distance", IntegerArgumentType.integer(0, CLIENT.options.getEffectiveRenderDistance())).executes(
                                                        context -> executeBlocks(
                                                                context,
                                                                CResourceArgument.getResource(context, "blocks", Registries.BLOCK),
                                                                IntegerArgumentType.getInteger(context, "distance")
                                                        )
                                                )
                                        )
                                )
                        )
                ));
    }

    private static int executeEntities(CommandContext<FabricClientCommandSource> context, int distance) {
        int queryCount = 0;
        Vec3 sourcePos = context.getSource().getPosition();
        try {
            queryCount = CEntityArgument.getEntities(context, "entities").stream()
                    .filter(entity ->
                        pow((int)floor(entity.position().x/16) - (int)floor(sourcePos.x/16), 2) + pow((int)floor(entity.position().z/16) - (int)floor(sourcePos.z/16), 2) <= pow(distance, 2)
                    )
                    .toList().size();
            context.getSource().sendFeedback(getFeedback(context, queryCount, distance));
        } catch (CommandSyntaxException ignored) {}
        if(queryCount == 0)
            context.getSource().sendFeedback(
                    translatable(
                            "chat.voidcommands.entity_not_found",
                            getQueryArgument(context),
                            distance
                    ).withStyle(ERROR_FORMATTING)
            );

        return queryCount;
    }

    private static int executeBlocks(CommandContext<FabricClientCommandSource> context, Holder.Reference<Block> blockReference, int distance) {
        new Thread(() -> {
            AtomicInteger queryCount = new AtomicInteger(0);

            ClientLevel level = context.getSource().getLevel();
            final Vec3 sourcePos = context.getSource().getPosition();

            for(ChunkPos chunkPos : new ArrayList<>(ChunkTracker.loadedChunks)) {
                if (pow(chunkPos.x() - (int) floor(sourcePos.x / 16), 2) + pow(chunkPos.z() - (int) floor(sourcePos.z / 16), 2) <= pow(distance, 2)) {
                    level.getChunk(chunkPos.x(), chunkPos.z()).findBlocks(blockState -> blockReference.value().equals(blockState.getBlock()), (_, _) -> queryCount.incrementAndGet());
                }
            }

            Component feedback;

            if(queryCount.get() != 0) {
                feedback = getFeedback(context, queryCount.get(), distance);
            } else {
                feedback = Component.translatable("chat.voidcommands.block_not_found", getQueryArgument(context), distance).withStyle(ERROR_FORMATTING);
            }

            CLIENT.execute(() -> context.getSource().sendFeedback(feedback));
        }).start();

        return Command.SINGLE_SUCCESS;
    }

    private static Component getFeedback(CommandContext<FabricClientCommandSource> context, int count, int distance) {
        String formattedCount = NumberFormat
                .getInstance(Locale.US)
                .format(count);

        return translatable(
                "chat.voidcommands.query_found",
                formattedCount,
                getQueryArgument(context),
                distance
        );
    }

    private static String getQueryArgument(CommandContext<FabricClientCommandSource> context) {
        return context.getInput().split("\\s+", 3)[2].split("\\s+\\d+$")[0];
    }
}