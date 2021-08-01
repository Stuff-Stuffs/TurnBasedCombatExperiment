package io.github.stuff_stuffs.tbcexgui.client.widget.interaction;

import net.minecraft.util.Identifier;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

public enum ButtonPart {
    MIDDLE_MIDDLE,
    TOP_MIDDLE,
    BOTTOM_MIDDLE,
    MIDDLE_LEFT,
    MIDDLE_RIGHT,
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT;
    private final String name;
    private final Map<ButtonState, Identifier> identifiers;

    ButtonPart() {
        name = name().toLowerCase(Locale.ROOT);
        identifiers = new EnumMap<>(ButtonState.class);
        for (final ButtonState state : ButtonState.values()) {
            identifiers.put(state, new Identifier("tbcexgui", "gui/button/" + state.name().toLowerCase(Locale.ROOT) + "/" + getName()));
        }
    }

    public String getName() {
        return name;
    }

    public Identifier getIdentifier(final ButtonState state) {
        return identifiers.get(state);
    }
}
