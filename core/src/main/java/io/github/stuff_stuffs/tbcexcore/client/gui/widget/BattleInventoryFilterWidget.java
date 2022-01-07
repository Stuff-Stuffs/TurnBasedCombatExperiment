package io.github.stuff_stuffs.tbcexcore.client.gui.widget;

import io.github.stuff_stuffs.tbcexcore.client.util.ItemStackInfo;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantInventoryHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemCategory;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemStack;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.api.*;
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
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.IntConsumer;

//fixme
public class BattleInventoryFilterWidget extends AbstractWidget implements PositionedWidget {
    public static final Colour FIRST_BACKGROUND_COLOUR = new IntRgbColour(0x00111111);
    public static final Colour SECOND_BACKGROUND_COLOUR = new IntRgbColour(0x00222222);

    public static final List<Category> DEFAULTS = Util.make(new ArrayList<>(), list -> {
        list.add(new Category() {
            @Override
            public List<ItemStackInfo> filter(final List<ItemStackInfo> infos) {
                return infos;
            }

            @Override
            public Text getName() {
                return new LiteralText("ALL");
            }
        });
        list.add(new Category() {
            @Override
            public List<ItemStackInfo> filter(final List<ItemStackInfo> infos) {
                return infos.stream().filter(item -> item.stack.getItem().isInCategory(BattleParticipantItemCategory.CONSUMABLE_CATEGORY)).toList();
            }

            @Override
            public Text getName() {
                return new LiteralText("CONSUMABLES");
            }
        });
        list.add(new Category() {
            @Override
            public List<ItemStackInfo> filter(final List<ItemStackInfo> infos) {
                return infos.stream().filter(item -> item.stack.getItem().isInCategory(BattleParticipantItemCategory.INVALID_CATEGORY)).toList();
            }

            @Override
            public Text getName() {
                return new LiteralText("INVALID");
            }
        });
        list.add(new Category() {
            @Override
            public List<ItemStackInfo> filter(final List<ItemStackInfo> infos) {
                return infos.stream().filter(item -> {
                    for (final BattleEquipmentSlot slot : BattleEquipmentSlot.REGISTRY) {
                        if (item.stack.getItem().isInCategory(BattleParticipantItemCategory.BATTLE_EQUIPMENT_CATEGORY.apply(slot))) {
                            return true;
                        }
                    }
                    return false;
                }).toList();
            }

            @Override
            public Text getName() {
                return new LiteralText("EQUIPMENT");
            }
        });
        for (BattleEquipmentSlot slot : BattleEquipmentSlot.REGISTRY) {
            list.add(new Category() {
                @Override
                public List<ItemStackInfo> filter(final List<ItemStackInfo> infos) {
                    return infos.stream().filter(item -> item.stack.getItem().isInCategory(BattleParticipantItemCategory.BATTLE_EQUIPMENT_CATEGORY.apply(slot))).toList();
                }

                @Override
                public Text getName() {
                    return new LiteralText("EQUIPMENT(").append(slot.name()).append(")");
                }
            });
        }
    });
    private static final TextDrawer TEXT_DRAWER = TextDrawers.oneShot(TextDrawers.HorizontalJustification.CENTER, TextDrawers.VerticalJustification.CENTER, -1, 0, false);
    private static final TextDrawer TEXT_DRAWER_SHADOWED = TextDrawers.oneShot(TextDrawers.HorizontalJustification.CENTER, TextDrawers.VerticalJustification.CENTER, -1, 0, true);
    private final DoubleSupplier x;
    private final DoubleSupplier y;
    private final DoubleSupplier width;
    private final DoubleSupplier height;
    private final double borderThickness;
    private final double entryHeight;
    private final double verticalSpacing;
    private final World world;
    private final BattleParticipantHandle handle;
    private final List<Category> categories;
    private final IntConsumer onSelect;
    private double pos = 0;
    private int selectedIndex = 0;

