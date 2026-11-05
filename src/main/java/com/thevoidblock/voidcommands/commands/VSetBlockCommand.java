package com.thevoidblock.voidcommands.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import dev.xpple.clientarguments.arguments.CBlockPosArgument;
import dev.xpple.clientarguments.arguments.CBlockStateArgument;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

import static com.thevoidblock.voidcommands.VoidCommands.*;
import static java.lang.String.format;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;

@Environment(EnvType.CLIENT)
public class VSetBlockCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> dispatcher.register(
                        literal(format("%ssetblock", COMMAND_PREFIX))
                                .then(
                                        argument("pos", CBlockPosArgument.blockPos())
                                                .then(
                                                        argument("block", CBlockStateArgument.blockState(registryAccess))
                                                                .executes(VSetBlockCommand::setBlock)
        ))));
    }

    private static int setBlock(CommandContext<FabricClientCommandSource> context) {
        BlockPos blockPos = CBlockPosArgument.getBlockPos(context, "pos");
        BlockState blockState = CBlockStateArgument.getBlockState(context, "block").getState();

        assert CLIENT.level != null;
        CLIENT.level.setBlockAndUpdate(blockPos, blockState);

        context.getSource().sendFeedback(
                Component.translatable(format("chat.%s.setblock", MOD_ID))
                        .append(" ")
                        .append(blockPos.toShortString())
        );

        return Command.SINGLE_SUCCESS;
    }
}
