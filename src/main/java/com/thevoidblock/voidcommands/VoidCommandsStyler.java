package com.thevoidblock.voidcommands;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

public class VoidCommandsStyler {

    public static final Formatting HEADER_FORMATTING = Formatting.WHITE;
    public static final Formatting SEPERATOR_FORMATTING = Formatting.WHITE;
    public static final Formatting NUMBER_FORMATTING = Formatting.GOLD;
    public static final Formatting IDENTIFIER_FORMATTING = Formatting.WHITE;
    public static final Formatting KEY_FORMATTING = Formatting.AQUA;
    public static final Formatting ERROR_FORMATTING = Formatting.RED;

    public static <T extends Number> MutableText formatTranslatableNumber(String key, T value) {
        return
                Text.translatable(key).formatted(KEY_FORMATTING)
                        .append(Text.literal(": ").formatted(SEPERATOR_FORMATTING))
                        .append(Text.literal(value.toString()).formatted(NUMBER_FORMATTING))
                ;
    }

    public static MutableText formatPlayerPos(Vec3d vector) {
        return
                Text.literal(String.format("%.3f", vector.x)).formatted(NUMBER_FORMATTING)
                        .append(Text.literal(", ").formatted(SEPERATOR_FORMATTING))
                        .append(Text.literal(String.format("%.5f", vector.y)).formatted(NUMBER_FORMATTING))
                        .append(Text.literal(", ").formatted(SEPERATOR_FORMATTING))
                        .append(Text.literal(String.format("%.3f", vector.z)).formatted(NUMBER_FORMATTING))
                ;
    }
}
