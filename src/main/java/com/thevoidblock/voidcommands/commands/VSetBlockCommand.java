package com.thevoidblock.voidcommands.commands;

import com.mojang.brigadier.context.CommandContext;
import dev.xpple.clientarguments.arguments.CBlockPosArgument;
import dev.xpple.clientarguments.arguments.CBlockStateArgument;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import static com.thevoidblock.voidcommands.VoidCommands.CLIENT;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

@Environment(EnvType.CLIENT)
public class VSetBlockCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> dispatcher.register(
                        literal("vsetblock")
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

        assert CLIENT.world != null; // If you are running this command, you should be in a world, right?
        CLIENT.world.setBlockState(blockPos, blockState);
        return 1;
    }
}
