package com.thevoidblock.voidcommands.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

@Environment(EnvType.CLIENT)
public enum BooleanToggles implements StringRepresentable {
    ON("on"),
    OFF("off"),
    TOGGLE("toggle");

    private final String id;

    BooleanToggles(String id) {
        this.id = id;
    }

    @Override
    public @NonNull String getSerializedName() {
        return this.id;
    }
}
