package io.github.stuff_stuffs.tbcexgui.client.api;

import net.minecraft.text.OrderedText;

public interface GuiTextRenderer {
    double getHeight();

    double getWidth(OrderedText text);

    void render(OrderedText text, int colour, boolean shadow, int backgroundColour);
}
