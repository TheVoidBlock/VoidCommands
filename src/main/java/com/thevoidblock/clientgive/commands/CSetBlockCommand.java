package com.thevoidblock.clientgive.commands;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;

import static com.thevoidblock.clientgive.ClientGive.CLIENT;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class CSetBlockCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> dispatcher.register(
                        literal("csetblock")
                                .then(
                                        argument("pos", BlockPosArgumentType.blockPos())
                                                .then(
                                                        argument("block", BlockStateArgumentType.blockState(registryAccess))
                                                                .executes(CSetBlockCommand::setblock)
                                ))));
    }

    private static int setblock(CommandContext<FabricClientCommandSource> context) {
        BlockPos blockPos = context.getArgument("pos", PosArgument.class).toAbsoluteBlockPos((ServerCommandSource) context.getSource());
        BlockState blockState = context.getArgument("block", BlockStateArgument.class).getBlockState();

        CLIENT.world.setBlockState(blockPos, blockState);
        return 1;
    }
}
