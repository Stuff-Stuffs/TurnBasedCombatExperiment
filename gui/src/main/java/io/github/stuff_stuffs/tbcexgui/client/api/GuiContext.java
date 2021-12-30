package io.github.stuff_stuffs.tbcexgui.client.api;

import io.github.stuff_stuffs.tbcexgui.client.impl.render.QuadSplitter;
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

    default void pushScale(final double xScale, final double yScale, final double zScale) {
        final Vec2d scalar = new Vec2d(xScale, yScale);
        pushQuadTransform(new GuiTransform() {
            @Override
            public boolean transform(final MutableGuiQuad quad, final GuiTransform.Context context) {
                for (int i = 0; i < 4; i++) {
                    quad.pos(i, (float) (quad.x(i) * yScale), (float) (quad.y(i) * yScale));
                    quad.depth((float) (quad.depth() * zScale));
                }
                return true;
            }

            @Override
            public Vec2d transformMouseCursor(final Vec2d cursor) {
                return cursor.multiply(scalar);
            }
        });
    }

    default void pushTranslate(final double x, final double y, final double z) {
        final Vec2d offset = new Vec2d(x, y);
        pushQuadTransform(new GuiTransform() {
            @Override
            public boolean transform(final MutableGuiQuad quad, final GuiTransform.Context context) {
                for (int i = 0; i < 4; i++) {
                    quad.pos(i, (float) (quad.x(i) + x), (float) (quad.y(i) + y));
                    quad.depth((float) (quad.depth() + z));
                }
                return true;
            }

            @Override
            public Vec2d transformMouseCursor(final Vec2d cursor) {
                return cursor.add(offset);
            }
        });
    }

    default void pushRotate(final Quaternion quaternion) {
        pushMatrixMultiply(new Matrix4f(quaternion));
    }

    default void pushMatrixMultiply(final Matrix4f matrix) {
        final Vector4f vec = new Vector4f();
        pushQuadTransform(new GuiTransform() {
            @Override
            public boolean transform(final MutableGuiQuad quad, final GuiTransform.Context context) {
                for (int i = 0; i < 4; i++) {
                    vec.set(quad.x(i), quad.y(i), quad.depth(), 1);
                    vec.transform(matrix);
                    quad.pos(i, vec.getX(), vec.getY());
                    quad.depth(vec.getZ());
                }
                return true;
            }

            @Override
            public Vec2d transformMouseCursor(final Vec2d cursor) {
                vec.set((float) cursor.x, (float) cursor.y, /*TODO 1?*/0, 1);
                return new Vec2d(vec.getX(), vec.getY());
            }
        });
    }

    default void pushScissor(final float x, final float y, final float width, final float height) {
        final Rect2d rect = new Rect2d(x, y, x + width, y + height);
        pushQuadTransform(new GuiTransform() {
            @Override
            public boolean transform(final MutableGuiQuad quad, final GuiTransform.Context context) {
                boolean allInside = true;
                for (int i = 0; i < 4; i++) {
                    if (!rect.isIn(quad.x(i), quad.y(i))) {
                        allInside = false;
                    }
                }
                if (allInside) {
                    return true;
                }
                QuadSplitter.scissor(quad, x, y, x + width, y + height, context.emitter());
                return true;
            }

            @Override
            public Vec2d transformMouseCursor(final Vec2d cursor) {
                return cursor;
            }
        });
    }

    Vec2d transformMouseCursor(Vec2d mouseCursor);

    void renderText(OrderedText text, TextOutline outline, int colour, int outlineColour, int underlineColour);

    float getTickDelta();

    GuiInputContext getInputContext();

    GuiQuadEmitter getEmitter();

    enum TextOutline {
        NONE,
        OUTLINE,
        SHADOW
    }
}
