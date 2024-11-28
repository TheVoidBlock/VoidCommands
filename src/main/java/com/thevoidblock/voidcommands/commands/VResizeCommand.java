package com.thevoidblock.voidcommands.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static com.thevoidblock.voidcommands.VoidCommands.CLIENT;
import static com.thevoidblock.voidcommands.VoidCommands.COMMAND_PREFIX;
import static java.lang.String.format;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

@Environment(EnvType.CLIENT)
public class VResizeCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> dispatcher.register(
                        literal(format("%sresize", COMMAND_PREFIX)).then(
                                argument(
                                        "width", IntegerArgumentType.integer(0)
                                ).then(
                                        argument(
                                                "height", IntegerArgumentType.integer(0)
                                        ).executes(
                                                VResizeCommand::execute
                                        )
                                )
                        )
                )
        );
    }

    private static int execute(CommandContext<FabricClientCommandSource> context) {
        int width = IntegerArgumentType.getInteger(context, "width");
        int height = IntegerArgumentType.getInteger(context, "height");
        CLIENT.getWindow().setWindowedSize(width, height);
        return 0;
    }
}
