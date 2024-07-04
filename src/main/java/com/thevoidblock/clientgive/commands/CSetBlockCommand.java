package com.thevoidblock.clientgive.commands;

import com.mojang.brigadier.context.CommandContext;
import dev.xpple.clientarguments.arguments.CBlockPosArgument;
import dev.xpple.clientarguments.arguments.CBlockStateArgument;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.BlockState;
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
                                        argument("pos", CBlockPosArgument.blockPos())
                                                .then(
                                                        argument("block", CBlockStateArgument.blockState(registryAccess))
                                                                .executes(CSetBlockCommand::setBlock)
        ))));
    }

    private static int setBlock(CommandContext<FabricClientCommandSource> context) {
        BlockPos blockPos = CBlockPosArgument.getBlockPos(context, "pos");
        BlockState blockState = CBlockStateArgument.getBlockState(context, "block").getState();

        CLIENT.world.setBlockState(blockPos, blockState);
        return 1;
    }
}
