package com.thevoidblock.voidcommands.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.xpple.clientarguments.arguments.CEntityArgument;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;

import java.util.Collection;

import static com.thevoidblock.voidcommands.VoidCommands.CLIENT;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class VKillCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> dispatcher.register(
                        literal("vkill").then(argument(
                                "entities", CEntityArgument.entities()
                                ).executes(
                                        VKillCommand::execute
                                )
                        )
                ));
    }

    private static int execute(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = CEntityArgument.getEntities(context, "entities");

        assert CLIENT.world != null;
        targets.forEach(entity -> CLIENT.world.removeEntity(entity.getId(), Entity.RemovalReason.KILLED));

        if (targets.size() == 1) {
            context.getSource().sendFeedback(Text.translatable("commands.kill.success.single", ((targets.iterator().next()).getDisplayName())));
        } else {
            context.getSource().sendFeedback(Text.translatable("commands.kill.success.multiple", targets.size()));
        }
        // TODO: add feedback
        return targets.size();
    }
}
