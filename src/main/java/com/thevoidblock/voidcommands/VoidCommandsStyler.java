package com.thevoidblock.voidcommands;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class VoidCommandsStyler {

    public static final Formatting HEADER_FORMATTING = Formatting.WHITE;
    public static final Formatting SEPERATOR_FORMATTING = Formatting.WHITE;
    public static final Formatting NUMBER_FORMATTING = Formatting.GOLD;
    public static final Formatting KEY_FORMATTING = Formatting.AQUA;

    public static <T extends Number> MutableText formatTranslatableNumber(String key, T value) {
        return
                Text.translatable(key).formatted(KEY_FORMATTING)
                        .append(Text.literal(": ").formatted(SEPERATOR_FORMATTING))
                        .append(Text.literal(value.toString()).formatted(NUMBER_FORMATTING))
                ;
    }
}
