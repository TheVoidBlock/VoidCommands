package com.thevoidblock.voidcommands.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.thevoidblock.voidcommands.mixin.SummonCommandAccessor;
import dev.xpple.clientarguments.arguments.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import static com.thevoidblock.voidcommands.VoidCommands.COMMAND_PREFIX;
import static java.lang.String.format;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;

@Environment(EnvType.CLIENT)
public class VSummonCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, buildContext) -> dispatcher.register(
                        literal(format("%ssummon", COMMAND_PREFIX)).then(argument(
                                "entity", CResourceArgument.resource(buildContext, Registries.ENTITY_TYPE)
                        ).suggests(CSuggestionProviders.SUMMONABLE_ENTITIES).executes(
                            context -> execute(context, context.getSource().getPosition(), new CompoundTag())

                        ).then(argument("location", CVec3Argument.vec3()).executes(
                            context -> execute(context, CVec3Argument.getVec3(context, "location"), new CompoundTag())

                        ).then(argument("nbt", CNbtTagArgument.nbtTag()).executes(
                                context -> execute(context, CVec3Argument.getVec3(context, "location"), (CompoundTag) CNbtTagArgument.getNbtTag(context, "nbt"))
                        ))
        ))));
    }

    private static int execute(CommandContext<FabricClientCommandSource> context, Vec3 pos, CompoundTag nbt) throws CommandSyntaxException {

        Holder.Reference<EntityType<?>> entityType = CResourceArgument.getEntityType(context, "entity");

        CompoundTag nbtCompound = nbt.copy();
        nbtCompound.putString("id", entityType.key().identifier().toString());

        ClientLevel level = context.getSource().getLevel();

        final Entity processedEntity = EntityType.loadEntityRecursive(nbtCompound, level, EntitySpawnReason.COMMAND, entity -> {
            entity.snapTo(pos.x, pos.y, pos.z, entity.getYRot(), entity.getXRot());
            return entity;
        });

        if(processedEntity == null) {
            throw SummonCommandAccessor.getERROR_FAILED().create();
        }

        level.addEntity(processedEntity);

        context.getSource().sendFeedback(Component.translatable("commands.summon.success", processedEntity.getDisplayName()));

        return Command.SINGLE_SUCCESS;
    }
}
