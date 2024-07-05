package com.thevoidblock.voidcommands.commands;

import com.mojang.brigadier.context.CommandContext;
import com.thevoidblock.voidcommands.TempConfig;
import dev.xpple.clientarguments.arguments.CEnumArgument;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import com.thevoidblock.voidcommands.util.BooleanToggles;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import java.awt.*;

import static com.thevoidblock.voidcommands.VoidCommands.CLIENT;
import static com.thevoidblock.voidcommands.VoidCommands.MOD_ID;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class VGhostPlacementCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> dispatcher.register(
                        literal("vghostplacement")
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

        assert CLIENT.player != null;
        CLIENT.player.sendMessage(
                Text.translatable(String.format("chat.%s.ghost_placement_toggle", MOD_ID)).withColor(Color.WHITE.getRGB()).append(Text.literal(" "))
                        .append(Text.translatable(String.format(TempConfig.ghostPlacement ? "chat.%s.toggle_on" : "chat.%s.toggle_off", MOD_ID))
                                .withColor(TempConfig.ghostPlacement ? Color.GREEN.getRGB() : Color.RED.getRGB())
        ), true);
        return 1;
    }
}
