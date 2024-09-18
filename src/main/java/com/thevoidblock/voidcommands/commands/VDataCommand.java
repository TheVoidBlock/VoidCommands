package com.thevoidblock.voidcommands.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.thevoidblock.voidcommands.util.EntityStringInterface;
import dev.xpple.clientarguments.arguments.CEntityArgument;
import dev.xpple.clientarguments.arguments.CNbtPathArgument;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.*;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.thevoidblock.voidcommands.NbtFormatter.*;
import static com.thevoidblock.voidcommands.VoidCommandsStyler.KEY_FORMATTING;
import static com.thevoidblock.voidcommands.VoidCommandsStyler.formatNameUUID;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class VDataCommand {

    private static final SimpleCommandExceptionType MERGE_FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.data.merge.failed"));
    private static final DynamicCommandExceptionType GET_INVALID_EXCEPTION = new DynamicCommandExceptionType(path -> Text.stringifiedTranslatable("commands.data.get.invalid", path));
    private static final DynamicCommandExceptionType GET_UNKNOWN_EXCEPTION = new DynamicCommandExceptionType(path -> Text.stringifiedTranslatable("commands.data.get.unknown", path));
    private static final SimpleCommandExceptionType GET_MULTIPLE_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.data.get.multiple"));
    private static final DynamicCommandExceptionType MODIFY_EXPECTED_OBJECT_EXCEPTION = new DynamicCommandExceptionType(nbt -> Text.stringifiedTranslatable("commands.data.modify.expected_object", nbt));
    private static final DynamicCommandExceptionType MODIFY_EXPECTED_VALUE_EXCEPTION = new DynamicCommandExceptionType(nbt -> Text.stringifiedTranslatable("commands.data.modify.expected_value", nbt));
    private static final Dynamic2CommandExceptionType MODIFY_INVALID_SUBSTRING_EXCEPTION = new Dynamic2CommandExceptionType((startIndex, endIndex) -> Text.stringifiedTranslatable("commands.data.modify.invalid_substring", startIndex, endIndex));

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> dispatcher.register(
                        literal("vdata").then(
                                literal("get").then(
                                        argument("entity", CEntityArgument.entities()).then(
                                                        argument("path", CNbtPathArgument.nbtPath()).then(
                                                                argument("scale", IntegerArgumentType.integer()).executes(
                                                                        context -> executeGetPathScale(context, CEntityArgument.getEntities(context, "entity"), CNbtPathArgument.getNbtPath(context, "path"), IntegerArgumentType.getInteger(context, "scale"))
                                                                )
                                                        ).executes(
                                                                context -> executeGetPath(context, CEntityArgument.getEntities(context, "entity"), CNbtPathArgument.getNbtPath(context, "path"))
                                                        )
                                                ).executes(
                                                    context -> executeGet(context, CEntityArgument.getEntities(context, "entity"))
                                                )
                                )
                        ).then(
                                literal("merge") // TODO
                        ).then(
                                literal("modify") // TODO
                        ).then(
                                literal("remove") // TODO
                        )

        ));
    }

    private static int executeGet(CommandContext<FabricClientCommandSource> context, Collection<? extends Entity> entities) throws CommandSyntaxException {
        context.getSource().sendFeedback(formatNBTText(entities, entity -> getNBT(entity).asString()));
        return entities.size();
    }
    @SuppressWarnings("rawtypes")
    private static int executeGetPath(CommandContext<FabricClientCommandSource> context, Collection<? extends Entity> entities, CNbtPathArgument.NbtPath path) throws CommandSyntaxException {
        context.getSource().sendFeedback(formatNBTText(entities, entity -> path.getString() + ":" + getNbt(getNBT(entity), path).asString()));

        int i;
        assert !entities.isEmpty();
        NbtElement nbt = getNbt(getNBT(entities.stream().findFirst().get()), path);
        i = switch (nbt) {
            case AbstractNbtNumber abstractNbtNumber -> MathHelper.floor(abstractNbtNumber.doubleValue());
            case AbstractNbtList abstractNbtList -> abstractNbtList.size();
            case NbtCompound nbtCompound -> nbtCompound.getSize();
            case NbtString ignored -> nbt.asString().length();
            case null, default -> throw GET_UNKNOWN_EXCEPTION.create(path.toString());
        };
        return i;
    }
    private static int executeGetPathScale(CommandContext<FabricClientCommandSource> context, Collection<? extends Entity> entities, CNbtPathArgument.NbtPath path, double scale) {
        // TODO: add functionality for executeGetPathScale
        return 1;
    }

    private static <T extends Entity>NbtCompound getNBT(T entity) {
        NbtCompound nbt = new NbtCompound();
        entity.writeNbt(nbt);
        return nbt;
    }
    private static Text formatNBTText(@NotNull Collection<? extends Entity> entities, EntityStringInterface nbt) throws CommandSyntaxException {
        assert !entities.isEmpty();
        Entity firstEntity = entities.stream().findFirst().get();
        MutableText text = (MutableText) getNbtQueryWithPrefix(firstEntity, FormatNBTText(Text.literal(nbt.getString(firstEntity))));
        var wrapper = new Object(){CommandSyntaxException commandSyntaxException;};
        entities.stream().skip(1).forEach(
                entity -> {
                    try {
                        text.append(Text.literal(", ").formatted(SEPARATION_COLOR))
                                .append(getNbtQueryWithPrefix(entity, FormatNBTText(Text.literal(nbt.getString(entity)))));
                    } catch (CommandSyntaxException e) {
                        wrapper.commandSyntaxException = e;
                    }
                }
        );
        if(wrapper.commandSyntaxException != null) throw wrapper.commandSyntaxException;
        return text;
    }
    private static NbtElement getNbt(NbtCompound nbt, CNbtPathArgument.NbtPath path) throws CommandSyntaxException {
        List<NbtElement> collection = path.get(nbt);

        Iterator<NbtElement> iterator = collection.iterator();
        NbtElement nbtElement = iterator.next();
        if (iterator.hasNext()) {
            throw GET_MULTIPLE_EXCEPTION.create();
        }
        return nbtElement;
    }
    private static <T extends Entity>Text getNbtQueryWithPrefix(T entity, Text nbt) {
        return Text.translatable("commands.data.entity.query", formatNameUUID(entity), nbt);
    }
}
