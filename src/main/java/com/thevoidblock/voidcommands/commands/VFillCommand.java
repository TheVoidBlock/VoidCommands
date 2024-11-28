package com.thevoidblock.voidcommands.commands;

import com.mojang.brigadier.context.CommandContext;
import dev.xpple.clientarguments.arguments.CBlockPosArgument;
import dev.xpple.clientarguments.arguments.CBlockPredicateArgument;
import dev.xpple.clientarguments.arguments.CBlockStateArgument;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.function.Predicate;

import static com.thevoidblock.voidcommands.VoidCommands.CLIENT;
import static com.thevoidblock.voidcommands.VoidCommands.COMMAND_PREFIX;
import static java.lang.String.format;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

@Environment(EnvType.CLIENT)
public class VFillCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> dispatcher.register(
                        literal(format("%sfill", COMMAND_PREFIX)).then(
                                argument("pos1", CBlockPosArgument.blockPos()).then(
                                    argument("pos2", CBlockPosArgument.blockPos()).then(
                                            argument("blockState", CBlockStateArgument.blockState(registryAccess)).executes(
                                                    context -> execute(context, FillModes.NORMAL, Optional.empty())
                                            ).then(
                                                    literal("hollow").executes(
                                                            context -> execute(context, FillModes.HOLLOW, Optional.empty())
                                                    )
                                            ).then(
                                                    literal("keep").executes(
                                                            context -> execute(context, FillModes.KEEP, Optional.empty())
                                                    )
                                            ).then(
                                                    literal("outline").executes(
                                                            context -> execute(context, FillModes.OUTLINE, Optional.empty())
                                                    )
                                            ).then(
                                                    literal("replace").then(
                                                            argument("replace", CBlockPredicateArgument.blockPredicate(registryAccess)).executes(
                                                                    context -> execute(
                                                                            context,
                                                                            FillModes.REPLACE, Optional.of(
                                                                                    CBlockPredicateArgument.getBlockPredicate(context, "replace")
                                                                            )
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                                )
                        )
                ));
    }

    private static int execute(CommandContext<FabricClientCommandSource> context, FillModes fillMode, Optional<Predicate<CachedBlockPosition>> replace) {
        BlockState blockState = CBlockStateArgument.getBlockState(context, "blockState").getState();
        BlockPos pos1 = CBlockPosArgument.getBlockPos(context, "pos1");
        BlockPos pos2 = CBlockPosArgument.getBlockPos(context, "pos2");

        fillMode.getFiller().fill(blockState, pos1, pos2, fillMode.getFilter(), replace);
        return 0;
    }

    private enum FillModes {
        NORMAL((block, pos, pos1, pos2, replace) -> block, Fillers.CUBE.getFiller()),
        OUTLINE((block, pos, pos1, pos2, replace) -> block, Fillers.CUBE_OUTLINE.getFiller()),
        HOLLOW((block, pos, pos1, pos2, replace) -> {
            BlockBox range = getRange(pos1, pos2);
            return pos.getX() == range.getMinX()
                    || pos.getX() == range.getMaxX()
                    || pos.getY() == range.getMinY()
                    || pos.getY() == range.getMaxY()
                    || pos.getZ() == range.getMinZ()
                    || pos.getZ() == range.getMaxZ()
                    ? block
                    : Blocks.AIR.getDefaultState();
        }, Fillers.CUBE.getFiller()),
        KEEP((block, pos, pos1, pos2, replace) -> {
            assert CLIENT.world != null;
            return CLIENT.world.getBlockState(pos).isIn(BlockTags.AIR) ? block : null;
        }, Fillers.CUBE.getFiller()),
        REPLACE(
                (block, pos, pos1, pos2, replace) -> {
                    if(replace.isPresent()) {
                        if(replace.get().test(new CachedBlockPosition(CLIENT.world, pos, false)))
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

        CUBE((block, pos1, pos2, filter, replace) ->
                BlockPos.iterate(pos1, pos2).forEach(pos ->
                        setFilteredBlock(block, pos, pos1, pos2, replace, filter)
                )
        ),
        CUBE_OUTLINE((block, pos1, pos2, filter, replace) -> {
            BlockBox range = getRange(pos1, pos2);

            for(int x = range.getMinX(); x <= range.getMaxX(); x++)
                for(int y = range.getMinY(); y <= range.getMaxY(); y++) {
                    int z = range.getMinZ();
                    setFilteredBlock(block, new BlockPos(x, y, z), pos1, pos2, replace, filter);
                }

            for(int x = range.getMinX(); x <= range.getMaxX(); x++)
                for(int y = range.getMinY(); y <= range.getMaxY(); y++) {
                    int z = range.getMaxZ();
                    setFilteredBlock(block, new BlockPos(x, y, z), pos1, pos2, replace, filter);
                }

            for(int x = range.getMinX(); x <= range.getMaxX(); x++)
                for(int z = range.getMinZ(); z <= range.getMaxZ(); z++) {
                    int y = range.getMinY();
                    setFilteredBlock(block, new BlockPos(x, y, z), pos1, pos2, replace, filter);
                }

            for(int x = range.getMinX(); x <= range.getMaxX(); x++)
                for(int z = range.getMinZ(); z <= range.getMaxZ(); z++) {
                    int y = range.getMaxY();
                    setFilteredBlock(block, new BlockPos(x, y, z), pos1, pos2, replace, filter);
                }

            for(int y = range.getMinY(); y <= range.getMaxY(); y++)
                for(int z = range.getMinZ(); z <= range.getMaxZ(); z++) {
                    int x = range.getMinX();
                    setFilteredBlock(block, new BlockPos(x, y, z), pos1, pos2, replace, filter);
                }

            for(int y = range.getMinY(); y <= range.getMaxY(); y++)
                for(int z = range.getMinZ(); z <= range.getMaxZ(); z++) {
                    int x = range.getMaxX();
                    setFilteredBlock(block, new BlockPos(x, y, z), pos1, pos2, replace, filter);
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
        BlockState filter(BlockState block, BlockPos pos, BlockPos pos1, BlockPos pos2, Optional<Predicate<CachedBlockPosition>> replace);
    }

    @FunctionalInterface
    private interface Filler {
        void fill(BlockState block, BlockPos pos1, BlockPos pos2, FillFilter filter, Optional<Predicate<CachedBlockPosition>> replace);
    }

    private static void setFilteredBlock(BlockState block, BlockPos pos, BlockPos pos1, BlockPos pos2, Optional<Predicate<CachedBlockPosition>> replace, FillFilter filter) {
        BlockState filteredBlock = filter.filter(block, pos, pos1, pos2, replace);
        assert CLIENT.world != null;
        if (filteredBlock != null)
            CLIENT.world.setBlockState(pos, filteredBlock);
    }

    private static BlockBox getRange(BlockPos pos1, BlockPos pos2) {
        return new BlockBox(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ());
    }
}
