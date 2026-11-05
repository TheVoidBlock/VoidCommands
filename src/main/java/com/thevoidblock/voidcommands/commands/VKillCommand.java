package com.thevoidblock.voidcommands.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.xpple.clientarguments.arguments.CEntityArgument;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import java.util.Collection;

import static com.thevoidblock.voidcommands.VoidCommands.CLIENT;
import static com.thevoidblock.voidcommands.VoidCommands.COMMAND_PREFIX;
import static java.lang.String.format;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;

@Environment(EnvType.CLIENT)
public class VKillCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> dispatcher.register(
                        literal(format("%skill", COMMAND_PREFIX)).then(argument(
                                "entities", CEntityArgument.entities()
                                ).executes(
                                        VKillCommand::execute
                                )
                        )
                ));
    }

    private static int execute(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = CEntityArgument.getEntities(context, "entities");

        assert CLIENT.level != null;
        targets.forEach(entity -> entity.remove(Entity.RemovalReason.KILLED));

        if (targets.size() == 1) {
            context.getSource().sendFeedback(Component.translatable("commands.kill.success.single", ((targets.iterator().next()).getDisplayName())));
        } else {
            context.getSource().sendFeedback(Component.translatable("commands.kill.success.multiple", targets.size()));
        }
        return targets.size();
    }
}
