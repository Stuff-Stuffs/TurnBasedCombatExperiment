package io.github.stuff_stuffs.tbcexgui.client.api.text;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import net.minecraft.text.OrderedText;

public interface TextDrawer {
    void draw(double width, double height, OrderedText text, GuiContext context);
}
