package com.thevoidblock.voidcommands.commands;

import com.mojang.brigadier.context.CommandContext;
import dev.xpple.clientarguments.arguments.CEntityArgument;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import static com.thevoidblock.voidcommands.VoidCommands.CLIENT;
import static com.thevoidblock.voidcommands.VoidCommands.MOD_ID;
import static com.thevoidblock.voidcommands.VoidCommandsStyler.*;
import static java.lang.String.format;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class VGetLocationCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> dispatcher.register(
                        literal("vgetlocation").executes(
                                context -> execute(context, context.getSource().getEntity())
                        ).then(argument("entity", CEntityArgument.entity()).executes(
                                context -> execute(context, CEntityArgument.getEntity(context, "entity"))
                        ))

        ));
    }

    private static int execute(CommandContext<FabricClientCommandSource> context, Entity entity) {

        final Identifier dimension = entity.getWorld().getDimensionEntry().getKey().get().getValue();

        assert entity.getDisplayName() != null;
        context.getSource().sendFeedback(
                ((MutableText)entity.getDisplayName()).formatted(HEADER_FORMATTING).append("'s ").append(Text.translatable(format("chat.%s.current_location", MOD_ID))).append(": ")
                        .append(formatPlayerPos(
                                entity.getPos()
                        )).append(" ")
                        .append(Text.translatable(format("chat.%s.location_dimension_separator", MOD_ID)).formatted(SEPERATOR_FORMATTING)).append(" ")
                        .append(Text.literal(dimension.toString()).formatted(IDENTIFIER_FORMATTING))
        );
        return 1;
    }
}
