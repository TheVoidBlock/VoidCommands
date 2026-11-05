package com.thevoidblock.voidcommands.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.xpple.clientarguments.arguments.CItemArgument;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import static com.thevoidblock.voidcommands.VoidCommands.*;
import static java.lang.String.format;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;

@Environment(EnvType.CLIENT)
public class VGetCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> dispatcher.register(
                        literal(format("%sget", COMMAND_PREFIX))
                                .then(
                                        argument("item", CItemArgument.itemStack(registryAccess))
                                                .executes(VGetCommand::getItem)

                                        .then(
                                                argument("count", IntegerArgumentType.integer(1))
                                                        .executes(VGetCommand::getItemCount)
                                        )
                                )
        ));
    }

    private static int getItem(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        giveItem(context, CItemArgument.getItemStackArgument(context, "item").createItemStack(1));
        return Command.SINGLE_SUCCESS;
    }

    private static int getItemCount(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        giveItem(context, CItemArgument.getItemStackArgument(context, "item").createItemStack(IntegerArgumentType.getInteger(context, "count")));
        return Command.SINGLE_SUCCESS;
    }

    private static void giveItem(CommandContext<FabricClientCommandSource> context, ItemStack item) {
        context.getSource().sendFeedback(Component.translatable(format("chat.%s.receive_item", MOD_ID))
                .append(" ")
                .append(Integer.toString(item.getCount()))
                .append(" ")
                .append(item.getDisplayName())
        );

        context.getSource().getPlayer().addItem(item);
    }
}
