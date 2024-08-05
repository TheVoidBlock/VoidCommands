package com.thevoidblock.voidcommands.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.xpple.clientarguments.arguments.*;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import static com.thevoidblock.voidcommands.VoidCommands.CLIENT;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class VSummonCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> dispatcher.register(
                        literal("vsummon").then(argument(
                                "entity", CResourceArgument.registryEntry(registryAccess, RegistryKeys.ENTITY_TYPE)
                        ).suggests(CSuggestionProviders.SUMMONABLE_ENTITIES).executes(
                            context -> execute(context, context.getSource().getPosition(), new NbtCompound())

                        ).then(argument("location", CVec3Argument.vec3()).executes(
                            context -> execute(context, CVec3Argument.getVec3(context, "location"), new NbtCompound())

                        ).then(argument("nbt", CNbtTagArgument.nbtTag()).executes(
                                context -> execute(context, CVec3Argument.getVec3(context, "location"), (NbtCompound) CNbtTagArgument.getNbtTag(context, "nbt"))
                        ))
        ))));
    }

    private static int execute(CommandContext<FabricClientCommandSource> context, Vec3d pos, NbtCompound nbt) throws CommandSyntaxException {

        RegistryEntry.Reference<EntityType<?>> entityType = CResourceArgument.getEntityType(context, "entity");

        NbtCompound nbtCompound = nbt.copy();
        nbtCompound.putString("id", entityType.registryKey().getValue().toString());

        final Entity processedEntity = EntityType.loadEntityWithPassengers(nbtCompound, CLIENT.world, entity -> {
            entity.refreshPositionAndAngles(pos.x, pos.y, pos.z, entity.getYaw(), entity.getPitch());
            return entity;
        });

        assert CLIENT.world != null;
        CLIENT.world.addEntity(processedEntity);

        assert processedEntity != null;
        context.getSource().sendFeedback(Text.translatable("commands.summon.success", processedEntity.getDisplayName()));

        return 1;
    }
}
