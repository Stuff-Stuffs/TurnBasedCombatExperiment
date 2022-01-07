package io.github.stuff_stuffs.tbcexgui.client.api;

import net.minecraft.text.OrderedText;

public interface GuiTextRenderer {
    int getHeight();

    int getWidth(OrderedText text);

    void render(OrderedText text, int colour, boolean shadow, int backgroundColour);
}
