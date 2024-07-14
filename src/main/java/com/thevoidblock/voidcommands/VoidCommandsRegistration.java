package com.thevoidblock.voidcommands;

import com.thevoidblock.voidcommands.commands.VGetCommand;
import com.thevoidblock.voidcommands.commands.VGetRenderCommand;
import com.thevoidblock.voidcommands.commands.VGhostPlacementCommand;
import com.thevoidblock.voidcommands.commands.VSetBlockCommand;
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
    }
}
