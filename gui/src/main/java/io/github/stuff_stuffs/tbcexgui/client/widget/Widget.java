package io.github.stuff_stuffs.tbcexgui.client.widget;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;

public interface Widget {
    void resize(double width, double height, int pixelWidth, int pixelHeight);

    void render(GuiContext context);

    String getDebugName();
}
