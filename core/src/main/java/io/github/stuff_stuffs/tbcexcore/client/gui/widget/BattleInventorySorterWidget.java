package io.github.stuff_stuffs.tbcexcore.client.gui.widget;

import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.tbcexcore.client.util.ItemStackInfo;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantInventoryHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
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
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.DoubleSupplier;
import java.util.function.IntConsumer;

public class BattleInventorySorterWidget extends AbstractWidget implements PositionedWidget {
    private static final Comparator<ItemStackInfo> DEFAULT_COMPARATOR = (first, second) -> {
        final Optional<Pair<BattleParticipantHandle, BattleEquipmentSlot>> firstRight = first.location.right();
        final Optional<Pair<BattleParticipantHandle, BattleEquipmentSlot>> secondRight = second.location.right();
        if (firstRight.isPresent() && secondRight.isPresent()) {
            return Integer.compare(BattleEquipmentSlot.REGISTRY.getRawId(firstRight.get().getSecond()), BattleEquipmentSlot.REGISTRY.getRawId(secondRight.get().getSecond()));
        }
        final Optional<BattleParticipantInventoryHandle> firstLeft = first.location.left();
        final Optional<BattleParticipantInventoryHandle> secondLeft = second.location.left();
        if (firstLeft.isPresent()) {
            if (secondLeft.isPresent()) {
                return Integer.compare(firstLeft.get().id(), secondLeft.get().id());
            } else {
                return 1;
            }
        }
        return -1;
    };
    private static final Comparator<ItemStackInfo> ALPHABETICAL_COMPARATOR = Comparator.comparing(o -> o.stack.getItem().getName().getString());
    private static final Comparator<ItemStackInfo> COUNT_COMPARATOR = Comparator.comparingInt(o -> o.stack.getCount());
    private static final Comparator<ItemStackInfo> RARITY_COMPARATOR = Comparator.<ItemStackInfo>comparingInt(o -> o.stack.getItem().getRarity().getRarity().ordinal()).thenComparingDouble(o -> o.stack.getItem().getRarity().getProgress());
    public static final List<Sort> DEFAULTS = Util.make(new ArrayList<>(), list -> {
        list.add(new Sort() {
            @Override
            public Comparator<ItemStackInfo> getComparator() {
                return ALPHABETICAL_COMPARATOR;
            }

            @Override
            public Text getName() {
                return new LiteralText("ALPHABETICAL");
            }
        });
        list.add(new Sort() {
            @Override
            public Comparator<ItemStackInfo> getComparator() {
                return COUNT_COMPARATOR;
            }

            @Override
            public Text getName() {
                return new LiteralText("COUNT");
            }
        });
        list.add(new Sort() {
            @Override
            public Comparator<ItemStackInfo> getComparator() {
                return RARITY_COMPARATOR;
            }

            @Override
            public Text getName() {
                return new LiteralText("RARITY");
            }
        });
    });
    private static final TextDrawer TEXT_DRAWER = TextDrawers.oneShot(TextDrawers.HorizontalJustification.CENTER, TextDrawers.VerticalJustification.CENTER, -1, 0, false);
    private static final TextDrawer TEXT_DRAWER_SHADOWED = TextDrawers.oneShot(TextDrawers.HorizontalJustification.CENTER, TextDrawers.VerticalJustification.CENTER, -1, 0, true);
    private final DoubleSupplier x;
    private final DoubleSupplier y;
    private final DoubleSupplier width;
    private final DoubleSupplier height;
    private final double borderThickness;
    private final double entryWidth;
    private final double horizontalSpacing;
    private final List<Sort> sorts;
    private final IntConsumer onSelect;
    private double pos = 0;
    private int selectedIndex = 0;
    private boolean reversedSort = false;
    private int prevSelectedIndex = -1;
    private boolean prevReversedSort = false;


