package io.github.stuff_stuffs.tbcexcore.client.gui.widget;

import io.github.stuff_stuffs.tbcexcore.client.util.ItemStackInfo;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiInputContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiQuadEmitter;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiRenderMaterial;
import io.github.stuff_stuffs.tbcexgui.client.api.text.TextDrawer;
import io.github.stuff_stuffs.tbcexgui.client.api.text.TextDrawers;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.PositionedWidget;
import io.github.stuff_stuffs.tbcexutil.common.Rect2d;
import io.github.stuff_stuffs.tbcexutil.common.Vec2d;
import io.github.stuff_stuffs.tbcexutil.common.colour.Colour;
import io.github.stuff_stuffs.tbcexutil.common.colour.IntRgbColour;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

public class BattleInventoryTabWidget extends AbstractWidget implements PositionedWidget {
    public static final int COLUMN_COUNT = 3;
    private static final Colour EQUIPED_COLOR = new IntRgbColour(200, 31, 0);
    private static final TextDrawer TEXT_DRAWER = TextDrawers.oneShot(TextDrawers.HorizontalJustification.CENTER, TextDrawers.VerticalJustification.CENTER, -1, 0, false);
    private static final TextDrawer TEXT_DRAWER_SHADOWED = TextDrawers.oneShot(TextDrawers.HorizontalJustification.CENTER, TextDrawers.VerticalJustification.CENTER, -1, 0, true);
    private final DoubleSupplier x;
    private final DoubleSupplier y;
    private final Supplier<List<ItemStackInfo>> stacks;
    private final double borderThickness;
    private final double entryHeight;
    private final double verticalSpacing;
    private final DoubleSupplier width;
    private final DoubleSupplier height;
    private final IntConsumer onSelect;
    private final DoubleSelect doubleSelect;
    private double pos = 0;
    private int selectedIndex = 0;

    public BattleInventoryTabWidget(final DoubleSupplier x, final DoubleSupplier y, final Supplier<List<ItemStackInfo>> stacks, final double borderThickness, final double entryHeight, final double verticalSpacing, final DoubleSupplier width, final DoubleSupplier height, final IntConsumer onSelect, final DoubleSelect doubleSelect) {
        this.x = x;
        this.y = y;
        this.stacks = stacks;
        this.borderThickness = borderThickness;
        this.entryHeight = entryHeight;
        this.verticalSpacing = verticalSpacing;
        this.width = width;
        this.height = height;
        this.onSelect = onSelect;
        this.doubleSelect = doubleSelect;
    }

    public void resetSelectedIndex() {
        setSelectedIndex(-1, 0, 0);
    }

    public void setSelectedIndex(final int selectedIndex, final double mouseX, final double mouseY) {
        if (0 <= selectedIndex && selectedIndex < stacks.get().size()) {
            if (selectedIndex != this.selectedIndex) {
                this.selectedIndex = selectedIndex;
                onSelect.accept(selectedIndex);
                doubleSelect.onDoubleSelect(-1, 0, 0);
            } else {
                doubleSelect.onDoubleSelect(selectedIndex, mouseX, mouseY);
            }
        } else {
            this.selectedIndex = -1;
            onSelect.accept(selectedIndex);
            doubleSelect.onDoubleSelect(-1, 0, 0);
        }
    }

