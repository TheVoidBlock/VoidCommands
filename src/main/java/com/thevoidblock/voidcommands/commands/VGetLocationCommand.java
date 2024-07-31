package com.thevoidblock.voidcommands.commands;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import static com.thevoidblock.voidcommands.VoidCommands.CLIENT;
import static com.thevoidblock.voidcommands.VoidCommands.MOD_ID;
import static com.thevoidblock.voidcommands.VoidCommandsStyler.*;
import static java.lang.String.format;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class VGetLocationCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> dispatcher.register(
                        literal("vgetlocation")
                                .executes(VGetLocationCommand::execute)
        ));
    }

    private static int execute(CommandContext<FabricClientCommandSource> context) {

        assert CLIENT.world != null;
        final Identifier dimension = CLIENT.world.getDimensionEntry().getKey().get().getValue();

        assert CLIENT.player != null;
        context.getSource().sendFeedback(
                Text.translatable(format("chat.%s.current_location", MOD_ID)).formatted(HEADER_FORMATTING).append(": ")
                        .append(formatPlayerPos(
                                new Vec3d(
                                        CLIENT.player.getPos().x,
                                        CLIENT.player.getPos().y,
                                        CLIENT.player.getPos().z
                                )
                        )).append(" ")
                        .append(Text.translatable(format("chat.%s.location_dimension_separator", MOD_ID)).formatted(SEPERATOR_FORMATTING)).append(" ")
                        .append(Text.literal(dimension.toString()).formatted(IDENTIFIER_FORMATTING))
        );
        return 1;
    }
}
