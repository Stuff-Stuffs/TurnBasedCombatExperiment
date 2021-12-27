package io.github.stuff_stuffs.tbcexgui.client.render;

import io.github.stuff_stuffs.tbcexutil.common.Vec2d;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vector4f;

public interface GuiContext {
    GuiContext createChild();

    void pushQuadTransform(GuiTransform transform);

    void popQuadTransform();

    default void pushScale(double xScale, double yScale, double zScale) {
        Vec2d scalar = new Vec2d(xScale, yScale);
        pushQuadTransform(new GuiTransform() {
            @Override
            public boolean transform(MutableGuiQuad quad) {
                for (int i = 0; i < 4; i++) {
                    quad.pos(i, (float) (quad.x(i) * yScale), (float) (quad.y(i) * yScale));
                    quad.depth((float) (quad.depth() * zScale));
                }
                return true;
            }

            @Override
            public Vec2d transformMouseCursor(Vec2d cursor) {
                return cursor.multiply(scalar);
            }

            @Override
            public Vec2d transformMouseDelta(Vec2d delta) {
                return delta.multiply(scalar);
            }
        });
    }

    default void pushTranslate(double x, double y, double z) {
        Vec2d offset = new Vec2d(x, y);
        pushQuadTransform(new GuiTransform() {
            @Override
            public boolean transform(MutableGuiQuad quad) {
                for (int i = 0; i < 4; i++) {
                    quad.pos(i, (float) (quad.x(i) + x), (float) (quad.y(i) + y));
                    quad.depth((float) (quad.depth() + z));
                }
                return true;
            }

            @Override
            public Vec2d transformMouseCursor(Vec2d cursor) {
                return cursor.add(offset);
            }

            @Override
            public Vec2d transformMouseDelta(Vec2d delta) {
                return delta;
            }
        });
    }

    default void pushRotate(Quaternion quaternion) {
        pushMatrixMultiply(new Matrix4f(quaternion));
    }

    default void pushMatrixMultiply(Matrix4f matrix) {
        Vector4f vec = new Vector4f();
        pushQuadTransform(new GuiTransform() {
            @Override
            public boolean transform(MutableGuiQuad quad) {
                for (int i = 0; i < 4; i++) {
                    vec.set(quad.x(i), quad.y(i), quad.depth(), 1);
                    vec.transform(matrix);
                    quad.pos(i, vec.getX(), vec.getY());
                    quad.depth(vec.getZ());
                }
                return true;
            }

            @Override
            public Vec2d transformMouseCursor(Vec2d cursor) {
                vec.set((float) cursor.x, (float) cursor.y, /*TODO 1?*/0, 1);
                return new Vec2d(vec.getX(), vec.getY());
            }

            @Override
            public Vec2d transformMouseDelta(Vec2d delta) {
                vec.set((float) delta.x, (float) delta.y, /*TODO 1?*/0, 1);
                return new Vec2d(vec.getX(), vec.getY());
            }
        });
    }

    Vec2d transformMouseCursor(Vec2d mouseCursor);

    Vec2d transformMouseDelta(Vec2d mouseDelta);
}
