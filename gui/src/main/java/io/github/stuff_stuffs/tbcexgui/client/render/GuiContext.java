package io.github.stuff_stuffs.tbcexgui.client.render;

import io.github.stuff_stuffs.tbcexgui.client.render.impl.QuadSplitter;
import io.github.stuff_stuffs.tbcexutil.common.Rect2d;
import io.github.stuff_stuffs.tbcexutil.common.Vec2d;
import net.minecraft.text.OrderedText;
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
            public boolean transform(MutableGuiQuad quad, GuiTransform.Context context) {
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
        });
    }

    default void pushTranslate(double x, double y, double z) {
        Vec2d offset = new Vec2d(x, y);
        pushQuadTransform(new GuiTransform() {
            @Override
            public boolean transform(MutableGuiQuad quad, GuiTransform.Context context) {
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
        });
    }

    default void pushRotate(Quaternion quaternion) {
        pushMatrixMultiply(new Matrix4f(quaternion));
    }

    default void pushMatrixMultiply(Matrix4f matrix) {
        Vector4f vec = new Vector4f();
        pushQuadTransform(new GuiTransform() {
            @Override
            public boolean transform(MutableGuiQuad quad, GuiTransform.Context context) {
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
        });
    }

    default void pushScissor(float x, float y, float width, float height) {
        Rect2d rect = new Rect2d(x, y, x + width, y + height);
        pushQuadTransform(new GuiTransform() {
            private static float clamp(float v, float min, float max) {
                return Math.min(Math.max(v, min), max);
            }

            @Override
            public boolean transform(MutableGuiQuad quad, GuiTransform.Context context) {
                boolean allInside = true;
                for (int i = 0; i < 4; i++) {
                    if (!rect.isIn(quad.x(i), quad.y(i))) {
                        allInside = false;
                    }
                }
                if(allInside) {
                    return true;
                }
                QuadSplitter.scissor(quad, x, y, x + width, y + height, context.emitter());
                return true;
            }

            @Override
            public Vec2d transformMouseCursor(Vec2d cursor) {
                return cursor;
            }
        });
    }

    Vec2d transformMouseCursor(Vec2d mouseCursor);

    void renderText(OrderedText text, TextOutline outline, int colour, int outlineColour, int underlineColour);

    enum TextOutline {
        NONE,
        OUTLINE,
        SHADOW
    }
}
