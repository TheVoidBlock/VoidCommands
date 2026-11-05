package com.thevoidblock.voidcommands.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.thevoidblock.voidcommands.VoidCommandsStyler;
import com.thevoidblock.voidcommands.mixin.GameOptionsAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

import static com.thevoidblock.voidcommands.VoidCommands.*;
import static com.thevoidblock.voidcommands.VoidCommandsStyler.formatTranslatableNumber;
import static java.lang.String.format;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

@Environment(EnvType.CLIENT)
public class VGetRenderCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> dispatcher.register(
                        literal(format("%sgetrender", COMMAND_PREFIX)).executes(VGetRenderCommand::execute)
        ));
    }

    private static int execute(CommandContext<FabricClientCommandSource> context) {
        assert CLIENT.level != null;
        context.getSource().sendFeedback(
                Component.translatable(String.format("chat.%s.render_info_header", MOD_ID)).withStyle(VoidCommandsStyler.HEADER_FORMATTING).append("\n")
                        .append(formatTranslatableNumber(String.format("chat.%s.server_simulation_distance", MOD_ID), CLIENT.level.getServerSimulationDistance())).append("\n")
                        .append(formatTranslatableNumber(String.format("chat.%s.server_view_distance", MOD_ID), ((GameOptionsAccessor)CLIENT.options).getServerViewDistance())).append("\n")
                        .append(formatTranslatableNumber(String.format("chat.%s.client_view_distance", MOD_ID), CLIENT.options.renderDistance().get())).append("\n")
                        .append(formatTranslatableNumber(String.format("chat.%s.clamped_view_distance", MOD_ID), CLIENT.options.getEffectiveRenderDistance()))
        );

        return Command.SINGLE_SUCCESS;
    }
}
