package com.thevoidblock.voidcommands.mixin;

import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Options.class)
public interface GameOptionsAccessor {
    @Accessor("serverRenderDistance")
    int getServerViewDistance();
}
