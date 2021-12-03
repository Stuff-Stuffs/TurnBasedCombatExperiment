package io.github.stuff_stuffs.tbcexgui.client.widget.interaction;

import io.github.stuff_stuffs.tbcexgui.client.render.GuiRenderLayers;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import io.github.stuff_stuffs.tbcexutil.common.Vec2d;
import io.github.stuff_stuffs.tbcexutil.common.colour.Colour;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class CycleSelectionWheelWidget extends AbstractWidget {
    private static final double SQRT_2 = Math.sqrt(2);
    private final WidgetPosition position;
    private final List<Entry<?>> entries;

    private CycleSelectionWheelWidget(final WidgetPosition position, final List<Entry<?>> entries) {
        this.position = position;
        this.entries = entries;
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return position;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        for (final Entry<?> entry : entries) {
            if (entry.mouseClicked(mouseX - position.getX(), mouseY - position.getY(), button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        return false;
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        return false;
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        return false;
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        matrices.push();
        matrices.translate(position.getX(), position.getY(), 0);
        final double tX = mouseX - position.getX();
        final double tY = mouseY - position.getY();
        render(vertexConsumers -> {
            for (final Entry<?> entry : entries) {
                entry.render(matrices, tX, tY, delta, new CycleSelectionWheelWidget.TextRenderer() {
                    @Override
                    public void renderTooltip(final MatrixStack matrices, final List<TooltipComponent> tooltip, final double x, final double y) {
                        CycleSelectionWheelWidget.this.renderTooltip(matrices, tooltip, x, y);
                    }

                    @Override
                    public void renderText(final MatrixStack matrices, final Text name, final double x, final double y, final double width, final double height) {
                        renderFitText(matrices, name, x + (-width / 2.0), y + (-height / 2.0), width, height, true, Colour.WHITE, 255, vertexConsumers);
                    }
                }, vertexConsumers);
            }
        });
        matrices.pop();
    }

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        return false;
    }

    private static final class Entry<T> {
        private final Vec2d[] corners;
        private final Vec2d[] hoveredCorners;
        private final double angle;
        private final Colour colour;
        private final int alpha;
        private final Colour hoveredColour;
        private final int hoveredAlpha;
        private final Colour forcedColour;
        private final int forcedAlpha;
        private final Function<T, Text> name;
        private final Function<T, List<TooltipComponent>> tooltip;
        private final UnaryOperator<T> next;
        private final @Nullable UnaryOperator<T> prev;
        private final Supplier<@Nullable T> forced;
        private boolean hovered = false;
        private T val;

        private Entry(final Vec2d[] corners, final Vec2d[] hoveredCorners, final double angle, final Colour colour, final int alpha, final Colour hoveredColour, final int hoveredAlpha, final Colour forcedColour, final int forcedAlpha, final Function<T, Text> name, final Function<T, List<TooltipComponent>> tooltip, final UnaryOperator<T> next, @Nullable final UnaryOperator<T> prev, final Supplier<@Nullable T> forced, final T start) {
            this.corners = corners;
            this.hoveredCorners = hoveredCorners;
            this.angle = angle;
            this.colour = colour;
            this.alpha = alpha;
            this.hoveredColour = hoveredColour;
            this.hoveredAlpha = hoveredAlpha;
            this.forcedColour = forcedColour;
            this.forcedAlpha = forcedAlpha;
            this.name = name;
            this.tooltip = tooltip;
            this.next = next;
            this.prev = prev;
            this.forced = forced;
            val = start;
        }

        public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
            if (forced.get() == null && isIn(mouseX, mouseY, hovered ? hoveredCorners : corners)) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    val = next.apply(val);
                } else if (prev != null && button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                    val = prev.apply(val);
                }
                return true;
            }
            return false;
        }

        public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float tickDelta, final TextRenderer textRenderer, final VertexConsumerProvider vertexConsumers) {
            final T forced = this.forced.get();
            if (forced != null) {
                val = forced;
            }
            hovered = forced == null && isIn(mouseX, mouseY, hovered ? hoveredCorners : corners);
            if (hovered) {
                textRenderer.renderTooltip(matrices, tooltip.apply(val), mouseX, mouseY);
            }
            final VertexConsumer positionColourConsumer = vertexConsumers.getBuffer(GuiRenderLayers.POSITION_COLOUR_TRANSPARENT_LAYER);
            final Vec2d[] c = hovered ? hoveredCorners : corners;
            final int packedColour = hovered ? hoveredColour.pack(hoveredAlpha) : forced == null ? colour.pack(alpha) : forcedColour.pack(forcedAlpha);
            for (final Vec2d first : c) {
                positionColourConsumer.vertex(matrices.peek().getPositionMatrix(), (float) first.x, (float) first.y, 0).color(packedColour).next();
            }
            Vec2d avg = Vec2d.ZERO;
            for (final Vec2d d : c) {
                avg = avg.add(d);
            }
            avg = avg.scale(0.25);
            matrices.push();
            matrices.translate(avg.x, avg.y, 0);
            double angNorm = angle + Math.PI / 2.0;
            angNorm = angNorm % Math.PI;
            angNorm = angNorm - Math.PI / 2.0;
            matrices.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion((float) angNorm));
            final Vec2d deltaX = c[0].add(c[3].scale(-1));
            double maxWidth = deltaX.dot(deltaX);
            maxWidth = maxWidth * MathHelper.fastInverseSqrt(maxWidth);
            final Vec2d deltaY = c[0].add(c[1].scale(-1));
            double maxHeight = deltaY.dot(deltaY);
            maxHeight = maxHeight * MathHelper.fastInverseSqrt(maxHeight);
            textRenderer.renderText(matrices, name.apply(val), 0, 0, maxWidth, maxHeight);
            matrices.pop();
        }
    }

    private static boolean isIn(final double mouseX, final double mouseY, final Vec2d[] corners) {
        TriState gz = TriState.DEFAULT;
        for (int i = 0; i < 4; i++) {
            final Vec2d first = corners[i];
            final Vec2d second = corners[(i + 1) & 3];
            final double s = (mouseY - first.y) * (second.x - first.x) - (mouseX - first.x) * (second.y - first.y);
            if (gz == TriState.DEFAULT) {
                if (s != 0) {
                    gz = s > 0 ? TriState.TRUE : TriState.FALSE;
                }
            } else {
                if (s != 0) {
                    final TriState cur = s > 0 ? TriState.TRUE : TriState.FALSE;
                    if (cur != gz) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private interface TextRenderer {
        void renderTooltip(MatrixStack matrices, List<TooltipComponent> tooltip, double x, double y);

        void renderText(MatrixStack matrices, Text name, double x, double y, double width, double height);
    }

    private record PartialEntry<T>(Colour colour, int alpha, Colour hoveredColour, int hoveredAlpha,
                                   Colour forcedColour, int forcedAlpha, Function<T, Text> name,
                                   Function<T, List<TooltipComponent>> tooltip, UnaryOperator<T> next,
                                   @Nullable UnaryOperator<T> prev, Supplier<@Nullable T> forced, T start,
                                   MutableObject<Supplier<T>> getterHolder) {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final List<PartialEntry<?>> partialEntries;

        private Builder() {
            partialEntries = new ArrayList<>(8);
        }

        public <T> Supplier<T> addEntry(final Colour colour, final int alpha, final Colour hoveredColour, final int hoveredAlpha, final Colour forcedColour, final int forcedAlpha, final Function<T, Text> name, final Function<T, List<TooltipComponent>> tooltip, final UnaryOperator<T> next, @Nullable final UnaryOperator<T> prev, final Supplier<@Nullable T> forced, final T start) {
            final MutableObject<Supplier<T>> getterHolder = new MutableObject<>(() -> start);
            partialEntries.add(new PartialEntry<>(colour, alpha, hoveredColour, hoveredAlpha, forcedColour, forcedAlpha, name, tooltip, next, prev, forced, start, getterHolder));
            return () -> getterHolder.getValue().get();
        }

        public CycleSelectionWheelWidget build(final double innerDiameter, final double outerDiameter, final double hoverDiameter, final WidgetPosition position) {
            final int size = partialEntries.size();
            final double denominator = (Math.PI * 2) / size;
            final List<Entry<?>> entries = new ArrayList<>(size);
            if (size < 3) {
                if (size == 1) {
                    entries.add(createSingle(partialEntries.get(0), outerDiameter, hoverDiameter));
                } else {
                    entries.add(createDouble(partialEntries.get(0), 0, outerDiameter, hoverDiameter));
                    entries.add(createDouble(partialEntries.get(1), 1, outerDiameter, hoverDiameter));
                }
            } else {
                for (int i = 0; i < size; i++) {
                    entries.add(create(partialEntries.get(i), denominator, i, innerDiameter, outerDiameter, hoverDiameter));
                }
            }
            return new CycleSelectionWheelWidget(position, entries);
        }

        private static <T> Entry<T> createSingle(final PartialEntry<T> partial, final double outerDiameter, final double hoverDiameter) {
            final Vec2d first = new Vec2d(outerDiameter * SQRT_2, outerDiameter * SQRT_2);
            final Vec2d second = new Vec2d(outerDiameter * SQRT_2, outerDiameter * -SQRT_2);
            final Vec2d third = new Vec2d(outerDiameter * -SQRT_2, outerDiameter * -SQRT_2);
            final Vec2d fourth = new Vec2d(outerDiameter * -SQRT_2, outerDiameter * SQRT_2);
            final Vec2d firstHover = new Vec2d(hoverDiameter * SQRT_2, hoverDiameter * SQRT_2);
            final Vec2d secondHover = new Vec2d(hoverDiameter * SQRT_2, hoverDiameter * -SQRT_2);
            final Vec2d thirdHover = new Vec2d(hoverDiameter * -SQRT_2, hoverDiameter * -SQRT_2);
            final Vec2d fourthHover = new Vec2d(hoverDiameter * -SQRT_2, hoverDiameter * SQRT_2);
            final Entry<T> entry = new Entry<>(new Vec2d[]{first, second, third, fourth}, new Vec2d[]{firstHover, secondHover, thirdHover, fourthHover}, 0, partial.colour, partial.alpha, partial.hoveredColour, partial.hoveredAlpha, partial.forcedColour, partial.forcedAlpha, partial.name, partial.tooltip, partial.next, partial.prev, partial.forced, partial.start);
            partial.getterHolder.setValue(() -> entry.val);
            return entry;
        }

        private static <T> Entry<T> createDouble(final PartialEntry<T> partial, final int index, final double outerDiameter, final double hoverDiameter) {
            final int mult1 = index == 0 ? 0 : 1;
            final int mult2 = index == 0 ? 1 : 0;
            final Vec2d first = new Vec2d(outerDiameter * SQRT_2 * mult1, outerDiameter * SQRT_2);
            final Vec2d second = new Vec2d(outerDiameter * SQRT_2 * mult1, outerDiameter * -SQRT_2);
            final Vec2d third = new Vec2d(outerDiameter * -SQRT_2 * mult2, outerDiameter * -SQRT_2);
            final Vec2d fourth = new Vec2d(outerDiameter * -SQRT_2 * mult2, outerDiameter * SQRT_2);
            final Vec2d firstHover = new Vec2d(hoverDiameter * SQRT_2 * mult1, hoverDiameter * SQRT_2);
            final Vec2d secondHover = new Vec2d(hoverDiameter * SQRT_2 * mult1, hoverDiameter * -SQRT_2);
            final Vec2d thirdHover = new Vec2d(hoverDiameter * -SQRT_2 * mult2, hoverDiameter * -SQRT_2);
            final Vec2d fourthHover = new Vec2d(hoverDiameter * -SQRT_2 * mult2, hoverDiameter * SQRT_2);
            final Entry<T> entry = new Entry<>(new Vec2d[]{first, second, third, fourth}, new Vec2d[]{firstHover, secondHover, thirdHover, fourthHover}, 0, partial.colour, partial.alpha, partial.hoveredColour, partial.hoveredAlpha, partial.forcedColour, partial.forcedAlpha, partial.name, partial.tooltip, partial.next, partial.prev, partial.forced, partial.start);
            partial.getterHolder.setValue(() -> entry.val);
            return entry;
        }

        private static <T> Entry<T> create(final PartialEntry<T> partial, final double denominator, final int index, final double innerDiameter, final double outerDiameter, final double hoverDiameter) {
            final double a1 = index * denominator;
            final double a2 = (index + 1) * denominator;
            final double a1sin = Math.sin(a1);
            final double a1cos = Math.cos(a1);
            final double a2sin = Math.sin(a2);
            final double a2cos = Math.cos(a2);
            final Vec2d first = new Vec2d(innerDiameter * a1sin, innerDiameter * a1cos);
            final Vec2d second = new Vec2d(outerDiameter * a1sin, outerDiameter * a1cos);
            final Vec2d third = new Vec2d(outerDiameter * a2sin, outerDiameter * a2cos);
            final Vec2d fourth = new Vec2d(innerDiameter * a2sin, innerDiameter * a2cos);
            final Vec2d secondHover = new Vec2d(hoverDiameter * a1sin, hoverDiameter * a1cos);
            final Vec2d thirdHover = new Vec2d(hoverDiameter * a2sin, hoverDiameter * a2cos);
            final Entry<T> entry = new Entry<>(new Vec2d[]{first, second, third, fourth}, new Vec2d[]{first, secondHover, thirdHover, fourth}, (a1 + a2 + Math.PI) / 2, partial.colour, partial.alpha, partial.hoveredColour, partial.hoveredAlpha, partial.forcedColour, partial.forcedAlpha, partial.name, partial.tooltip, partial.next, partial.prev, partial.forced, partial.start);
            partial.getterHolder.setValue(() -> entry.val);
            return entry;
        }
    }
}
