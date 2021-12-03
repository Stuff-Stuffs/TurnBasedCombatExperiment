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
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class SelectionWheelWidget extends AbstractWidget {
    private static final double SQRT_2 = Math.sqrt(2);
    private final WidgetPosition position;
    private final List<Entry> entries;

    private SelectionWheelWidget(final WidgetPosition position, final List<Entry> entries) {
        this.position = position;
        this.entries = entries;
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return position;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        for (final Entry entry : entries) {
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
            for (final Entry entry : entries) {
                entry.render(matrices, tX, tY, delta, new TextRenderer() {
                    @Override
                    public void renderTooltip(final MatrixStack matrices, final List<TooltipComponent> tooltip, final double x, final double y) {
                        SelectionWheelWidget.this.renderTooltip(matrices, tooltip, x, y);
                    }

                    @Override
                    public void renderText(final MatrixStack matrices, final Text name, final double x, final double y, final double width, final double height) {
                        renderFitTextWrap(matrices, name, x + (-width / 2.0), y + (-height / 2.0), width, height, true, Colour.WHITE, 255, vertexConsumers);
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

    private static final class Entry {
        private final Vec2d[] corners;
        private final Vec2d[] hoveredCorners;
        private final double angle;
        private final Colour colour;
        private final int alpha;
        private final Colour hoveredColour;
        private final int hoveredAlpha;
        private final Supplier<Text> name;
        private final List<TooltipComponent> tooltip;
        private final BooleanSupplier enabled;
        private final Runnable action;
        private boolean hovered = false;

        private Entry(final Vec2d[] corners, final Vec2d[] hoveredCorners, final double angle, final Colour colour, final int alpha, final Colour hoveredColour, final int hoveredAlpha, final Supplier<Text> name, final List<TooltipComponent> tooltip, final BooleanSupplier enabled, final Runnable action) {
            this.corners = corners;
            this.hoveredCorners = hoveredCorners;
            this.angle = angle;
            this.colour = colour;
            this.alpha = alpha;
            this.hoveredColour = hoveredColour;
            this.hoveredAlpha = hoveredAlpha;
            this.name = name;
            this.tooltip = tooltip;
            this.enabled = enabled;
            this.action = action;
        }

        public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
            if (enabled.getAsBoolean() && isIn(mouseX, mouseY, hovered ? hoveredCorners : corners)) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    action.run();
                    return true;
                }
                return true;
            }
            return false;
        }

        public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float tickDelta, final TextRenderer textRenderer, final VertexConsumerProvider vertexConsumers) {
            final boolean enabled = this.enabled.getAsBoolean();
            hovered = enabled && isIn(mouseX, mouseY, hovered ? hoveredCorners : corners);
            if (hovered) {
                textRenderer.renderTooltip(matrices, tooltip, mouseX, mouseY);
            }
            final VertexConsumer positionColourConsumer = vertexConsumers.getBuffer(GuiRenderLayers.POSITION_COLOUR_TRANSPARENT_LAYER);
            final Vec2d[] c = hovered ? hoveredCorners : corners;
            final int packedColour = hovered ? hoveredColour.pack(hoveredAlpha) : colour.pack(alpha);
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
            textRenderer.renderText(matrices, name.get(), 0, 0, maxWidth, maxHeight);
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

    private record PartialEntry(Colour colour, int alpha, Colour hoveredColour, int hoveredAlpha, Supplier<Text> name,
                                List<TooltipComponent> tooltip, BooleanSupplier enabled, Runnable action) {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final List<PartialEntry> partialEntries;

        private Builder() {
            partialEntries = new ArrayList<>(8);
        }

        public Builder addEntry(final Colour colour, final int alpha, final Colour hoveredColour, final int hoveredAlpha, final Supplier<Text> name, final List<TooltipComponent> tooltip, final BooleanSupplier enabled, final Runnable action) {
            partialEntries.add(new PartialEntry(colour, alpha, hoveredColour, hoveredAlpha, name, tooltip, enabled, action));
            return this;
        }

        public SelectionWheelWidget build(final double innerDiameter, final double outerDiameter, final double hoverDiameter, final WidgetPosition position) {
            final int size = partialEntries.size();
            final List<Entry> entries = new ArrayList<>(size);
            if (size < 3) {
                if (size == 1) {
                    entries.add(createSingle(partialEntries.get(0), outerDiameter, hoverDiameter));
                } else {
                    entries.add(createDouble(partialEntries.get(0), 0, outerDiameter, hoverDiameter));
                    entries.add(createDouble(partialEntries.get(1), 1, outerDiameter, hoverDiameter));
                }
                return new SelectionWheelWidget(position, entries);
            }
            final double denominator = (Math.PI * 2) / size;
            for (int i = 0; i < size; i++) {
                final PartialEntry partial = partialEntries.get(i);
                final double a1 = i * denominator;
                final double a2 = (i + 1) * denominator;
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
                entries.add(new Entry(new Vec2d[]{first, second, third, fourth}, new Vec2d[]{first, secondHover, thirdHover, fourth}, (a1 + a2 + Math.PI) / 2, partial.colour, partial.alpha, partial.hoveredColour, partial.hoveredAlpha, partial.name, partial.tooltip, partial.enabled, partial.action));
            }
            return new SelectionWheelWidget(position, entries);
        }

        private static Entry createSingle(final PartialEntry partial, final double outerDiameter, final double hoverDiameter) {
            final Vec2d first = new Vec2d(outerDiameter * SQRT_2, outerDiameter * SQRT_2);
            final Vec2d second = new Vec2d(outerDiameter * SQRT_2, outerDiameter * -SQRT_2);
            final Vec2d third = new Vec2d(outerDiameter * -SQRT_2, outerDiameter * -SQRT_2);
            final Vec2d fourth = new Vec2d(outerDiameter * -SQRT_2, outerDiameter * SQRT_2);
            final Vec2d firstHover = new Vec2d(hoverDiameter * SQRT_2, hoverDiameter * SQRT_2);
            final Vec2d secondHover = new Vec2d(hoverDiameter * SQRT_2, hoverDiameter * -SQRT_2);
            final Vec2d thirdHover = new Vec2d(hoverDiameter * -SQRT_2, hoverDiameter * -SQRT_2);
            final Vec2d fourthHover = new Vec2d(hoverDiameter * -SQRT_2, hoverDiameter * SQRT_2);
            return new Entry(new Vec2d[]{first, second, third, fourth}, new Vec2d[]{firstHover, secondHover, thirdHover, fourthHover}, 0, partial.colour, partial.alpha, partial.hoveredColour, partial.hoveredAlpha, partial.name, partial.tooltip, partial.enabled, partial.action);
        }

        private static Entry createDouble(final PartialEntry partial, final int index, final double outerDiameter, final double hoverDiameter) {
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
            return new Entry(new Vec2d[]{first, second, third, fourth}, new Vec2d[]{firstHover, secondHover, thirdHover, fourthHover}, 0, partial.colour, partial.alpha, partial.hoveredColour, partial.hoveredAlpha, partial.name, partial.tooltip, partial.enabled, partial.action);
        }
    }
}
