package com.thevoidblock.voidcommands.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
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
import net.minecraft.world.entity.EntitySpawnRequest;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

import static com.thevoidblock.voidcommands.VoidCommands.COMMAND_PREFIX;
import static java.lang.String.format;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;

@Environment(EnvType.CLIENT)
public class VSummonCommand {
    private static final int ID_ASSIGNMENT_ATTEMPT_LIMIT = 1_000;
    private static final SimpleCommandExceptionType ID_ASSIGNMENT_FAILED_EXCEPTION = new SimpleCommandExceptionType(Component.translatable("command.voidcommands.vsummon.failed.id", ID_ASSIGNMENT_ATTEMPT_LIMIT));

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
        Holder.Reference<EntityType<?>> entityType = CResourceArgument.getSummonableEntityType(context, "entity");

        CompoundTag entityTag = nbt.copy();
        entityTag.putString("id", entityType.key().identifier().toString());

        ClientLevel level = context.getSource().getLevel();

        final Entity entity = EntityType.loadEntityRecursive(entityTag, level, new EntitySpawnRequest(EntitySpawnReason.COMMAND, false), processingEntity -> {
            processingEntity.snapTo(pos.x, pos.y, pos.z, processingEntity.getYRot(), processingEntity.getXRot());
            return processingEntity;
        });

        if(entity == null) {
            throw SummonCommandAccessor.getERROR_FAILED().create();
        }

        Random random = new Random();

        int id;
        int attempt = 0;
        while(true) {
            id = random.nextInt(Integer.MIN_VALUE, 0);

            if(level.getEntity(id) == null) {
                break;
            }

            ++attempt;

            if(attempt > ID_ASSIGNMENT_ATTEMPT_LIMIT) {
                throw ID_ASSIGNMENT_FAILED_EXCEPTION.create();
            }
        }
        // yes I know this is terrible
        // no I don't know a better way to do this

        entity.setId(id);
        level.addEntity(entity);

        context.getSource().sendFeedback(Component.translatable("commands.summon.success", entity.getDisplayName()));

        return Command.SINGLE_SUCCESS;
    }
}
