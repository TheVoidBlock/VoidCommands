package com.thevoidblock.voidcommands.mixin;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.commands.SummonCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SummonCommand.class)
public interface SummonCommandAccessor {
    @Accessor
    static SimpleCommandExceptionType getERROR_FAILED() {
        throw new AssertionError();
    }
}
