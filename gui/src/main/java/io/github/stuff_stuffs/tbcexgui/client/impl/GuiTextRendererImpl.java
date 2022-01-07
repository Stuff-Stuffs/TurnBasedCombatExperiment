package io.github.stuff_stuffs.tbcexgui.client.impl;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiTextRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.Matrix4f;

public class GuiTextRendererImpl implements GuiTextRenderer {
    private static final Matrix4f SCALE = Matrix4f.scale(1, 1, 1);
    private final TextRenderer delegate;
    private final GuiContextImpl context;

    public GuiTextRendererImpl(final TextRenderer delegate, final GuiContextImpl context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    public int getHeight() {
        return delegate.fontHeight;
    }

    @Override
    public int getWidth(final OrderedText text) {
        return delegate.getWidth(text);
    }

    @Override
    public void render(final OrderedText text, final int colour, final boolean shadow, final int backgroundColour) {
        final boolean seeThrough = (colour & 0xFF_00_00_00) != 0xFF_00_00_00 || (backgroundColour != 0 && (backgroundColour & 0xFF_00_00_00) != 0xFF_00_00_00);
        delegate.draw(text, 0, 0, colour, shadow, SCALE, context.getTextAdapter(), seeThrough, backgroundColour, LightmapTextureManager.MAX_LIGHT_COORDINATE);
    }
}
