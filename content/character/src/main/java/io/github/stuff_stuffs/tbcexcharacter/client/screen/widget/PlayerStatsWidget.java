package io.github.stuff_stuffs.tbcexcharacter.client.screen.widget;

import io.github.stuff_stuffs.tbcexcharacter.common.entity.CharacterInfo;
import io.github.stuff_stuffs.tbcexcharacter.mixin.api.PlayerCharacterInfoSupplier;
import io.github.stuff_stuffs.tbcexcore.client.gui.widget.BattleInventoryFilterWidget;
import io.github.stuff_stuffs.tbcexcore.client.gui.widget.info.AbstractParticipantStatListWidget;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;
import io.github.stuff_stuffs.tbcexgui.client.render.GuiRenderLayers;
import io.github.stuff_stuffs.tbcexgui.client.render.ScissorStack;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import io.github.stuff_stuffs.tbcexutil.client.RenderUtil;
import io.github.stuff_stuffs.tbcexutil.common.Rect2d;
import io.github.stuff_stuffs.tbcexutil.common.colour.Colour;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.mutable.MutableInt;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class PlayerStatsWidget extends AbstractWidget {
    private final WidgetPosition position;
    private final double width;
    private final double height;
    private final List<Entry> entries;
    private double scrollPos;

    public PlayerStatsWidget(final WidgetPosition position, final double width, final double height, final double entryHeight, final PlayerEntity entity) {
        this.position = position;
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

    @Override
    public WidgetPosition getWidgetPosition() {
        return position;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        final double transformedMouseX = (mouseX - position.getX()) / width;
        double offset = -scrollPos + position.getY();
        for (final Entry entry : entries) {
            if (entry.mouseClicked(transformedMouseX, mouseY - offset, button)) {
                return true;
            }
            offset += entry.getHeight();
        }
        return false;
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        final double transformedMouseX = (mouseX - position.getX()) / width;
        double offset = -scrollPos + position.getY();
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

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        double x = position.getX();
        double y = position.getY();
        if(new Rect2d(x,y,x+width,y+width).isIn(mouseX,mouseY)) {
            scroll(-deltaY);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        double x = position.getX();
        double y = position.getY();
        if(new Rect2d(x,y,x+width,y+width).isIn(mouseX,mouseY)) {
            scroll(-amount);
            return true;
        }
        return false;
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        final double startX = position.getX();
        final double startY = position.getY();
        final double endX = startX + width;
        final double endY = startY + height;
        ScissorStack.push(matrices.peek().getPositionMatrix(), startX, startY, endX, endY);
        render(vertexConsumers -> {
            matrices.push();
            double offset = -scrollPos;
            boolean colourSelector = false;
            for (final Entry entry : entries) {
                entry.render(matrices, mouseX, mouseY, offset, colourSelector ? BattleInventoryFilterWidget.FIRST_BACKGROUND_COLOUR : BattleInventoryFilterWidget.SECOND_BACKGROUND_COLOUR, !colourSelector ? BattleInventoryFilterWidget.FIRST_BACKGROUND_COLOUR : BattleInventoryFilterWidget.SECOND_BACKGROUND_COLOUR, vertexConsumers);
                offset += entry.getHeight();
                if (entry.shouldFlipColour()) {
                    colourSelector = !colourSelector;
                }
            }
            matrices.pop();
        });
        ScissorStack.pop();
    }


    private final class Entry {
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

        public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final double offset, final Colour colour, final Colour secondaryColour, final VertexConsumerProvider vertexConsumers) {
            final double x = position.getX();
            final double y = position.getY() + offset;
            RenderUtil.renderRectangle(matrices, x, y, width, height, colour, 255, vertexConsumers.getBuffer(GuiRenderLayers.POSITION_COLOUR_LAYER));
            final double textStart = (width / 2) * 0.025 + x;
            final double textWidth = (width / 2) * 0.95;
            final double textHeight = height * 0.95;
            final Text name = stat.getName();
            matrices.push();
            matrices.translate(0, 0, 0.001);
            renderFitText(matrices, name, textStart, y, textWidth, height, false, AbstractParticipantStatListWidget.NEUTRAL_COLOUR, 255, vertexConsumers);
            final double numberStart = (width / 2) * 1.025 + x;
            renderFitText(matrices, AbstractParticipantStatListWidget.format(characterInfo.getStat(stat)), numberStart, y, textWidth, textHeight, false, Colour.WHITE, 255, vertexConsumers);
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
                    RenderUtil.renderRectangle(matrices, x, textY, width, height, valColour, 255, vertexConsumers.getBuffer(GuiRenderLayers.POSITION_COLOUR_LAYER));
                    final Text text = new LiteralText(isLast ? "└" : "├").append(statSource.getText());
                    final Text number = AbstractParticipantStatListWidget.format(statSource.getAmount());
                    matrices.translate(0, 0, 0.001);
                    renderFitText(matrices, text, valTextStart, textY, valTextWidth, textHeight, false, AbstractParticipantStatListWidget.NEUTRAL_COLOUR, 255, vertexConsumers);
                    renderFitText(matrices, number, valNumberStart, textY, valTextWidth, textHeight, false, Colour.WHITE, 255, vertexConsumers);
                });
            }
            matrices.pop();
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

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        return true;
    }
}
