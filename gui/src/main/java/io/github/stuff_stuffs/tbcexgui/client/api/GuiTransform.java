package io.github.stuff_stuffs.tbcexgui.client.api;

import io.github.stuff_stuffs.tbcexutil.common.Vec2d;

public interface GuiTransform {
    boolean transform(MutableGuiQuad quad);

    Vec2d transformMouseCursor(Vec2d cursor);
}
