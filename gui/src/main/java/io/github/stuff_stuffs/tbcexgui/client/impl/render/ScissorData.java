package io.github.stuff_stuffs.tbcexgui.client.impl.render;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiTransform;
import io.github.stuff_stuffs.tbcexgui.client.api.MutableGuiQuad;
import io.github.stuff_stuffs.tbcexutil.common.Vec2d;

public class ScissorData implements GuiTransform {
    public final float x, y, width, height;

    public ScissorData(final float x, final float y, final float width, final float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean transform(final MutableGuiQuad quad) {
        return true;
    }

    @Override
    public Vec2d transformMouseCursor(final Vec2d cursor) {
        return cursor;
    }
}
