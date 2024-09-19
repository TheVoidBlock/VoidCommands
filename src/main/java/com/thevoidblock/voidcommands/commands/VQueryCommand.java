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
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;

import javax.swing.text.html.parser.Entity;
import java.util.Collection;

import static com.thevoidblock.voidcommands.VoidCommands.CLIENT;
import static com.thevoidblock.voidcommands.VoidCommandsStyler.ERROR_FORMATTING;
import static java.lang.String.format;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;
import static net.minecraft.SharedConstants.CHUNK_WIDTH;
import static net.minecraft.text.Text.translatable;
import static java.lang.Math.*;

@Environment(EnvType.CLIENT)
public class VQueryCommand {

    private static final String ENTITY_QUERY_NAME = "entities";
    private static final String BLOCK_QUERY_NAME = "blocks";

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> dispatcher.register(
                        literal("vquery").then(
                                literal(ENTITY_QUERY_NAME).then(
                                        argument("entities", CEntityArgument.entities()).executes(
                                                context -> executeEntities(context, CLIENT.options.getClampedViewDistance())
                                        ).then(
                                                argument("distance", IntegerArgumentType.integer(0, CLIENT.options.getClampedViewDistance())).executes(
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
                                                        CLIENT.options.getClampedViewDistance()
                                                )
                                        ).then(
                                                argument("distance", IntegerArgumentType.integer(0, CLIENT.options.getClampedViewDistance())).executes(
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
        Vec3d sourcePos = context.getSource().getPosition();
        try {
            queryCount = CEntityArgument.getEntities(context, "entities").stream()
                    .filter(entity ->
                        pow((int)floor(entity.getPos().x/16) - (int)floor(sourcePos.x/16), 2) + pow((int)floor(entity.getPos().z/16) - (int)floor(sourcePos.z/16), 2) <= pow(distance, 2)
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
                    ).formatted(ERROR_FORMATTING)
            );

        return queryCount;
    }

    private static int executeBlocks(CommandContext<FabricClientCommandSource> context, BlockState blockState, int distance) {

        int queryCount = 0;

        assert CLIENT.world != null;
        final Vec3d sourcePos = context.getSource().getPosition();
        for(ChunkPos chunkPos : ChunkTracker.loadedChunks) {
            if(pow(chunkPos.x - (int)floor(sourcePos.x/16), 2) + pow(chunkPos.z - (int)floor(sourcePos.z/16), 2) <= pow(distance, 2))
                for (int x = 0; x < CHUNK_WIDTH; x++) {
                    for (int y = CLIENT.world.getBottomY(); y < CLIENT.world.getHeight(); y++)
                        for (int z = 0; z < CHUNK_WIDTH; z++) {
                            BlockPos blockPos = new BlockPos(chunkPos.x*CHUNK_WIDTH + x, y, chunkPos.z*CHUNK_WIDTH + z);
                            if(CLIENT.world.getBlockState(blockPos) == blockState) queryCount++;
                        }
            }
        }

        if(queryCount != 0) context.getSource().sendFeedback(getFeedback(context, queryCount, distance));
        else context.getSource().sendFeedback(
                translatable(
                        "chat.voidcommands.block_not_found",
                        getQueryArgument(context),
                        distance
                ).formatted(ERROR_FORMATTING)
        );
        return queryCount;
    }

    private static Text getFeedback(CommandContext<FabricClientCommandSource> context, int count, int distance) {
        return translatable(
                "chat.voidcommands.query_found",
                count,
                getQueryArgument(context),
                distance
        );
    }

    private static String getQueryArgument(CommandContext<FabricClientCommandSource> context) {
        return context.getInput().split("\s+", 3)[2].split("\\s+\\d+$")[0];
    }
}