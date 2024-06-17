package com.thevoidblock.clientgive.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.xpple.clientarguments.arguments.CItemArgument;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.item.ItemStack;

import static com.thevoidblock.clientgive.ClientGive.CLIENT;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class CGetCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> dispatcher.register(
                        literal("cget")
                                .then(
                                        argument("item", CItemArgument.itemStack(registryAccess))
                                                .executes(CGetCommand::getItem)

                                        .then(
                                                argument("count", IntegerArgumentType.integer(1))
                                                        .executes(CGetCommand::getItemCount)
                                        )
                                )
                ));
    }

    private static int getItem(CommandContext<FabricClientCommandSource> context) {
        try {
            giveItem(CItemArgument.getItemStackArgument(context, "item").createStack(1, false));
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }

    private static int getItemCount(CommandContext<FabricClientCommandSource> context) {
        try {
            giveItem(CItemArgument.getItemStackArgument(context, "item").createStack(IntegerArgumentType.getInteger(context, "count"), false));
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }

    private static void giveItem(ItemStack item) {
        assert CLIENT.player != null;
        CLIENT.player.giveItemStack(item);
    }
}
