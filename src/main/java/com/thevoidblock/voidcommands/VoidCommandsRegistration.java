package com.thevoidblock.voidcommands;

import com.thevoidblock.voidcommands.commands.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class VoidCommandsRegistration {
    public static void registerAll() {
        registerCommands();
    }

    public static void registerCommands() {
        VGetCommand.register();
        VSetBlockCommand.register();
        VGhostPlacementCommand.register();
        VGetRenderCommand.register();
        VGetLocationCommand.register();
        VSummonCommand.register();
        VKillCommand.register();
    }
}
