package com.thevoidblock.voidcommands.commands;

import com.mojang.brigadier.context.CommandContext;
import dev.xpple.clientarguments.arguments.CBlockPosArgumentType;
import dev.xpple.clientarguments.arguments.CBlockStateArgumentType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import static com.thevoidblock.voidcommands.VoidCommands.CLIENT;
import static com.thevoidblock.voidcommands.VoidCommands.MOD_ID;
import static java.lang.String.format;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

@Environment(EnvType.CLIENT)
public class VSetBlockCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> dispatcher.register(
                        literal("vsetblock")
                                .then(
                                        argument("pos", CBlockPosArgumentType.blockPos())
                                                .then(
                                                        argument("block", CBlockStateArgumentType.blockState(registryAccess))
                                                                .executes(VSetBlockCommand::setBlock)
        ))));
    }

    private static int setBlock(CommandContext<FabricClientCommandSource> context) {
        BlockPos blockPos = CBlockPosArgumentType.getCBlockPos(context, "pos");
        BlockState blockState = CBlockStateArgumentType.getCBlockState(context, "block").getBlockState();

        assert CLIENT.world != null; // If you are running this command, you should be in a world, right?
        CLIENT.world.setBlockState(blockPos, blockState);

        context.getSource().sendFeedback(
                Text.translatable(format("chat.%s.setblock", MOD_ID))
                        .append(" ")
                        .append(blockPos.toShortString())
        );

        return 1;
    }
}
