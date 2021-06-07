package io.github.stuff_stuffs.tbcexgui.client.widget.panel;

import net.minecraft.util.Identifier;

import java.util.Locale;

public enum PanelPart {
    MIDDLE,
    TOP,
    BOTTOM,
    LEFT,
    RIGHT,
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT;
    private final String name;
    private final Identifier identifier;

    PanelPart() {
        name = name().toLowerCase(Locale.ROOT);
        identifier = new Identifier("tbcexgui", "gui/panel_" + getName());
    }

    public String getName() {
        return name;
    }

    public Identifier getIdentifier() {
        return identifier;
    }
}
