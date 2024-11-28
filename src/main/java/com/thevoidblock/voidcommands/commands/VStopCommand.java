package com.thevoidblock.voidcommands.commands;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static com.thevoidblock.voidcommands.VoidCommands.CLIENT;
import static com.thevoidblock.voidcommands.VoidCommands.COMMAND_PREFIX;
import static java.lang.String.format;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

@Environment(EnvType.CLIENT)
public class VStopCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> dispatcher.register(
                        literal(format("%sstop", COMMAND_PREFIX)).executes(
                                VStopCommand::execute
                        )
                )
        );
    }

    private static int execute(CommandContext<FabricClientCommandSource> context) {
        CLIENT.scheduleStop();
        return 0;
    }
}
