package com.thevoidblock.voidcommands.commands;

import com.mojang.brigadier.context.CommandContext;
import com.thevoidblock.voidcommands.mixin.GameOptionsAccessor;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static com.thevoidblock.voidcommands.VoidCommands.*;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class VGetRenderCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> dispatcher.register(
                        literal("vgetrender").executes(VGetRenderCommand::execute)
        ));
    }

    private static int execute(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(
                formatKeyValue(String.format("chat.%s.server_simulation_distance", MOD_ID), CLIENT.world.getSimulationDistance()).append("\n")
                        .append(formatKeyValue(String.format("chat.%s.server_view_distance", MOD_ID), ((GameOptionsAccessor)CLIENT.options).getServerViewDistance())).append("\n")
                        .append(formatKeyValue(String.format("chat.%s.client_view_distance", MOD_ID), CLIENT.options.getViewDistance().getValue()))
        );
        return 1;
    }
}
