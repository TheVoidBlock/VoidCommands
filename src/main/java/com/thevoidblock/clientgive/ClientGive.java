package com.thevoidblock.clientgive;

import com.thevoidblock.clientgive.commands.CGetCommand;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientGive implements ClientModInitializer {

    public static final String MOD_ID = "clientgive";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    @Override
    public void onInitializeClient() {
        CGetCommand.register();
        LOGGER.info("{} initialized!", MOD_ID);
    }
}
