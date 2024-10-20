package com.thevoidblock.voidcommands.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.xpple.clientarguments.arguments.CItemArgument;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import static com.thevoidblock.voidcommands.VoidCommands.CLIENT;
import static com.thevoidblock.voidcommands.VoidCommands.MOD_ID;
import static java.lang.String.format;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

@Environment(EnvType.CLIENT)
public class VGetCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> dispatcher.register(
                        literal("vget")
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

    private static int getItem(CommandContext<FabricClientCommandSource> context) {
        try {
            giveItem(context,
                    CItemArgument.getItemStackArgument(context, "item").createStack(1, false)
            );
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }

    private static int getItemCount(CommandContext<FabricClientCommandSource> context) {
        try {
            giveItem(context,
                    CItemArgument.getItemStackArgument(context, "item").createStack(IntegerArgumentType.getInteger(context, "count"), false)
            );
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }

    private static void giveItem(CommandContext<FabricClientCommandSource> context, ItemStack item) {

        context.getSource().sendFeedback(
                Text.translatable(format("chat.%s.receive_item", MOD_ID)).append(" ")
                        .append(Integer.toString(item.getCount())).append(" ")
                        .append(item.toHoverableText())
        );

        assert CLIENT.player != null;
        CLIENT.player.giveItemStack(item);
    }


}
