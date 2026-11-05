package com.thevoidblock.voidcommands;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

import static com.thevoidblock.voidcommands.VoidCommandsStyler.IDENTIFIER_FORMATTING;

@Environment(EnvType.CLIENT)
public class VoidCommands implements ClientModInitializer {

    public static final String MOD_ID = "voidcommands";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Minecraft CLIENT = Minecraft.getInstance();
    public static final char COMMAND_PREFIX = 'v';

    @Override
    public void onInitializeClient() {
        VoidCommandsRegistration.registerAll();

        LOGGER.info("{} initialized!", MOD_ID);
    }

    public static void notifyModuleState(Component module, boolean state) {
        Component message = ((MutableComponent)module).withStyle(IDENTIFIER_FORMATTING).append(Component.literal(" "))
                .append(Component.translatable(String.format(state ? "chat.%s.state_on" : "chat.%s.state_off", MOD_ID))
                        .withColor(TempConfig.ghostPlacement ? Color.GREEN.getRGB() : Color.RED.getRGB())
                );

        CLIENT.gui.getChat().addClientSystemMessage(message);
    }
}
