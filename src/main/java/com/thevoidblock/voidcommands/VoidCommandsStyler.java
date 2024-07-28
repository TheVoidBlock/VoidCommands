package com.thevoidblock.voidcommands;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class VoidCommandsStyler {

    public static <T extends Number> MutableText formatKeyValue(String key, T value) {
        return
                Text.translatable(key).formatted(Formatting.AQUA)
                        .append(Text.literal(": ").formatted(Formatting.WHITE))
                        .append(Text.literal(value.toString()).formatted(Formatting.GOLD))
                ;
    }
}
