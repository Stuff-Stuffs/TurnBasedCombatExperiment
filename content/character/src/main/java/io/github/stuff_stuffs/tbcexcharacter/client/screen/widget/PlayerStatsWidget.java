package io.github.stuff_stuffs.tbcexcharacter.client.screen.widget;

import io.github.stuff_stuffs.tbcexcharacter.common.entity.CharacterInfo;
import io.github.stuff_stuffs.tbcexcharacter.mixin.api.PlayerCharacterInfoSupplier;
import io.github.stuff_stuffs.tbcexcore.client.gui.widget.BattleInventoryFilterWidget;
import io.github.stuff_stuffs.tbcexcore.client.gui.widget.info.AbstractParticipantStatListWidget;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiInputContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiQuadEmitter;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiRenderMaterial;
import io.github.stuff_stuffs.tbcexgui.client.api.text.TextDrawer;
import io.github.stuff_stuffs.tbcexgui.client.api.text.TextDrawers;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexutil.common.Rect2d;
import io.github.stuff_stuffs.tbcexutil.common.Vec2d;
import io.github.stuff_stuffs.tbcexutil.common.colour.Colour;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.mutable.MutableInt;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class PlayerStatsWidget extends AbstractWidget {
    private final double width;
    private final double height;
    private final List<Entry> entries;
    private double scrollPos;

    public PlayerStatsWidget(final double width, final double height, final double entryHeight, final PlayerEntity entity) {
        this.width = width;
        this.height = height;
        final List<BattleParticipantStat> battleParticipantStats = BattleParticipantStat.REGISTRY.stream().toList();
        entries = new ArrayList<>(battleParticipantStats.size());
        final CharacterInfo characterInfo = ((PlayerCharacterInfoSupplier) entity).tbcex_getCharacterInfo();
        for (final BattleParticipantStat battleParticipantStat : battleParticipantStats) {
            entries.add(new Entry(battleParticipantStat, characterInfo, entryHeight));
        }
        scroll(0);
    }

    private boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        final double transformedMouseX = mouseX / width;
        double offset = -scrollPos;
        for (final Entry entry : entries) {
            if (entry.mouseClicked(transformedMouseX, mouseY - offset, button)) {
                return true;
            }
            offset += entry.getHeight();
        }
        return false;
    }

    private boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        final double transformedMouseX = mouseX / width;
        double offset = -scrollPos;
        for (final Entry entry : entries) {
            if (entry.mouseReleased(transformedMouseX, mouseY - offset, button)) {
                return true;
            }
            offset += entry.getHeight();
        }
        return false;
    }

    private void scroll(final double amount) {
        scrollPos -= amount;
        if (scrollPos < 0) {
            scrollPos = 0;
        }
        final double max = entries.stream().mapToDouble(Entry::getHeight).sum() - height;
        if (max < 0) {
            scrollPos = 0;
        }
        if (scrollPos > max) {
            scrollPos = max;
        }
    }

    private boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        final double x = 0;
        final double y = 0;
        if (new Rect2d(x, y, x + width, y + width).isIn(mouseX, mouseY)) {
            scroll(-deltaY);
            return true;
        }
        return false;
    }

    private boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        final double x = 0;
        final double y = 0;
        if (new Rect2d(x, y, x + width, y + width).isIn(mouseX, mouseY)) {
            scroll(-amount);
            return true;
        }
        return false;
    }

    @Override
    public void render(final GuiContext context) {
        context.enterSection(getDebugName());
        processEvents(context, event -> {
            if (event instanceof GuiInputContext.MouseClick click) {
                final Vec2d mouse = context.transformMouseCursor(new Vec2d(click.mouseX, click.mouseY));
                return mouseClicked(mouse.x, mouse.y, click.button);
            } else if (event instanceof GuiInputContext.MouseReleased released) {
                final Vec2d mouse = context.transformMouseCursor(new Vec2d(released.mouseX, released.mouseY));
                return mouseReleased(mouse.x, mouse.y, released.button);
            } else if (event instanceof GuiInputContext.MouseDrag drag) {
                final Vec2d mouse = context.transformMouseCursor(new Vec2d(drag.mouseX, drag.mouseY));
                final Vec2d delta = context.transformMouseCursor(new Vec2d(drag.mouseX + drag.deltaX, drag.mouseY + drag.deltaY)).subtract(mouse);
                return mouseDragged(mouse.x, mouse.y, drag.button, delta.x, delta.y);
            } else if (event instanceof GuiInputContext.MouseScroll scroll) {
                final Vec2d mouse = context.transformMouseCursor(new Vec2d(scroll.mouseX, scroll.mouseY));
                final Vec2d delta = context.transformMouseCursor(new Vec2d(scroll.mouseX, scroll.mouseY + scroll.amount)).subtract(mouse);
                return mouseScrolled(mouse.x, mouse.y, delta.y);
            }
            return false;
        });
        final float startX = 0;
        final float startY = 0;
        final float endX = startX + (float) width;
        final float endY = startY + (float) height;
        context.pushScissor(startX, startY, endX, endY);
        double offset = -scrollPos;
        boolean colourSelector = false;
        for (final Entry entry : entries) {
            entry.render(context, offset, colourSelector ? BattleInventoryFilterWidget.FIRST_BACKGROUND_COLOUR : BattleInventoryFilterWidget.SECOND_BACKGROUND_COLOUR, !colourSelector ? BattleInventoryFilterWidget.FIRST_BACKGROUND_COLOUR : BattleInventoryFilterWidget.SECOND_BACKGROUND_COLOUR);
            offset += entry.getHeight();
            if (entry.shouldFlipColour()) {
                colourSelector = !colourSelector;
            }
        }
        context.popGuiTransform();
        context.exitSection();
    }

    @Override
    public String getDebugName() {
        return "PlayerStatsWidget";
    }


    private final class Entry {
        private static final TextDrawer NAME_DRAWER = TextDrawers.oneShot(TextDrawers.HorizontalJustification.CENTER, TextDrawers.VerticalJustification.CENTER, AbstractParticipantStatListWidget.NEUTRAL_COLOUR.pack(255), 0, false);
        private static final TextDrawer STAT_VALUE_DRAWER = TextDrawers.oneShot(TextDrawers.HorizontalJustification.CENTER, TextDrawers.VerticalJustification.CENTER, Colour.WHITE.pack(255), 0, false);
        private final BattleParticipantStat stat;
        private final CharacterInfo characterInfo;
        private final double height;
        private final Rect2d area;
        private boolean expanded;

        private Entry(final BattleParticipantStat stat, final CharacterInfo characterInfo, final double height) {
            this.stat = stat;
            this.characterInfo = characterInfo;
            this.height = height;
            area = new Rect2d(0, 0, 1, height);
        }

        public void render(final GuiContext context, final double offset, final Colour colour, final Colour secondaryColour) {
            final GuiQuadEmitter emitter = context.getEmitter();
            final double x = 0;
            final double y = 0 + offset;
            final int c = colour.pack(255);
            emitter.rectangle(x, y, width, height, c, c, c, c);
            emitter.renderMaterial(GuiRenderMaterial.POS_COLOUR);
            emitter.emit();
            final double textStart = (width / 2) * 0.025 + x;
            final double textWidth = (width / 2) * 0.95;
            final double textHeight = height * 0.95;
            final Text name = stat.getName();
            context.pushTranslate(textStart + textWidth / 2.0, y + height / 2.0, 1);
            NAME_DRAWER.draw(textWidth, height, name.asOrderedText(), context);
            context.popGuiTransform();
            final double numberStart = (width / 2) * 1.025 + x;
            context.pushTranslate(numberStart + textWidth / 2.0, y + textHeight / 2.0, 1);
            STAT_VALUE_DRAWER.draw(textWidth, textHeight, AbstractParticipantStatListWidget.format(characterInfo.getStat(stat)).asOrderedText(), context);
            context.popGuiTransform();
            if (expanded) {
                final double valTextStart = (width / 2) * 0.05 + x;
                final double valNumberStart = (width / 2) * 1.05 + x;
                final double valTextWidth = (width / 2) * 0.90;
                final MutableInt counter = new MutableInt(1);
                characterInfo.forEachSourcedStat(stat, (statSource, isLast) -> {
                    final double textY = y + counter.intValue() * height;
                    final Colour valColour;
                    if (((counter.getAndIncrement()) & 1) == 1) {
                        valColour = secondaryColour;
                    } else {
                        valColour = colour;
                    }
                    final int c0 = valColour.pack(255);
                    emitter.rectangle(x, textY, width, height, c0, c0, c0, c0);
                    emitter.emit();
                    final Text text = new LiteralText(isLast ? "└" : "├").append(statSource.getText());
                    final Text number = AbstractParticipantStatListWidget.format(statSource.getAmount());
                    context.pushTranslate(valTextStart + valTextWidth / 2.0, textY + textHeight / 2.0, 0.001);
                    NAME_DRAWER.draw(valTextWidth, textHeight, text.asOrderedText(), context);
                    context.popGuiTransform();
                    context.pushTranslate(valNumberStart + valTextWidth / 2.0, textY + textHeight / 2.0, 0.001);
                    STAT_VALUE_DRAWER.draw(valTextWidth, textHeight, number.asOrderedText(), context);
                    context.popGuiTransform();
                });
            }
        }

        public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                if (expanded && new Rect2d(0, 0, 1, getHeight()).isIn(mouseX, mouseY)) {
                    expanded = false;
                    scroll(0);
                    return true;
                } else if (area.isIn(mouseX, mouseY)) {
                    expanded = true;
                    scroll(0);
                    return true;
                }
            }
            return false;
        }

        public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
            return (expanded && new Rect2d(0, 0, 1, getHeight()).isIn(mouseX, mouseY)) || (!expanded && area.isIn(mouseX, mouseY));
        }

        public double getHeight() {
            if (expanded) {
                final MutableInt counter = new MutableInt(0);
                characterInfo.forEachSourcedStat(stat, (statSource, isLast) -> counter.increment());
                return counter.intValue() * height + height;
            } else {
                return height;
            }
        }

        public boolean shouldFlipColour() {
            if (expanded) {
                final MutableInt counter = new MutableInt(0);
                characterInfo.forEachSourcedStat(stat, (statSource, isLast) -> counter.increment());
                return (counter.intValue() & 1) == 0;
            }
            return true;
        }
    }
}
