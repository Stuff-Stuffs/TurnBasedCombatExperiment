package io.github.stuff_stuffs.tbcexgui.client.api;

import io.github.stuff_stuffs.tbcexutil.common.Vec2d;

public interface GuiTransform {
    boolean transform(MutableGuiQuad quad);

    Vec2d transformMouseCursorToGui(Vec2d cursor);

    Vec2d transformMouseCursorToScreen(Vec2d cursor);
}
