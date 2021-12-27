package io.github.stuff_stuffs.tbcexgui.client.render.impl;

import io.github.stuff_stuffs.tbcexgui.client.render.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.render.GuiTransform;
import io.github.stuff_stuffs.tbcexgui.client.render.MutableGuiQuad;
import io.github.stuff_stuffs.tbcexutil.common.Vec2d;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public class GuiContextImpl implements GuiContext {
    private final List<GuiTransform> transforms;

    public GuiContextImpl(MatrixStack stack) {
        this.transforms = new ArrayList<>();
        pushMatrixMultiply(stack.peek().getPositionMatrix());
    }

    public GuiContextImpl(List<GuiTransform> transforms) {
        this.transforms = transforms;
    }

    @Override
    public GuiContext createChild() {
        return new GuiContextImpl(new ArrayList<>(transforms));
    }

    @Override
    public void pushQuadTransform(GuiTransform transform) {
        transforms.add(transform);
    }

    @Override
    public void popQuadTransform() {
        transforms.remove(transforms.size() - 1);
    }

    @Override
    public Vec2d transformMouseCursor(Vec2d mouseCursor) {
        for (int i = transforms.size() - 1; i >= 0; i--) {
            mouseCursor = transforms.get(i).transformMouseCursor(mouseCursor);
        }
        return mouseCursor;
    }

    @Override
    public Vec2d transformMouseDelta(Vec2d mouseDelta) {
        for (int i = transforms.size() - 1; i >= 0; i--) {
            mouseDelta = transforms.get(i).transformMouseCursor(mouseDelta);
        }
        return mouseDelta;
    }

    public boolean transformQuad(MutableGuiQuad quad) {
        for (int i = transforms.size() - 1; i >= 0; i--) {
            if (!transforms.get(i).transform(quad)) {
                return false;
            }
        }
        return true;
    }
}
