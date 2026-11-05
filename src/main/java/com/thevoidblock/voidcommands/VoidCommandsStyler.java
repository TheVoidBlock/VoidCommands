package com.thevoidblock.voidcommands;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class VoidCommandsStyler {

    public static final ChatFormatting HEADER_FORMATTING = ChatFormatting.WHITE;
    public static final ChatFormatting SEPERATOR_FORMATTING = ChatFormatting.WHITE;
    public static final ChatFormatting NUMBER_FORMATTING = ChatFormatting.GOLD;
    public static final ChatFormatting IDENTIFIER_FORMATTING = ChatFormatting.WHITE;
    public static final ChatFormatting KEY_FORMATTING = ChatFormatting.AQUA;
    public static final ChatFormatting ERROR_FORMATTING = ChatFormatting.RED;

    public static <T extends Number> MutableComponent formatTranslatableNumber(String key, T value) {
        return
                Component.translatable(key).withStyle(KEY_FORMATTING)
                        .append(Component.literal(": ").withStyle(SEPERATOR_FORMATTING))
                        .append(Component.literal(value.toString()).withStyle(NUMBER_FORMATTING))
                ;
    }

    public static MutableComponent formatPlayerPos(Vec3 vector) {
        return
                Component.literal(String.format("%.3f", vector.x)).withStyle(NUMBER_FORMATTING)
                        .append(Component.literal(", ").withStyle(SEPERATOR_FORMATTING))
                        .append(Component.literal(String.format("%.5f", vector.y)).withStyle(NUMBER_FORMATTING))
                        .append(Component.literal(", ").withStyle(SEPERATOR_FORMATTING))
                        .append(Component.literal(String.format("%.3f", vector.z)).withStyle(NUMBER_FORMATTING))
                ;
    }
}
