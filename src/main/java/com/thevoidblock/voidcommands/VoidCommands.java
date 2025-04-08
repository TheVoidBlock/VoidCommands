package com.thevoidblock.voidcommands;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

import static com.thevoidblock.voidcommands.VoidCommandsStyler.IDENTIFIER_FORMATTING;

@Environment(EnvType.CLIENT)
public class VoidCommands implements ClientModInitializer {

    public static final String MOD_ID = "voidcommands";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    public static final char COMMAND_PREFIX = 'v';

    @Override
    public void onInitializeClient() {
        VoidCommandsRegistration.registerAll();

        LOGGER.info("{} initialized!", MOD_ID);
    }

    public static void notifyModuleState(Text module, boolean state) {
        Text message = ((MutableText)module).formatted(IDENTIFIER_FORMATTING).append(Text.literal(" "))
                .append(Text.translatable(String.format(state ? "chat.%s.state_on" : "chat.%s.state_off", MOD_ID))
                        .withColor(TempConfig.ghostPlacement ? Color.GREEN.getRGB() : Color.RED.getRGB())
                )
        ;

        if (CLIENT.player != null) {
            CLIENT.player.sendMessage(message, false);
        } else {
            LOGGER.info(message.getString());
        }
    }
}
