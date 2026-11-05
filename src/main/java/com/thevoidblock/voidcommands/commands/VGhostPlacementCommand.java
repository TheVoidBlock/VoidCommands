package com.thevoidblock.voidcommands.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.thevoidblock.voidcommands.TempConfig;
import com.thevoidblock.voidcommands.VoidCommands;
import dev.xpple.clientarguments.arguments.CEnumArgument;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import com.thevoidblock.voidcommands.util.BooleanToggles;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

import static com.thevoidblock.voidcommands.VoidCommands.*;
import static java.lang.String.format;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;

@Environment(EnvType.CLIENT)
public class VGhostPlacementCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> dispatcher.register(
                        literal(format("%sghostplacement", COMMAND_PREFIX))
                                .then(argument("toggle", CEnumArgument.enumArg(BooleanToggles.class))
                                        .executes(VGhostPlacementCommand::ghostPlacementToggle)
        )));
    }

    private static int ghostPlacementToggle(CommandContext<FabricClientCommandSource> context) {

        BooleanToggles toggle = CEnumArgument.getEnum(context, "toggle");
        switch (toggle) {
            case BooleanToggles.ON -> TempConfig.ghostPlacement = true;
            case BooleanToggles.OFF -> TempConfig.ghostPlacement = false;
            case BooleanToggles.TOGGLE -> TempConfig.ghostPlacement = !TempConfig.ghostPlacement;
        }

        VoidCommands.notifyModuleState(
                Component.translatable(String.format("chat.%s.ghost_placement_toggle", MOD_ID)),
                TempConfig.ghostPlacement
        );

        return Command.SINGLE_SUCCESS;
    }
}
