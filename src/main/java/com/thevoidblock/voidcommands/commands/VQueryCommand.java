package com.thevoidblock.voidcommands.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.thevoidblock.voidcommands.ChunkTracker;
import dev.xpple.clientarguments.arguments.CBlockStateArgument;
import dev.xpple.clientarguments.arguments.CEntityArgument;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.thevoidblock.voidcommands.VoidCommands.CLIENT;
import static com.thevoidblock.voidcommands.VoidCommands.COMMAND_PREFIX;
import static com.thevoidblock.voidcommands.VoidCommandsStyler.ERROR_FORMATTING;
import static java.lang.String.format;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.*;
import static net.minecraft.SharedConstants.WORLD_RESOLUTION;
import static net.minecraft.network.chat.Component.translatable;
import static java.lang.Math.*;

@Environment(EnvType.CLIENT)
public class VQueryCommand {

    private static final String ENTITY_QUERY_NAME = "entities";
    private static final String BLOCK_QUERY_NAME = "blocks";

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> dispatcher.register(
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
                                        argument("blocks", CBlockStateArgument.blockState(registryAccess)).executes(
                                                context -> executeBlocks(
                                                        context,
                                                        CBlockStateArgument.getBlockState(context, "blocks").getState(),
                                                        CLIENT.options.getEffectiveRenderDistance()
                                                )
                                        ).then(
                                                argument("distance", IntegerArgumentType.integer(0, CLIENT.options.getEffectiveRenderDistance())).executes(
                                                        context -> executeBlocks(
                                                                context,
                                                                CBlockStateArgument.getBlockState(context, "blocks").getState(),
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

    private static int executeBlocks(CommandContext<FabricClientCommandSource> context, BlockState blockState, int distance) {
        AtomicInteger queryCount = new AtomicInteger(0);

        ClientLevel level = context.getSource().getLevel();
        final Vec3 sourcePos = context.getSource().getPosition();

        try (ExecutorService threadPool = Executors.newFixedThreadPool(ChunkTracker.loadedChunks.size())) {
            for(ChunkPos chunkPos : ChunkTracker.loadedChunks) {
                threadPool.submit(() -> {
                    if (pow(chunkPos.x() - (int) floor(sourcePos.x / 16), 2) + pow(chunkPos.z() - (int) floor(sourcePos.z / 16), 2) <= pow(distance, 2)) {
                        for (int x = 0; x < WORLD_RESOLUTION; x++) {
                            for (int y = level.getMinY(); y < level.getHeight(); y++) {
                                for (int z = 0; z < WORLD_RESOLUTION; z++) {
                                    BlockPos blockPos = new BlockPos(chunkPos.x() * WORLD_RESOLUTION + x, y, chunkPos.z() * WORLD_RESOLUTION + z);
                                    if (level.getBlockState(blockPos) == blockState) {
                                        queryCount.incrementAndGet();
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }

        if(queryCount.get() != 0) context.getSource().sendFeedback(getFeedback(context, queryCount.get(), distance));
        else context.getSource().sendFeedback(
                translatable(
                        "chat.voidcommands.block_not_found",
                        getQueryArgument(context),
                        distance
                ).withStyle(ERROR_FORMATTING)
        );
        return queryCount.get();
    }

    private static Component getFeedback(CommandContext<FabricClientCommandSource> context, int count, int distance) {
        return translatable(
                "chat.voidcommands.query_found",
                count,
                getQueryArgument(context),
                distance
        );
    }

    private static String getQueryArgument(CommandContext<FabricClientCommandSource> context) {
        return context.getInput().split("\\s+", 3)[2].split("\\s+\\d+$")[0];
    }
}