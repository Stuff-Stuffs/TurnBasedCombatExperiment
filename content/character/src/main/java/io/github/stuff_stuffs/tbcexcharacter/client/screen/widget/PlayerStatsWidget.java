package io.github.stuff_stuffs.tbcexcharacter.client.screen.widget;

import io.github.stuff_stuffs.tbcexcharacter.common.entity.CharacterInfo;
import io.github.stuff_stuffs.tbcexcharacter.mixin.api.PlayerStatContainerSupplier;
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
import net.minecraft.text.Text;
import org.apache.commons.lang3.mutable.MutableInt;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class PlayerStatsWidget extends AbstractWidget {
    private final WidgetPosition position;
    private final double width;
    private final double height;
    private final double entryHeight;
    private final List<Entry> entries;
    private double scrollPos;

    public PlayerStatsWidget(final WidgetPosition position, final double width, final double height, final double entryHeight, final PlayerEntity entity) {
        this.position = position;
        this.width = width;
        this.height = height;
        this.entryHeight = entryHeight;
        final List<BattleParticipantStat> battleParticipantStats = BattleParticipantStat.REGISTRY.stream().toList();
        entries = new ArrayList<>(battleParticipantStats.size());
        final CharacterInfo characterInfo = ((PlayerStatContainerSupplier) entity).tbcex_getCharacterInfo();
        for (int i = 0; i < battleParticipantStats.size(); i++) {
            entries.add(new Entry(battleParticipantStats.get(i), characterInfo, entryHeight, i));
        }
        scrollPos = 0;
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return position;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        final double transformedMouseX = (mouseX - position.getX()) / width - scrollPos;
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).mouseClicked(transformedMouseX, mouseY - i * entryHeight, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        final double transformedMouseX = (mouseX - position.getX()) / width - scrollPos;
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).mouseReleased(transformedMouseX, mouseY - i * entryHeight, button)) {
                return true;
            }
        }
        return false;
    }

    private void scroll(double amount) {
        scrollPos -= amount;
        if(scrollPos<0) {
            scrollPos = 0;
        } else {
            double max = entries.stream().mapToDouble(Entry::getHeight).sum() - height;
            if(scrollPos>max) {
                scrollPos = max;
            }
        }
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        scroll(-deltaY);
        return true;
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        scroll(amount);
        return true;
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
            for (final Entry entry : entries) {
                entry.render(matrices, mouseX, mouseY, offset, vertexConsumers);
                offset += entry.getHeight();
            }
            matrices.pop();
        });
        ScissorStack.pop();
    }


    private final class Entry {
        private final BattleParticipantStat stat;
        private final CharacterInfo characterInfo;
        private final double height;
        private final int index;
        private final Rect2d area;
        private boolean expanded;

        private Entry(final BattleParticipantStat stat, final CharacterInfo characterInfo, final double height, final int index) {
            this.stat = stat;
            this.characterInfo = characterInfo;
            this.height = height;
            this.index = index;
            area = new Rect2d(0, index * height, 1, index * height + height);
        }

        public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final double offset, final VertexConsumerProvider vertexConsumers) {
            final double x = position.getX();
            final double y = position.getY() + offset;
            final Colour colour;
            if ((index & 1) == 1) {
                colour = BattleInventoryFilterWidget.SECOND_BACKGROUND_COLOUR;
            } else {
                colour = BattleInventoryFilterWidget.FIRST_BACKGROUND_COLOUR;
            }
            RenderUtil.renderRectangle(matrices, x, y, width, height, colour, 255, vertexConsumers.getBuffer(GuiRenderLayers.POSITION_COLOUR_LAYER));
            final double textStart = (width / 2) * 0.025 + x;
            final double textWidth = (width / 2) * 0.95;
            final double textHeight = height * 0.95;
            final Text name = stat.getName();
            matrices.push();
            matrices.translate(0,0,1);
            renderFitText(matrices, name, textStart, y, textWidth, height, false, AbstractParticipantStatListWidget.NEUTRAL_COLOUR, 255, vertexConsumers);
            final double numberStart = (width / 2) * 1.025 + x;
            renderFitText(matrices, AbstractParticipantStatListWidget.format(characterInfo.getStat(stat)), numberStart, y, textWidth, textHeight, false, Colour.WHITE, 255, vertexConsumers);
            if (expanded) {
                final double valTextStart = (width / 2) * 0.05 + x;
                final double valNumberStart = (width / 2) * 1.05;
                final double valTextWidth = (width / 2) * 0.90;
                final MutableInt counter = new MutableInt(0);
                characterInfo.forEachSourcedStat(stat, sourcedStat -> {
                    final double textY = y + counter.intValue() * height;
                    final Colour valColour;
                    if (((index + counter.getAndIncrement()) & 1) == 1) {
                        valColour = BattleInventoryFilterWidget.SECOND_BACKGROUND_COLOUR;
                    } else {
                        valColour = BattleInventoryFilterWidget.FIRST_BACKGROUND_COLOUR;
                    }
                    RenderUtil.renderRectangle(matrices, x, textY, width, height, valColour, 255, vertexConsumers.getBuffer(GuiRenderLayers.POSITION_COLOUR_LAYER));
                    final Text text = sourcedStat.getText();
                    final Text number = AbstractParticipantStatListWidget.format(sourcedStat.getAmount());
                    renderFitText(matrices, text, valTextStart, textY, valTextWidth, textHeight, false, AbstractParticipantStatListWidget.NEUTRAL_COLOUR, 255, vertexConsumers);
                    renderFitText(matrices, number, valNumberStart, textY, valTextWidth, textHeight, false, Colour.WHITE, 255, vertexConsumers);
                });
            }
            matrices.pop();
        }

        public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                if (expanded && new Rect2d(0, index * height, 1, index * height + getHeight()).isIn(mouseX, mouseY)) {
                    expanded = false;
                    return true;
                } else if (area.isIn(mouseX, mouseY)) {
                    expanded = true;
                    return true;
                }
            }
            return false;
        }

        public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
            return (expanded && new Rect2d(0, index * height, 1, index * height + getHeight()).isIn(mouseX, mouseY)) || (!expanded && area.isIn(mouseX, mouseY));
        }

        public double getHeight() {
            if (expanded) {
                final MutableInt counter = new MutableInt(0);
                characterInfo.forEachSourcedStat(stat, sourcedStat -> counter.increment());
                return counter.intValue() * height + height;
            } else {
                return height;
            }
        }
    }

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        return true;
    }
}