    private boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (new Rect2d(0, 0, width.getAsDouble(), height.getAsDouble()).isIn(mouseX, mouseY)) {
            final int index = findHoverIndex(mouseX, mouseY + pos);
            setSelectedIndex(index, mouseX, mouseY);
            return true;
        }
        return false;
    }

    private boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        final double height = this.height.getAsDouble();
        if (new Rect2d(0, 0, width.getAsDouble(), height).isIn(mouseX, mouseY)) {
            pos = Math.min(Math.max(pos + deltaY, 0), getListHeight() - (height - 2 * borderThickness));
            return true;
        }
        return false;
    }

    private boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        final double height = this.height.getAsDouble();
        if (new Rect2d(0, 0, width.getAsDouble(), height).isIn(mouseX, mouseY)) {
            pos = Math.min(Math.max(pos - amount, 0), getListHeight() - (height - 2 * borderThickness));
            return true;
        }
        return false;
    }

    @Override
    public void render(final GuiContext context) {
        processEvents(context, event -> {
            if (event instanceof GuiInputContext.MouseClick click) {
                final Vec2d mouse = context.transformMouseCursor(new Vec2d(click.mouseX, click.mouseY));
                return mouseClicked(mouse.x, mouse.y, click.button);
            } else if (event instanceof GuiInputContext.MouseDrag drag) {
                final Vec2d mouse = context.transformMouseCursor(new Vec2d(drag.mouseX, drag.mouseY));
                final Vec2d delta = context.transformMouseCursor(new Vec2d(drag.mouseX + drag.deltaX, drag.mouseY + drag.deltaY)).subtract(mouse);
                return mouseDragged(mouse.x, mouse.y, drag.button, delta.x, delta.y);
            } else if (event instanceof GuiInputContext.MouseScroll scroll) {
                final Vec2d mouse = context.transformMouseCursor(new Vec2d(scroll.mouseX, scroll.mouseY));
                final Vec2d delta = context.transformMouseCursor(new Vec2d(scroll.mouseX, scroll.mouseY + scroll.amount)).subtract(mouse);
                return mouseScrolled(mouse.x, mouse.y, delta.y);
            } else if (event instanceof GuiInputContext.KeyPress keyPress) {
                return keyPress(keyPress.keyCode);
            }
            return false;
        });
        final GuiQuadEmitter emitter = context.getEmitter();
        final double offsetX = 0;
        final double offsetY = 0;
        final double width = this.width.getAsDouble();
        final double height = this.height.getAsDouble();
        emitter.renderMaterial(GuiRenderMaterial.POS_COLOUR_TRANSLUCENT);
        emitter.pos(0, (float) (offsetX + width), (float) offsetY);
        emitter.pos(1, (float) offsetX, (float) offsetY);
        emitter.pos(2, (float) offsetX, (float) (offsetY + height));
        emitter.pos(3, (float) (offsetX + width), (float) (offsetY + height));
        int c = IntRgbColour.BLACK.pack(127);
        emitter.colour(c, c, c, c);
        emitter.emit();
        final double scrollbarThickness = borderThickness / 3.0;
        final double scrollbarHeight = scrollbarThickness * 8;
        final double scrollAreaHeight = height - 2 * borderThickness - scrollbarHeight;
        final double progress = pos / (getListHeight() - (height - 2 * borderThickness));
        emitter.pos(0, (float) (offsetX + scrollbarThickness * 2), (float) (offsetY + borderThickness + progress * scrollAreaHeight));
        emitter.pos(1, (float) (offsetX + scrollbarThickness), (float) (offsetY + borderThickness + progress * scrollAreaHeight));
        emitter.pos(2, (float) (offsetX + scrollbarThickness), (float) (offsetY + borderThickness + progress * scrollAreaHeight + scrollbarHeight));
        emitter.pos(3, (float) (offsetX + scrollbarThickness * 2), (float) (offsetY + borderThickness + progress * scrollAreaHeight + scrollbarHeight));
        c = new IntRgbColour(127, 127, 127).pack(192);
        emitter.colour(c, c, c, c);
        emitter.emit();
        context.pushScissor((float) (borderThickness + offsetX), (float) (borderThickness + offsetY), (float) (offsetX + width - borderThickness), (float) ((offsetY + height - borderThickness) * 1.1));
        context.pushTranslate(0, -pos, 0);
        final Vec2d mouse = context.transformMouseCursor();
        final int hoverIndex = findHoverIndex(mouse.x, mouse.y);
        final List<ItemStackInfo> stacks = this.stacks.get();
        for (int i = 0; i < stacks.size(); i++) {
            renderInfo(stacks.get(i), context, i, hoverIndex);
        }
        for (int i = 0; i < stacks.size(); i++) {
            renderDecorations(stacks.get(i), context, i, hoverIndex);
        }
        context.popGuiTransform();
        context.popGuiTransform();
    }

    private void renderDecorations(final ItemStackInfo info, final GuiContext context, final int index, final int hoverIndex) {
        final double maxWidth = ((width.getAsDouble() - 2 * borderThickness) / (double) COLUMN_COUNT);
        final double y = borderThickness + index * entryHeight + index * verticalSpacing;
        final boolean shadow = index == hoverIndex || selectedIndex == index;
        final Text name = info.stack.getItem().getName();
        context.pushTranslate(borderThickness + maxWidth / 2.0, y + entryHeight / 2.0, 1);
        (shadow ? TEXT_DRAWER_SHADOWED : TEXT_DRAWER).draw(maxWidth, entryHeight, name.asOrderedText(), context);
        context.popGuiTransform();
        context.pushTranslate(borderThickness + maxWidth / 2.0, y + entryHeight / 2.0, 1);
        (shadow ? TEXT_DRAWER_SHADOWED : TEXT_DRAWER).draw(maxWidth, entryHeight, new LiteralText("" + info.stack.getCount()).asOrderedText(), context);
        context.popGuiTransform();
        context.pushTranslate(borderThickness + maxWidth / 2.0, y + entryHeight / 2.0, 1);
        TextDrawers.oneShot(TextDrawers.HorizontalJustification.CENTER, TextDrawers.VerticalJustification.CENTER, new IntRgbColour(info.stack.getItem().getRarity().getRarity().getColour()).pack(255), 0, true).draw(maxWidth, entryHeight, info.stack.getItem().getRarity().getAsText().asOrderedText(), context);
        context.popGuiTransform();
    }

    private void renderInfo(final ItemStackInfo info, final GuiContext context, final int index, final int hoverIndex) {
        final double offsetX = 0;
        final double offsetY = 0;
        final float startX = (float) (offsetX + borderThickness);
        final float endX = (float) (offsetX + width.getAsDouble() - borderThickness);
        final float startY = (float) (offsetY + borderThickness + index * entryHeight + index * verticalSpacing);
        final float endY = (float) (offsetY + borderThickness + index * entryHeight + index * verticalSpacing + entryHeight);
        final Colour backgroundColour = getBackgroundColour(index);
        final int alpha;
        if (hoverIndex == index || selectedIndex == index) {
            alpha = 0xFF;
        } else {
            alpha = 0x77;
        }
        final GuiQuadEmitter emitter = context.getEmitter();
        emitter.renderMaterial(GuiRenderMaterial.POS_COLOUR_TRANSLUCENT);
        info.location.ifLeft(loc -> {
            final int c = backgroundColour.pack(alpha);
            emitter.colour(c, c, c, c);
        }).ifRight(loc -> {
            final int c = EQUIPED_COLOR.pack();
            emitter.colour(c, c, c, c);
        });
        emitter.pos(0, endX, startY);
        emitter.pos(1, startX, startY);
        emitter.pos(2, startX, endY);
        emitter.pos(3, endX, endY);
        emitter.emit();
    }

    private double getListHeight() {
        final int size = stacks.get().size();
        return size * entryHeight + size * verticalSpacing;
    }

    private boolean keyPress(final int keyCode) {
        final double offsetX = 0;
        final double offsetY = 0;
        if (keyCode == GLFW.GLFW_KEY_UP) {
            final int index = selectedIndex - 1;
            final double startX = (offsetX + borderThickness);
            final double endX = (offsetX + width.getAsDouble() - borderThickness);
            final double startY = (offsetY + borderThickness + selectedIndex * entryHeight + index * verticalSpacing);
            final double endY = (offsetY + borderThickness + index * entryHeight + index * verticalSpacing + entryHeight);
            pos = Math.min(Math.max(pos - entryHeight, 0), getListHeight() - (offsetY - 2 * borderThickness));
            setSelectedIndex(index, (startX + endX) / 2, (startY + endY) / 2);
        } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
            final int index = selectedIndex + 1;
            final double startX = (offsetX + borderThickness);
            final double endX = (offsetX + width.getAsDouble() - borderThickness);
            final double startY = (offsetY + borderThickness + selectedIndex * entryHeight + index * verticalSpacing);
            final double endY = (offsetY + borderThickness + index * entryHeight + index * verticalSpacing + entryHeight);
            pos = Math.min(Math.max(pos + entryHeight, 0), getListHeight() - (offsetY - 2 * borderThickness));
            setSelectedIndex(index, (startX + endX) / 2, (startY + endY) / 2);
        } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            final int index = selectedIndex;
            final double startX = (offsetX + borderThickness);
            final double endX = (offsetX + width.getAsDouble() - borderThickness);
            final double startY = (offsetY + borderThickness + selectedIndex * entryHeight + index * verticalSpacing);
            final double endY = (offsetY + borderThickness + index * entryHeight + index * verticalSpacing + entryHeight);
            setSelectedIndex(index, (startX + endX) / 2, (startY + endY) / 2);
        } else {
            return false;
        }
        return true;
    }

    private int findHoverIndex(final double mouseX, final double mouseY) {
        final double offsetX = 0;
        final double offsetY = 0;
        final double width = this.width.getAsDouble();
        for (int index = 0; index < stacks.get().size(); index++) {
            final double startX = offsetX + borderThickness;
            final double endX = offsetX + width - borderThickness;
            final double startY = offsetY + borderThickness + index * entryHeight + index * verticalSpacing;
            final double endY = startY + entryHeight;
            if (new Rect2d(startX, startY, endX, endY).isIn(mouseX, mouseY)) {
                return index;
            }
        }
        return -1;
    }

    @Override
    public double getX() {
        return x.getAsDouble();
    }

    @Override
    public double getY() {
        return y.getAsDouble();
    }

    private static Colour getBackgroundColour(final int index) {
        return (index & 1) == 0 ? BattleInventoryFilterWidget.FIRST_BACKGROUND_COLOUR : BattleInventoryFilterWidget.SECOND_BACKGROUND_COLOUR;
    }

    public interface DoubleSelect {
        void onDoubleSelect(int index, double mouseX, double mouseY);
    }
}
