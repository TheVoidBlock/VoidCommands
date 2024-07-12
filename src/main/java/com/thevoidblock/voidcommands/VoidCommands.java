package com.thevoidblock.voidcommands;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VoidCommands implements ClientModInitializer {

    public static final String MOD_ID = "voidcommands";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    @Override
    public void onInitializeClient() {
        VoidCommandsRegistration.registerAll();

        LOGGER.info("{} initialized!", MOD_ID);
    }

    public static <T extends Number> MutableText formatKeyValue(String key, T value) {
        return
                Text.translatable(key).formatted(Formatting.AQUA)
                        .append(Text.literal(": ").formatted(Formatting.WHITE))
                        .append(Text.literal(value.toString()).formatted(Formatting.GOLD))
                ;
    }
}