    public BattleInventorySorterWidget(final DoubleSupplier x, final DoubleSupplier y, final DoubleSupplier width, final DoubleSupplier height, final double borderThickness, final double entryWidth, final double horizontalSpacing, final List<Sort> sorts, final IntConsumer onSelect) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.borderThickness = borderThickness;
        this.entryWidth = entryWidth;
        this.horizontalSpacing = horizontalSpacing;
        this.sorts = sorts;
        this.onSelect = onSelect;
    }

    public void sort(final List<ItemStackInfo> infos) {
        if (0 <= selectedIndex && selectedIndex < sorts.size()) {
            Comparator<ItemStackInfo> first = sorts.get(selectedIndex).getComparator();
            if (reversedSort) {
                first = first.reversed();
            }
            Comparator<ItemStackInfo> second;
            if (0 <= prevSelectedIndex && prevSelectedIndex < sorts.size()) {
                second = sorts.get(prevSelectedIndex).getComparator();
            } else {
                second = DEFAULT_COMPARATOR;
            }
            if (prevReversedSort) {
                second = second.reversed();
            }
            infos.sort(first.thenComparing(second));
        } else {
            infos.sort(DEFAULT_COMPARATOR);
        }
    }

    public void setSelectedIndex(final int selectedIndex) {
        if (0 <= selectedIndex && selectedIndex < sorts.size()) {
            if (selectedIndex != this.selectedIndex) {
                //fixme
                //pos = (position.getX() + selectedIndex * entryWidth + selectedIndex * horizontalSpacing + entryWidth / 2) - (width.getAsDouble() - 2 * borderThickness) / 2;
                prevSelectedIndex = this.selectedIndex;
                prevReversedSort = reversedSort;
                this.selectedIndex = selectedIndex;
            } else {
                reversedSort = !reversedSort;
            }
            onSelect.accept(selectedIndex);
        } else {
            this.selectedIndex = -1;
            onSelect.accept(-1);
        }
    }

    private boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (new Rect2d(0, 0, width.getAsDouble(), height.getAsDouble()).isIn(mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            final int index = findHoverIndex(mouseX + pos, mouseY);
            setSelectedIndex(index);
            return true;
        }
        return false;
    }

    private boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        final double width = this.width.getAsDouble();
        if (new Rect2d(0, 0, width, height.getAsDouble()).isIn(mouseX, mouseY)) {
            pos = Math.min(Math.max(pos + deltaX, -(width - 2 * borderThickness) / 2 + entryWidth), getListWidth() - (width - 2 * borderThickness) / 2);
            return true;
        }
        return false;
    }

    private boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        final double width = this.width.getAsDouble();
        if (new Rect2d(0, 0, width, height.getAsDouble()).isIn(mouseX, mouseY)) {
            pos = Math.min(Math.max(pos + amount, -(width - 2 * borderThickness) / 2 + entryWidth), getListWidth() - (width - 2 * borderThickness) / 2);
            return true;
        }
        return false;
    }

    @Override
    public void render(final GuiContext guiContext) {
        processEvents(guiContext, event -> {
            if (event instanceof GuiInputContext.MouseClick click) {
                final Vec2d mouseCursor = guiContext.transformMouseCursor(new Vec2d(click.mouseX, click.mouseY));
                return mouseClicked(mouseCursor.x, mouseCursor.y, click.button);
            } else if (event instanceof GuiInputContext.MouseDrag drag) {
                final Vec2d mouse = guiContext.transformMouseCursor(new Vec2d(drag.mouseX, drag.mouseY));
                final Vec2d delta = guiContext.transformMouseCursor(new Vec2d(drag.mouseX + drag.deltaX, drag.mouseY + drag.deltaY)).subtract(mouse);
                return mouseDragged(mouse.x, mouse.y, drag.button, delta.x, delta.y);
            } else if (event instanceof GuiInputContext.MouseScroll scroll) {
                final Vec2d mouse = guiContext.transformMouseCursor(new Vec2d(scroll.mouseX, scroll.mouseY));
                final Vec2d delta = guiContext.transformMouseCursor(new Vec2d(scroll.mouseX, scroll.mouseY + scroll.amount)).subtract(mouse);
                return mouseScrolled(mouse.x, mouse.y, delta.y);
            } else if (event instanceof GuiInputContext.KeyPress keyPress) {
                return keyPress(keyPress.keyCode);
            }
            return false;
        });

        final GuiQuadEmitter emitter = guiContext.getEmitter();
        final double offsetX = 0;
        final double offsetY = 0;
        final double width = this.width.getAsDouble();
        final double height = this.height.getAsDouble();
        final int c = IntRgbColour.BLACK.pack(127);
        emitter.colour(c, c, c, c);
        emitter.pos(0, (float) (offsetX + width), (float) offsetY);
        emitter.pos(1, (float) offsetX, (float) offsetY);
        emitter.pos(2, (float) offsetX, (float) (offsetY + height));
        emitter.pos(3, (float) (offsetX + width), (float) (offsetY + height));
        emitter.emit();

        guiContext.pushTranslate(-pos, 0, 0);
        final Vec2d mouseCursor = guiContext.transformMouseCursor();
        final int hoverIndex = findHoverIndex(mouseCursor.x, mouseCursor.y);
        for (int i = 0; i < sorts.size(); i++) {
            renderInfo(sorts.get(i), guiContext, i, hoverIndex);
        }

        for (int i = 0; i < sorts.size(); i++) {
            renderDecorations(sorts.get(i), guiContext, i, hoverIndex);
        }

        guiContext.popGuiTransform();
    }

    private void renderDecorations(final Sort sort, final GuiContext context, final int index, final int hoverIndex) {
        final float offsetX = 0;
        final float offsetY = 0;
        final double startX = offsetX + borderThickness + index * entryWidth + index * horizontalSpacing;
        final float endX = (float) (offsetX + borderThickness + index * entryWidth + index * horizontalSpacing + entryWidth);
        final double y = offsetY + borderThickness;
        final double centerX = (startX + endX) / 2.0;
        double dist = Math.abs(centerX - (pos + (width.getAsDouble() - 2 * borderThickness) / 2));
        dist *= dist * dist;
        final double offset = width.getAsDouble() / 8;
        final double scale = Math.max(offset - dist, 0) / offset;
        final boolean shadow = index == hoverIndex || selectedIndex == index;
        final TextDrawer textDrawer = shadow ? TEXT_DRAWER_SHADOWED : TEXT_DRAWER;
        final double width = (endX - startX) * scale;
        final double height = (this.height.getAsDouble() - 2 * borderThickness) * scale;
        context.pushTranslate(startX + width / 2.0, y + height / 2.0, 0);
        textDrawer.draw(width, height, sort.getName().asOrderedText(), context);
        context.popGuiTransform();
    }

    private void renderInfo(final Sort category, final GuiContext context, final int index, final int hoverIndex) {
        final double offsetX = 0;
        final double offsetY = 0;
        final float startX = (float) (offsetX + borderThickness + index * entryWidth + index * horizontalSpacing);
        final float endX = (float) (offsetX + borderThickness + index * entryWidth + index * horizontalSpacing + entryWidth);
        final float xLen = (endX - startX);
        final float startY = (float) (offsetY + borderThickness);
        final float endY = (float) (offsetY + height.getAsDouble() - borderThickness);
        final float centerX = (startX + endX) / 2f;
        final float yLen = (endY - startY);
        float dist = Math.abs(centerX - (float) (pos + (width.getAsDouble() - 2 * borderThickness) / 2));
        dist *= dist * dist;
        final float offset = ((float) width.getAsDouble()) / 8f;
        final float scale = Math.max(offset - dist, 0) / offset;
        final Colour backgroundColour = getBackgroundColour(index);
        int alpha;
        if (hoverIndex == index || selectedIndex == index) {
            alpha = 0xFF;
        } else {
            alpha = 0x77;
        }
        alpha = Math.round(alpha * scale);
        final GuiQuadEmitter emitter = context.getEmitter();
        emitter.renderMaterial(GuiRenderMaterial.POS_COLOUR_TRANSLUCENT);
        emitter.pos(0, startX + xLen * scale, startY);
        emitter.pos(1, startX, startY);
        emitter.pos(2, startX, startY + yLen * scale);
        emitter.pos(3, startX + xLen * scale, startY + yLen * scale);
        final int c = backgroundColour.pack(alpha);
        emitter.colour(c, c, c, c);
    }

    private boolean keyPress(final int keyCode) {
        if (keyCode == GLFW.GLFW_KEY_LEFT) {
            setSelectedIndex(selectedIndex - 1);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            setSelectedIndex(selectedIndex + 1);
            return true;
        }
        return false;
    }

    private int findHoverIndex(final double mouseX, final double mouseY) {
        final double offsetX = 0;
        final double offsetY = 0;
        final double height = this.height.getAsDouble();
        for (int index = 0; index < sorts.size(); index++) {
            final double startX = offsetX + borderThickness + index * entryWidth + index * horizontalSpacing;
            final double endX = startX + entryWidth;
            final double startY = offsetY + borderThickness;
            final double endY = offsetY + height - borderThickness;
            if (new Rect2d(startX, startY, endX, endY).isIn(mouseX, mouseY)) {
                return index;
            }
        }
        return -1;
    }

    private double getListWidth() {
        final int size = sorts.size();
        return size * entryWidth + size * horizontalSpacing;
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

    public interface Sort {
        Comparator<ItemStackInfo> getComparator();

        Text getName();
    }
}