    public BattleInventoryFilterWidget(final DoubleSupplier x, final DoubleSupplier y, final DoubleSupplier width, final DoubleSupplier height, final double borderThickness, final double entryHeight, final double verticalSpacing, final World world, final BattleParticipantHandle handle, final List<Category> categories, final IntConsumer onSelect) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.borderThickness = borderThickness;
        this.entryHeight = entryHeight;
        this.verticalSpacing = verticalSpacing;
        this.world = world;
        this.handle = handle;
        this.categories = categories;
        this.onSelect = onSelect;
    }

    public List<ItemStackInfo> getFiltered() {
        final Battle battle = ((BattleWorldSupplier) world).tbcex_getBattleWorld().getBattle(handle.battleId());
        if (battle != null) {
            final BattleParticipantStateView participant = battle.getState().getParticipant(handle);
            if (participant != null) {
                final List<ItemStackInfo> infos = new ArrayList<>();
                final Iterator<BattleParticipantInventoryHandle> iterator = participant.getInventoryIterator();
                while (iterator.hasNext()) {
                    final BattleParticipantInventoryHandle next = iterator.next();
                    infos.add(new ItemStackInfo(next, participant.getItemStack(next)));
                }
                for (final BattleEquipmentSlot slot : BattleEquipmentSlot.REGISTRY) {
                    final BattleParticipantItemStack stack = participant.getEquipmentStack(slot);
                    if (stack != null) {
                        infos.add(new ItemStackInfo(handle, slot, stack));
                    }
                }
                if (selectedIndex >= 0 && selectedIndex < categories.size()) {
                    return categories.get(selectedIndex).filter(infos);
                }
            }
        }
        return new ArrayList<>();
    }

    public void setSelectedIndex(final int selectedIndex) {
        if (0 <= selectedIndex && selectedIndex < categories.size()) {
            if (selectedIndex != this.selectedIndex) {
                //pos = (position.getY() + borderThickness + selectedIndex * entryHeight + selectedIndex * verticalSpacing + entryHeight / 2) - (height.getAsDouble() - 2 * borderThickness) / 2;
                this.selectedIndex = selectedIndex;
                onSelect.accept(selectedIndex);
            }
        } else {
            this.selectedIndex = -1;
            onSelect.accept(selectedIndex);
        }
    }

    private boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (new Rect2d(0, 0, width.getAsDouble(), height.getAsDouble()).isIn(mouseX, mouseY)) {
            final int index = findHoverIndex(mouseX, mouseY + pos);
            setSelectedIndex(index);
            return true;
        }
        return false;
    }

    private boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        final double height = this.height.getAsDouble();
        if (new Rect2d(0, 0, width.getAsDouble(), height).isIn(mouseX, mouseY)) {
            pos = Math.min(Math.max(pos - deltaY, -(height - 2 * borderThickness) / 2), getListHeight() - (height - 2 * borderThickness) / 2);
            return true;
        }
        return false;
    }

    private boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        final double height = this.height.getAsDouble();
        if (new Rect2d(0, 0, width.getAsDouble(), height).isIn(mouseX, mouseY)) {
            pos = Math.min(Math.max(pos - amount, -(height - 2 * borderThickness) / 2), getListHeight() - (height - 2 * borderThickness) / 2);
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
            } else if (event instanceof GuiInputContext.MouseScroll scroll) {
                final Vec2d mouse = context.transformMouseCursor(new Vec2d(scroll.mouseX, scroll.mouseY));
                final Vec2d delta = context.transformMouseCursor(new Vec2d(scroll.mouseX, scroll.mouseY + scroll.amount)).subtract(mouse);
                return mouseScrolled(mouse.x, mouse.y, delta.y);
            } else if (event instanceof GuiInputContext.MouseDrag drag) {
                final Vec2d mouse = context.transformMouseCursor(new Vec2d(drag.mouseX, drag.mouseY));
                final Vec2d delta = context.transformMouseCursor(new Vec2d(drag.mouseX + drag.deltaX, drag.mouseY + drag.deltaY)).subtract(mouse);
                return mouseDragged(mouse.x, mouse.y, drag.button, delta.x, delta.y);
            } else if (event instanceof GuiInputContext.KeyPress keyPress) {
                return keyPress(keyPress.keyCode);
            }
            return false;
        });

        final double offsetX = 0;
        final double offsetY = 0;
        final double width = this.width.getAsDouble();
        final double height = this.height.getAsDouble();
        final GuiQuadEmitter emitter = context.getEmitter();
        emitter.renderMaterial(GuiRenderMaterial.POS_COLOUR_TRANSLUCENT);
        emitter.pos(0, (float) (offsetX + width), (float) offsetY);
        emitter.pos(1, (float) offsetX, (float) offsetY);
        emitter.pos(2, (float) offsetX, (float) (offsetY + height));
        emitter.pos(3, (float) (offsetX + width), (float) (offsetY + height));
        final int colour = IntRgbColour.BLACK.pack(127);
        emitter.colour(colour, colour, colour, colour);
        emitter.emit();

        context.pushTranslate(0, -pos, 0);
        final Vec2d mouse = context.transformMouseCursor();
        final int hoverIndex = findHoverIndex(mouse.x, mouse.y);
        for (int i = 0; i < categories.size(); i++) {
            renderInfo(categories.get(i), context, i, hoverIndex);
        }

        for (int i = 0; i < categories.size(); i++) {
            renderDecorations(categories.get(i), context, i, hoverIndex);
        }
        context.popGuiTransform();
    }

    private void renderDecorations(final Category category, final GuiContext context, final int index, final int hoverIndex) {
        final float offsetX = 0;
        final float offsetY = 0;
        final double maxWidth = width.getAsDouble() - 2 * borderThickness;
        final double y = offsetY + borderThickness + index * entryHeight + index * verticalSpacing;
        final double centerY = y + entryHeight / 2.0;
        double dist = Math.abs(centerY - (pos + (height.getAsDouble() - 2 * borderThickness) / 2));
        dist *= dist * dist;
        final double offset = height.getAsDouble() / 4;
        final double scale = Math.max(offset - dist, 0) / offset;
        final boolean shadow = index == hoverIndex || selectedIndex == index;
        context.pushTranslate(offsetX + borderThickness + (maxWidth * scale) / 2.0, y + (entryHeight * scale) / 2.0, 0);
        context.pushGuiTransform(new GuiTransform() {
            @Override
            public boolean transform(final MutableGuiQuad quad) {
                for (int i = 0; i < 4; i++) {
                    int colour = quad.colour(i);
                    final int alpha = (colour >> 24) & 255;
                    final int newAlpha = (int) Math.round(alpha * scale);
                    colour &= 0xFF_FF_FF;
                    colour |= newAlpha << 24;
                    quad.colour(i, colour);
                }
                return true;
            }

            @Override
            public Vec2d transformMouseCursorToGui(final Vec2d cursor) {
                return cursor;
            }

            @Override
            public Vec2d transformMouseCursorToScreen(final Vec2d cursor) {
                return cursor;
            }
        });
        (shadow ? TEXT_DRAWER_SHADOWED : TEXT_DRAWER).draw(maxWidth * scale, entryHeight * scale, category.getName().asOrderedText(), context);
        context.popGuiTransform();
        context.popGuiTransform();
    }

    private void renderInfo(final Category category, final GuiContext context, final int index, final int hoverIndex) {
        final double offsetX = 0;
        final double offsetY = 0;
        final float startX = (float) (offsetX + borderThickness);
        final float endX = (float) (offsetX + width.getAsDouble() - borderThickness);
        final float xLen = (endX - startX);
        final float startY = (float) (offsetY + borderThickness + index * entryHeight + index * verticalSpacing);
        final float endY = (float) (offsetY + borderThickness + index * entryHeight + index * verticalSpacing + entryHeight);
        final float centerY = (startY + endY) / 2f;
        final float yLen = (endY - startY);
        float dist = Math.abs(centerY - (float) (pos + (height.getAsDouble() - 2 * borderThickness) / 2));
        dist *= dist * dist;
        final float offset = ((float) height.getAsDouble()) / 4f;
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
        emitter.pos(0, startX + xLen * scale, startY);
        emitter.pos(1, startX, startY);
        emitter.pos(2, startX, startY + yLen * scale);
        emitter.pos(3, startX + xLen * scale, startY + yLen * scale);
        final int c = backgroundColour.pack(alpha);
        emitter.colour(c, c, c, c);
        emitter.renderMaterial(GuiRenderMaterial.POS_COLOUR_TRANSLUCENT);
        emitter.emit();
    }

    private double getListHeight() {
        final int size = categories.size();
        return size * entryHeight + (size > 0 ? size - 1 : 0) * verticalSpacing;
    }

    private boolean keyPress(final int keyCode) {
        if (keyCode == GLFW.GLFW_KEY_DOWN) {
            setSelectedIndex(selectedIndex + 1);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_UP) {
            setSelectedIndex(selectedIndex - 1);
            return true;
        }
        return false;
    }

    private int findHoverIndex(final double mouseX, final double mouseY) {
        final double offsetX = 0;
        final double offsetY = 0;
        final double width = this.width.getAsDouble();
        for (int index = 0; index < categories.size(); index++) {
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
        return (index & 1) == 0 ? FIRST_BACKGROUND_COLOUR : SECOND_BACKGROUND_COLOUR;
    }

    public interface Category {
        List<ItemStackInfo> filter(List<ItemStackInfo> infos);

        Text getName();
    }
}
