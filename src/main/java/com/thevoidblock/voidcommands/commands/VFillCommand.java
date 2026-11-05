package com.thevoidblock.voidcommands.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import dev.xpple.clientarguments.arguments.CBlockPosArgument;
import dev.xpple.clientarguments.arguments.CBlockPredicateArgument;
import dev.xpple.clientarguments.arguments.CBlockStateArgument;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

import static com.thevoidblock.voidcommands.VoidCommands.COMMAND_PREFIX;
import static java.lang.String.format;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;

@Environment(EnvType.CLIENT)
public class VFillCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> dispatcher.register(
                        literal(format("%sfill", COMMAND_PREFIX)).then(
                                argument("pos1", CBlockPosArgument.blockPos()).then(
                                    argument("pos2", CBlockPosArgument.blockPos()).then(
                                            argument("blockState", CBlockStateArgument.blockState(registryAccess)).executes(
                                                    context -> execute(context, FillModes.NORMAL, null)
                                            ).then(
                                                    literal("hollow").executes(
                                                            context -> execute(context, FillModes.HOLLOW, null)
                                                    )
                                            ).then(
                                                    literal("keep").executes(
                                                            context -> execute(context, FillModes.KEEP, null)
                                                    )
                                            ).then(
                                                    literal("outline").executes(
                                                            context -> execute(context, FillModes.OUTLINE, null)
                                                    )
                                            ).then(
                                                    literal("replace").then(
                                                            argument("replace", CBlockPredicateArgument.blockPredicate(registryAccess)).executes(
                                                                    context -> execute(context, FillModes.REPLACE, CBlockPredicateArgument.getBlockPredicate(context, "replace"))
                                                            )
                                                    )
                                            )
                                    )
                                )
                        )
                ));
    }

    private static int execute(CommandContext<FabricClientCommandSource> context, FillModes fillMode, @Nullable Predicate<BlockInWorld> replace) {
        BlockState blockState = CBlockStateArgument.getBlockState(context, "blockState").getState();
        BlockPos pos1 = CBlockPosArgument.getBlockPos(context, "pos1");
        BlockPos pos2 = CBlockPosArgument.getBlockPos(context, "pos2");

        fillMode.getFiller().fill(blockState, pos1, pos2, fillMode.getFilter(), replace, context.getSource().getLevel());
        return Command.SINGLE_SUCCESS;
    }

    private enum FillModes {
        NORMAL((block, _, _, _, _, _) -> block, Fillers.CUBE.getFiller()),
        OUTLINE((block, _, _, _, _, _) -> block, Fillers.CUBE_OUTLINE.getFiller()),
        HOLLOW((block, pos, pos1, pos2, _, _) -> {
            BoundingBox range = getRange(pos1, pos2);
            return pos.getX() == range.minX()
                    || pos.getX() == range.maxX()
                    || pos.getY() == range.minY()
                    || pos.getY() == range.maxY()
                    || pos.getZ() == range.minZ()
                    || pos.getZ() == range.maxZ()
                    ? block
                    : Blocks.AIR.defaultBlockState();
        }, Fillers.CUBE.getFiller()),
        KEEP((block, pos, _, _, _, level) -> level.getBlockState(pos).is(BlockTags.AIR) ? block : null, Fillers.CUBE.getFiller()),
        REPLACE(
                (block, pos, _, _, replace, level) -> {
                    if(replace != null) {
                        if(replace.test(new BlockInWorld(level, pos, false)))
                            return block;
                        else
                            return null;
                    } else
                        return null;
                },
                Fillers.CUBE.getFiller()
        );

        private final FillFilter filter;
        private final Filler filler;

        FillModes(FillFilter filter, Filler filler) {
            this.filter = filter;
            this.filler = filler;
        }

        public Filler getFiller() {
            return this.filler;
        }

        public FillFilter getFilter() {
            return this.filter;
        }
    }

    private enum Fillers {
        CUBE((block, pos1, pos2, filter, replace, level) ->
                BlockPos.betweenClosed(pos1, pos2).forEach(pos ->
                        setFilteredBlock(block, pos, pos1, pos2, replace, filter, level)
                )
        ),
        CUBE_OUTLINE((block, pos1, pos2, filter, replace, level) -> {
            BoundingBox range = getRange(pos1, pos2);

            for(int x = range.minX(); x <= range.maxX(); x++)
                for(int y = range.minY(); y <= range.maxY(); y++) {
                    int z = range.minZ();
                    setFilteredBlock(block, new BlockPos(x, y, z), pos1, pos2, replace, filter, level);
                }

            for(int x = range.minX(); x <= range.maxX(); x++)
                for(int y = range.minY(); y <= range.maxY(); y++) {
                    int z = range.maxZ();
                    setFilteredBlock(block, new BlockPos(x, y, z), pos1, pos2, replace, filter, level);
                }

            for(int x = range.minX(); x <= range.maxX(); x++)
                for(int z = range.minZ(); z <= range.maxZ(); z++) {
                    int y = range.minY();
                    setFilteredBlock(block, new BlockPos(x, y, z), pos1, pos2, replace, filter, level);
                }

            for(int x = range.minX(); x <= range.maxX(); x++)
                for(int z = range.minZ(); z <= range.maxZ(); z++) {
                    int y = range.maxY();
                    setFilteredBlock(block, new BlockPos(x, y, z), pos1, pos2, replace, filter, level);
                }

            for(int y = range.minY(); y <= range.maxY(); y++)
                for(int z = range.minZ(); z <= range.maxZ(); z++) {
                    int x = range.minX();
                    setFilteredBlock(block, new BlockPos(x, y, z), pos1, pos2, replace, filter, level);
                }

            for(int y = range.minY(); y <= range.maxY(); y++)
                for(int z = range.minZ(); z <= range.maxZ(); z++) {
                    int x = range.maxX();
                    setFilteredBlock(block, new BlockPos(x, y, z), pos1, pos2, replace, filter, level);
                }
        });

        private final Filler filler;

        Fillers(Filler filler) {
            this.filler = filler;
        }

        public Filler getFiller() {
            return this.filler;
        }
    }

    @FunctionalInterface
    private interface FillFilter {
        BlockState filter(BlockState block, BlockPos pos, BlockPos pos1, BlockPos pos2, @Nullable Predicate<BlockInWorld> replace, ClientLevel level);
    }

    @FunctionalInterface
    private interface Filler {
        void fill(BlockState block, BlockPos pos1, BlockPos pos2, FillFilter filter, @Nullable Predicate<BlockInWorld> replace, ClientLevel level);
    }

    private static void setFilteredBlock(BlockState block, BlockPos pos, BlockPos pos1, BlockPos pos2, @Nullable Predicate<BlockInWorld> replace, FillFilter filter, ClientLevel level) {
        BlockState filteredBlock = filter.filter(block, pos, pos1, pos2, replace, level);
        if (filteredBlock != null) {
            level.setBlockAndUpdate(pos, filteredBlock);
        }
    }

    private static BoundingBox getRange(BlockPos pos1, BlockPos pos2) {
        return new BoundingBox(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ());
    }
}
