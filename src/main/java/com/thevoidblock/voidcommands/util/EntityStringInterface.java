package com.thevoidblock.voidcommands.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;

@FunctionalInterface
public interface EntityStringInterface {
    String getString(Entity entity) throws CommandSyntaxException;
}
