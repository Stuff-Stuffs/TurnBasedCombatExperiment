package io.github.stuff_stuffs.tbcexgui.client.api;

import io.github.stuff_stuffs.tbcexgui.client.impl.render.ScissorData;
import io.github.stuff_stuffs.tbcexutil.common.Vec2d;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vector4f;

import java.util.List;

public interface GuiContext {
    void pushGuiTransform(GuiTransform transform);

    void popGuiTransform();

    default void pushScale(final double xScale, final double yScale, final double zScale) {
        final Vec2d guiScalar = new Vec2d(1 / xScale, 1 / yScale);
        final Vec2d screenScalar = new Vec2d(xScale, yScale);
        pushGuiTransform(new GuiTransform() {
            @Override
            public boolean transform(final MutableGuiQuad quad) {
                for (int i = 0; i < 4; i++) {
                    quad.pos(i, (float) (quad.x(i) * xScale), (float) (quad.y(i) * yScale));
                    quad.depth((float) (quad.depth() * zScale));
                }
                return true;
            }

            @Override
            public Vec2d transformMouseCursorToGui(final Vec2d cursor) {
                return cursor.multiply(guiScalar);
            }

            @Override
            public Vec2d transformMouseCursorToScreen(final Vec2d cursor) {
                return cursor.multiply(screenScalar);
            }
        });
    }

    default void pushTranslate(final double x, final double y, final double z) {
        final Vec2d offset = new Vec2d(-x, -y);
        final Vec2d screen = new Vec2d(x, y);
        pushGuiTransform(new GuiTransform() {
            @Override
            public boolean transform(final MutableGuiQuad quad) {
                for (int i = 0; i < 4; i++) {
                    quad.pos(i, (float) (quad.x(i) + x), (float) (quad.y(i) + y));
                }
                quad.depth((float) (quad.depth() + z));
                return true;
            }

            @Override
            public Vec2d transformMouseCursorToGui(final Vec2d cursor) {
                return cursor.add(offset);
            }

            @Override
            public Vec2d transformMouseCursorToScreen(final Vec2d cursor) {
                return cursor.add(screen);
            }
        });
    }

    default void pushRotate(final Quaternion quaternion) {
        pushMatrixMultiply(new Matrix4f(quaternion));
    }

    default void pushMatrixMultiply(final Matrix4f matrix) {
        final Vector4f vec = new Vector4f();
        final Matrix4f inverse = matrix.copy();
        final float determinate = inverse.determinantAndAdjugate();
        inverse.multiply(1 / determinate);
        pushGuiTransform(new GuiTransform() {
            @Override
            public boolean transform(final MutableGuiQuad quad) {
                float avg = 0;
                for (int i = 0; i < 4; i++) {
                    vec.set(quad.x(i), quad.y(i), quad.depth(), 1);
                    vec.transform(matrix);
                    quad.pos(i, vec.getX(), vec.getY());
                    avg += vec.getZ();
                }
                quad.depth(avg * 0.25F);
                return true;
            }

            @Override
            public Vec2d transformMouseCursorToGui(final Vec2d cursor) {
                vec.set((float) cursor.x, (float) cursor.y, 0, 1);
                vec.transform(inverse);
                return new Vec2d(vec.getX(), vec.getY());
            }

            @Override
            public Vec2d transformMouseCursorToScreen(final Vec2d cursor) {
                vec.set((float) cursor.x, (float) cursor.y, 0, 1);
                vec.transform(matrix);
                return new Vec2d(vec.getX(), vec.getY());
            }
        });
    }

    default void pushScissor(final float x, final float y, final float width, final float height) {
        pushGuiTransform(new ScissorData(x, y, width, height));
    }

    Vec2d transformMouseCursor(Vec2d mouseCursor);

    default Vec2d transformMouseCursor() {
        final GuiInputContext inputContext = getInputContext();
        return transformMouseCursor(new Vec2d(inputContext.getMouseCursorX(), inputContext.getMouseCursorY()));
    }

    float getTickDelta();

    GuiInputContext getInputContext();

    GuiQuadEmitter getEmitter();

    GuiTextRenderer getTextRenderer();

    void addTooltip(List<OrderedText> components);

    void renderTooltipBackground(double x, double y, double width, double height);

    void enterSection(String widget);

    void exitSection();

    enum TextOutline {
        NONE,
        OUTLINE,
        SHADOW
    }
}
