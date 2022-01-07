package io.github.stuff_stuffs.tbcexgui.client.widget.interaction;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiInputContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiQuadEmitter;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiRenderMaterial;
import io.github.stuff_stuffs.tbcexgui.client.api.text.TextDrawer;
import io.github.stuff_stuffs.tbcexgui.client.api.text.TextDrawers;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import io.github.stuff_stuffs.tbcexutil.common.Vec2d;
import io.github.stuff_stuffs.tbcexutil.common.colour.Colour;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.apache.commons.lang3.mutable.MutableObject;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class CycleWheelWidget extends AbstractWidget {
    private static final double SQRT_2 = Math.sqrt(2);
    private final List<Entry<?>> entries;

    public CycleWheelWidget(final List<Entry<?>> entries) {
        this.entries = entries;
    }

    @Override
    public void render(final GuiContext context) {
        for (final Entry<?> entry : entries) {
            entry.render(context);
        }
        final GuiInputContext inputContext = context.getInputContext();
        try (final GuiInputContext.EventIterator events = inputContext.getEvents()) {
            GuiInputContext.InputEvent event = events.next();
            while (event != null) {
                if (event instanceof GuiInputContext.MouseClick click) {
                    final Vec2d mouseCursor = context.transformMouseCursor(new Vec2d(click.mouseX, click.mouseY));
                    for (final Entry<?> entry : entries) {
                        if (entry.mouseClicked(mouseCursor.x, mouseCursor.y, click.button)) {
                            events.consume();
                            return;
                        }
                    }
                }
                event = events.next();
            }
        } catch (final Exception e) {
            throw new TBCExException("Error while processing gui events", e);
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

    public static Builder builder() {
        return new Builder();
    }

    private static final class Entry<T> {
        private static final TextDrawer TEXT_DRAWER = TextDrawers.lineBreaking(TextDrawers.HorizontalJustification.CENTER, -1, 0, false);
        private static final TextDrawer TEXT_DRAWER_SHADOWED = TextDrawers.lineBreaking(TextDrawers.HorizontalJustification.CENTER, -1, 0, true);
        private final Vec2d[] corners;
        private final Vec2d[] hoveredCorners;
        private final double angle;
        private final Colour colour;
        private final int alpha;
        private final Colour hoveredColour;
        private final int hoveredAlpha;
        private final Function<T, Text> name;
        private final Function<T, List<OrderedText>> tooltip;
        private final BooleanSupplier enabled;
        private final UnaryOperator<T> next;
        private boolean hovered = false;
        private T value;

        private Entry(final Vec2d[] corners, final Vec2d[] hoveredCorners, final double angle, final Colour colour, final int alpha, final Colour hoveredColour, final int hoveredAlpha, final Function<T, Text> name, final Function<T, List<OrderedText>> tooltip, final BooleanSupplier enabled, final UnaryOperator<T> next, final T value) {
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
            this.next = next;
            this.value = value;
        }

        public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
            if (enabled.getAsBoolean() && isIn(mouseX, mouseY, hovered ? hoveredCorners : corners)) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    value = next.apply(value);
                    return true;
                }
                return true;
            }
            return false;
        }

        public void render(final GuiContext context) {
            final Vec2d mouse = context.transformMouseCursor();
            final double mouseX = mouse.x;
            final double mouseY = mouse.y;
            final boolean enabled = this.enabled.getAsBoolean();
            hovered = enabled && isIn(mouseX, mouseY, hovered ? hoveredCorners : corners);
            if (hovered) {
                context.addTooltip(tooltip.apply(value));
            }
            final GuiRenderMaterial material = GuiRenderMaterial.POS_COLOUR_TRANSLUCENT;
            final GuiQuadEmitter emitter = context.getEmitter();
            final Vec2d[] c = hovered ? hoveredCorners : corners;
            final int packedColour = hovered ? hoveredColour.pack(hoveredAlpha) : colour.pack(alpha);
            int index = 0;
            emitter.renderMaterial(material);
            for (final Vec2d first : c) {
                emitter.pos(index, (float) first.x, (float) first.y);
                emitter.colour(index, packedColour);
                index++;
            }
            emitter.emit();
            Vec2d avg = Vec2d.ZERO;
            for (final Vec2d d : c) {
                avg = avg.add(d);
            }
            avg = avg.scale(0.25);
            context.pushTranslate(avg.x, avg.y, 0);
            double angNorm = angle + Math.PI / 2.0;
            angNorm = angNorm % Math.PI;
            angNorm = angNorm - Math.PI / 2.0;
            context.pushRotate(Vec3f.POSITIVE_Z.getRadialQuaternion((float) angNorm));
            final Vec2d deltaX = c[0].add(c[3].scale(-1));
            double maxWidth = deltaX.dot(deltaX);
            maxWidth = maxWidth * MathHelper.fastInverseSqrt(maxWidth);
            final Vec2d deltaY = c[0].add(c[1].scale(-1));
            double maxHeight = deltaY.dot(deltaY);
            maxHeight = 1 / MathHelper.fastInverseSqrt(maxHeight);
            if (hovered) {
                TEXT_DRAWER_SHADOWED.draw(maxWidth, maxHeight, name.apply(value).asOrderedText(), context);
            } else {
                TEXT_DRAWER.draw(maxWidth, maxHeight, name.apply(value).asOrderedText(), context);
            }
            context.popGuiTransform();
            context.popGuiTransform();
        }
    }

    private record PartialEntry<T>(Colour colour, int alpha, Colour hoveredColour, int hoveredAlpha,
                                   Function<T, Text> name, Function<T, List<OrderedText>> tooltip,
                                   BooleanSupplier enabled, T startingValue, UnaryOperator<T> cycle,
                                   MutableObject<Supplier<T>> getter) {
    }

    public static final class Builder {
        private final List<PartialEntry<?>> partialEntries;

        private Builder() {
            partialEntries = new ArrayList<>(8);
        }

        public <T> Supplier<T> addEntry(final Colour colour, final int alpha, final Colour hoveredColour, final int hoveredAlpha, final Function<T, Text> name, final Function<T, List<OrderedText>> tooltip, final BooleanSupplier enabled, final T startingValue, final UnaryOperator<T> cycle) {
            final PartialEntry<T> entry = new PartialEntry<>(colour, alpha, hoveredColour, hoveredAlpha, name, tooltip, enabled, startingValue, cycle, new MutableObject<>());
            partialEntries.add(entry);
            return () -> entry.getter.getValue().get();
        }

        public CycleWheelWidget build(final double innerDiameter, final double outerDiameter, final double hoverDiameter) {
            final int size = partialEntries.size();
            final List<Entry<?>> entries = new ArrayList<>(size);
            if (size < 3) {
                if (size == 1) {
                    entries.add(createSingle(partialEntries.get(0), innerDiameter, outerDiameter));
                } else {
                    entries.add(createDouble(partialEntries.get(0), 0, outerDiameter, hoverDiameter));
                    entries.add(createDouble(partialEntries.get(1), 1, outerDiameter, hoverDiameter));
                }
                return new CycleWheelWidget(entries);
            }
            final double denominator = (Math.PI * 2) / size;
            for (int i = 0; i < size; i++) {
                final PartialEntry<?> partial = partialEntries.get(i);
                entries.add(createEntry(partial, i, innerDiameter, outerDiameter, hoverDiameter, denominator));
            }
            return new CycleWheelWidget(entries);
        }

        private static <T> Entry<T> createEntry(final PartialEntry<T> partial, final int i, final double innerDiameter, final double outerDiameter, final double hoverDiameter, final double denominator) {
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
            final Entry<T> entry = new Entry<>(new Vec2d[]{first, second, third, fourth}, new Vec2d[]{first, secondHover, thirdHover, fourth}, (a1 + a2 + Math.PI) / 2, partial.colour, partial.alpha, partial.hoveredColour, partial.hoveredAlpha, partial.name, partial.tooltip, partial.enabled, partial.cycle, partial.startingValue);
            partial.getter.setValue(() -> entry.value);
            return entry;
        }

        private static <T> Entry<T> createSingle(final PartialEntry<T> partial, final double innerDiameter, final double outerDiameter) {
            final Vec2d first = new Vec2d(innerDiameter * SQRT_2, innerDiameter * SQRT_2);
            final Vec2d second = new Vec2d(innerDiameter * SQRT_2, innerDiameter * -SQRT_2);
            final Vec2d third = new Vec2d(innerDiameter * -SQRT_2, innerDiameter * -SQRT_2);
            final Vec2d fourth = new Vec2d(innerDiameter * -SQRT_2, innerDiameter * SQRT_2);
            final Vec2d firstHover = new Vec2d(outerDiameter * SQRT_2, outerDiameter * SQRT_2);
            final Vec2d secondHover = new Vec2d(outerDiameter * SQRT_2, outerDiameter * -SQRT_2);
            final Vec2d thirdHover = new Vec2d(outerDiameter * -SQRT_2, outerDiameter * -SQRT_2);
            final Vec2d fourthHover = new Vec2d(outerDiameter * -SQRT_2, outerDiameter * SQRT_2);
            final Entry<T> entry = new Entry<>(new Vec2d[]{first, second, third, fourth}, new Vec2d[]{firstHover, secondHover, thirdHover, fourthHover}, 0, partial.colour, partial.alpha, partial.hoveredColour, partial.hoveredAlpha, partial.name, partial.tooltip, partial.enabled, partial.cycle, partial.startingValue);
            partial.getter.setValue(() -> entry.value);
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
            final Entry<T> entry = new Entry<>(new Vec2d[]{first, second, third, fourth}, new Vec2d[]{firstHover, secondHover, thirdHover, fourthHover}, 0, partial.colour, partial.alpha, partial.hoveredColour, partial.hoveredAlpha, partial.name, partial.tooltip, partial.enabled, partial.cycle, partial.startingValue);
            partial.getter.setValue(() -> entry.value);
            return entry;
        }
    }
}
