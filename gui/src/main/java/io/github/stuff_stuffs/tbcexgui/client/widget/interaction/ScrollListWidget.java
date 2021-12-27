package io.github.stuff_stuffs.tbcexgui.client.widget.interaction;

import io.github.stuff_stuffs.tbcexgui.client.render.GuiRenderLayers;
import io.github.stuff_stuffs.tbcexgui.client.render.ScissorStack;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexutil.client.RenderUtil;
import io.github.stuff_stuffs.tbcexutil.common.Rect2d;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import io.github.stuff_stuffs.tbcexutil.common.colour.Colour;
import io.github.stuff_stuffs.tbcexutil.common.colour.IntRgbColour;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class ScrollListWidget extends AbstractWidget {
    private final WidgetPosition position;
    private final double width;
    private final double height;
    private final double entryHeight;
    private final double gap;
    private final List<Entry> entries;
    private int selected;
    private double scrollPos;

    private ScrollListWidget(final WidgetPosition position, final double width, final double height, final double entryHeight, final double gap, final List<Entry> entries, final int selected) {
        this.position = position;
        this.width = width;
        this.height = height;
        this.entryHeight = entryHeight;
        this.gap = gap;
        this.entries = entries;
        setSelected(selected);
    }

    public void setScrollPos(final double scrollPos) {
        if (scrollPos < 0) {
            this.scrollPos = 0;
        } else {
            final double max = (entryHeight + gap) * entries.size() - height;
            this.scrollPos = Math.min(scrollPos, Math.max(max, 0));
        }
    }

    public void setSelected(final int selected) {
        if (selected != this.selected) {
            if (this.selected != -1) {
                entries.get(this.selected).action.onDeselected();
            }
            if (selected < 0) {
                this.selected = -1;
            } else if (selected > entries.size()) {
                this.selected = -1;
            } else {
                this.selected = selected;
            }
            if (this.selected != -1) {
                entries.get(this.selected).action.onSelected();
            }
        }
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return position;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        final double x = position.getX();
        final double y = position.getY();
        if (new Rect2d(x, y, x + width, y + height).isIn(mouseX, mouseY)) {
            final int hoverIndex = findHoverIndex(mouseX, mouseY);
            setSelected(hoverIndex);
        }
        return false;
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        return false;
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        final double x = position.getX();
        final double y = position.getY();
        if (new Rect2d(x, y, x + width, y + height).isIn(mouseX, mouseY)) {
            setScrollPos(scrollPos + deltaY);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        final double x = position.getX();
        final double y = position.getY();
        if (new Rect2d(x, y, x + width, y + height).isIn(mouseX, mouseY)) {
            setScrollPos(scrollPos + amount);
            return true;
        }
        return false;
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        final double x = position.getX();
        final double y = position.getY();
        final int hoverIndex = findHoverIndex(mouseX, mouseY);
        if (hoverIndex > -1) {
            renderTooltip(matrices, entries.get(hoverIndex).tooltip.get(), mouseX, mouseY);
        }
        ScissorStack.push(matrices, x, y, x + width, y + height);
        matrices.push();
        matrices.translate(0, -scrollPos, 0);
        render(vertexConsumers -> {
            for (int i = 0, entriesSize = entries.size(); i < entriesSize; i++) {
                final Entry entry = entries.get(i);
                final Colour colour;
                final int alpha;
                if (i == selected) {
                    colour = entry.selectedColour.get();
                    alpha = entry.selectedAlpha.getAsInt();
                } else if (i == hoverIndex) {
                    colour = entry.hoverColour.get();
                    alpha = entry.hoverAlpha.getAsInt();
                } else {
                    colour = entry.colour.get();
                    alpha = entry.alpha.getAsInt();
                }
                final double yPos = y + (gap + entryHeight) * i;
                RenderUtil.renderRectangle(matrices, x, yPos, width * 0.95, entryHeight, colour, alpha, vertexConsumers.getBuffer(alpha != 255 ? GuiRenderLayers.POSITION_COLOUR_TRANSPARENT_LAYER : GuiRenderLayers.POSITION_COLOUR_LAYER));
                matrices.translate(0, 0, 1);
                renderFitTextWrap(matrices, entry.name.get(), x, yPos, width * 0.95, entryHeight, i == hoverIndex, Colour.WHITE, 255, vertexConsumers);
                matrices.translate(0, 0, -1);
            }
        });
        matrices.pop();
        ScissorStack.pop();
    }

    private int findHoverIndex(final double mouseX, final double mouseY) {
        final double offsetX = position.getX();
        final double offsetY = position.getY();
        final double startX = offsetX;
        final double endX = offsetX + width * 0.95;
        for (int index = 0; index < entries.size(); index++) {
            final double startY = offsetY + index * (entryHeight + gap);
            final double endY = startY + entryHeight;
            if (new Rect2d(startX, startY, endX, endY).isIn(mouseX, mouseY)) {
                return index;
            }
        }
        return -1;
    }

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_DOWN) {
            if (selected < entries.size() - 1) {
                setSelected(selected + 1);
            }
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_UP) {
            if (selected > 0) {
                setSelected(selected - 1);
            }
            return true;
        }
        return false;
    }

    public static Builder builder() {
        return new Builder();
    }

    private record Entry(Supplier<Text> name,
                         Supplier<List<TooltipComponent>> tooltip,
                         Supplier<Colour> colour,
                         IntSupplier alpha,
                         Supplier<Colour> hoverColour,
                         IntSupplier hoverAlpha,
                         Supplier<Colour> selectedColour,
                         IntSupplier selectedAlpha,
                         Action action) {
    }

    public interface Action {
        void onSelected();

        void onDeselected();
    }

    public static final class Builder {
        private final List<Entry> entries = new ArrayList<>(1);

        public EntryBuilder addEntry() {
            return new EntryBuilder();
        }

        public ScrollListWidget build(final WidgetPosition position, final double width, final double height, final double entryHeight, final double gap) {
            return build(position, width, height, entryHeight, gap, -1);
        }

        public ScrollListWidget build(final WidgetPosition position, final double width, final double height, final double entryHeight, final double gap, final int selected) {
            if (entries.size() == 0) {
                throw new TBCExException("Can't create scroll list with zero size");
            }
            return new ScrollListWidget(position, width, height, entryHeight, gap, new ArrayList<>(entries), selected);
        }

        public final class EntryBuilder {
            private static final Colour DEFAULT = new IntRgbColour(17, 17, 17);
            private static final Colour DEFAULT_HIGHLIGHT = new IntRgbColour(34, 34, 34);
            private boolean built = false;
            private Supplier<Colour> colour = () -> DEFAULT;
            private IntSupplier alpha = () -> 255;
            private Supplier<Colour> hoverColour = () -> DEFAULT_HIGHLIGHT;
            private IntSupplier hoverAlpha = () -> 255;
            private Supplier<Colour> selectedColour = () -> DEFAULT_HIGHLIGHT;
            private IntSupplier selectedAlpha = () -> 255;

            private EntryBuilder() {
            }

            public EntryBuilder colour(final Colour colour) {
                return colour(() -> colour);
            }

            public EntryBuilder colour(final Supplier<Colour> colour) {
                this.colour = colour;
                return this;
            }

            public EntryBuilder alpha(final int alpha) {
                return alpha(() -> alpha);
            }

            public EntryBuilder alpha(final IntSupplier alpha) {
                this.alpha = alpha;
                return this;
            }

            public EntryBuilder hoverColour(final Colour hoverColour) {
                return hoverColour(() -> hoverColour);
            }

            public EntryBuilder hoverColour(final Supplier<Colour> hoverColour) {
                this.hoverColour = hoverColour;
                return this;
            }

            public EntryBuilder hoverAlpha(final int hoverAlpha) {
                return hoverAlpha(() -> hoverAlpha);
            }

            public EntryBuilder hoverAlpha(final IntSupplier hoverAlpha) {
                this.hoverAlpha = hoverAlpha;
                return this;
            }

            public EntryBuilder selectedColour(final Colour selectedColour) {
                return selectedColour(() -> selectedColour);
            }

            public EntryBuilder selectedColour(final Supplier<Colour> selectedColour) {
                this.selectedColour = selectedColour;
                return this;
            }

            public EntryBuilder selectedAlpha(final int selectedAlpha) {
                return selectedAlpha(() -> selectedAlpha);
            }

            public EntryBuilder selectedAlpha(final IntSupplier selectedAlpha) {
                this.selectedAlpha = selectedAlpha;
                return this;
            }

            public Builder build(final Text name, final Supplier<List<TooltipComponent>> tooltip, final Action action) {
                return build(() -> name, tooltip, action);
            }

            public Builder build(final Supplier<Text> name, final List<TooltipComponent> tooltip, final Action action) {
                return build(name, () -> tooltip, action);
            }

            public Builder build(final Text name, final List<TooltipComponent> tooltip, final Action action) {
                return build(() -> name, () -> tooltip, action);
            }

            public Builder build(final Supplier<Text> name, final Supplier<List<TooltipComponent>> tooltip, final Action action) {
                if (built) {
                    throw new TBCExException("Tried to reuse builder");
                }
                built = true;
                entries.add(new Entry(name, tooltip, colour, alpha, hoverColour, hoverAlpha, selectedColour, selectedAlpha, action));
                return Builder.this;
            }
        }

        private Builder() {
        }
    }
}
