package com.thevoidblock.voidcommands.util;

import net.minecraft.util.StringIdentifiable;

public enum BooleanToggles implements StringIdentifiable {
    ON("on"),
    OFF("off"),
    TOGGLE("toggle");

    private final String id;

    BooleanToggles(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }
}
