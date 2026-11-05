package com.thevoidblock.voidcommands.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import dev.xpple.clientarguments.arguments.CEntityArgument;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.Optional;

import static com.thevoidblock.voidcommands.VoidCommands.*;
import static com.thevoidblock.voidcommands.VoidCommandsStyler.*;
import static java.lang.String.format;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;

@Environment(EnvType.CLIENT)
public class VGetLocationCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> dispatcher.register(
                        literal(format("%sgetlocation", COMMAND_PREFIX)).executes(
                                context -> execute(context, context.getSource().getEntity())
                        ).then(argument("entity", CEntityArgument.entity()).executes(
                                context -> execute(context, CEntityArgument.getEntity(context, "entity"))
                        ))

        ));
    }

    @SuppressWarnings("resource")
    private static int execute(CommandContext<FabricClientCommandSource> context, Entity entity) {
        final Identifier dimensionIdentifier;

        Level level = entity.level();
        Optional<ResourceKey<DimensionType>> dimensionKey = level.dimensionTypeRegistration().unwrapKey();
        dimensionIdentifier = dimensionKey.orElseThrow().identifier();

        context.getSource().sendFeedback(
                ((MutableComponent)entity.getDisplayName()).withStyle(HEADER_FORMATTING).append("'s ").append(Component.translatable(format("chat.%s.current_location", MOD_ID))).append(": ")
                        .append(formatPlayerPos(
                                entity.position()
                        )).append(" ")
                        .append(Component.translatable(format("chat.%s.location_dimension_separator", MOD_ID)).withStyle(SEPERATOR_FORMATTING)).append(" ")
                        .append(Component.literal(dimensionIdentifier.toString()).withStyle(IDENTIFIER_FORMATTING))
        );

        return Command.SINGLE_SUCCESS;
    }
}
