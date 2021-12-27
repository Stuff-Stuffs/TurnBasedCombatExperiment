package io.github.stuff_stuffs.tbcexcore.client.gui.widget.info;

import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.ParticipantComponents;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.ParticipantInfoComponentView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.render.GuiRenderLayers;
import io.github.stuff_stuffs.tbcexgui.client.render.ScissorStack;
import io.github.stuff_stuffs.tbcexutil.client.RenderUtil;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.world.World;

import java.util.Random;

import static io.github.stuff_stuffs.tbcexcore.client.gui.widget.BattleInventoryFilterWidget.FIRST_BACKGROUND_COLOUR;
import static io.github.stuff_stuffs.tbcexcore.client.gui.widget.BattleInventoryFilterWidget.SECOND_BACKGROUND_COLOUR;

public class ParticipantOtherStatListWidget extends AbstractParticipantStatListWidget {

    public ParticipantOtherStatListWidget(final WidgetPosition position, final double width, final double height, final double entryHeight, final BattleParticipantHandle handle, final BattleParticipantHandle target, final World world) {
        super(position, width, height, entryHeight, handle, target, world);
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        final Battle battle = ((BattleWorldSupplier) world).tbcex_getBattleWorld().getBattle(handle.battleId());
        if (battle == null) {
            return;
        }
        final BattleParticipantStateView curParticipant = battle.getState().getParticipant(handle);
        final BattleParticipantStateView targetParticipant = battle.getState().getParticipant(target);
        if (curParticipant == null || targetParticipant == null) {
            return;
        }
        final ParticipantInfoComponentView curComponent = curParticipant.getComponent(ParticipantComponents.INFO_COMPONENT_TYPE.key);
        final ParticipantInfoComponentView targetComponent = targetParticipant.getComponent(ParticipantComponents.INFO_COMPONENT_TYPE.key);
        if (curComponent == null || targetComponent == null) {
            return;
        }
        final double perception = curComponent.getStat(BattleParticipantStat.PERCEPTION_STAT);
        final double targetPerception = targetComponent.getLevel();
        final double effect = calcEffect(targetPerception - perception);
        final Random rangeRandom = new Random(target.participantId().getLeastSignificantBits() ^ target.participantId().getMostSignificantBits());

        final double x = position.getX();
        final double y = position.getY();
        render(vertexConsumers -> {
            final VertexConsumer vertexConsumer = vertexConsumers.getBuffer(GuiRenderLayers.POSITION_COLOUR_LAYER);
            matrices.translate(0, 0, 2);
            renderFitText(matrices, new LiteralText("Stat"), x, y, width * 0.35, entryHeight, false, NEUTRAL_COLOUR, 255, vertexConsumers);
            renderFitText(matrices, new LiteralText("Possible Range(lo-hi)"), x + width * 0.4, y, width * 0.6, entryHeight, false, NEUTRAL_COLOUR, 255, vertexConsumers);
            matrices.translate(0, 0, -1);
            RenderUtil.renderRectangle(matrices, x, y, width, entryHeight, FIRST_BACKGROUND_COLOUR, 255, vertexConsumer);
            matrices.translate(0, 0, -1);
        });

        ScissorStack.push(matrices, x, y + entryHeight, x + width, y + height);
        render(vertexConsumers -> {
            final VertexConsumer buffer = vertexConsumers.getBuffer(GuiRenderLayers.POSITION_COLOUR_LAYER);
            double h = -getScrollPos() + entryHeight;
            boolean odd = false;
            for (final BattleParticipantStat stat : BattleParticipantStat.REGISTRY) {
                matrices.push();
                matrices.translate(0, h, 10);
                RenderUtil.renderRectangle(matrices, x, y, width, entryHeight, odd ? FIRST_BACKGROUND_COLOUR : SECOND_BACKGROUND_COLOUR, 255, buffer);
                renderStatEntry(targetComponent.getStat(stat), effect, rangeRandom, stat, matrices, vertexConsumers);
                h += entryHeight;
                matrices.pop();
                odd = !odd;
            }
        });
        ScissorStack.pop();
    }

    private void renderStatEntry(final double val, final double rangeSize, final Random rangeFinder, final BattleParticipantStat stat, final MatrixStack matrices, final VertexConsumerProvider vertexConsumers) {
        final double roundedRangeSize;
        if (stat == BattleParticipantStat.MAX_HEALTH_STAT) {
            roundedRangeSize = 0;
        } else {
            roundedRangeSize = floorToNearestHundredth(rangeSize);
        }
        final double x = position.getX();
        final double y = position.getY();
        final double rangeOffset = roundedRangeSize * rangeFinder.nextDouble();
        final double top = floorToNearestHundredth(val + rangeOffset);
        final double bottom = top - roundedRangeSize;
        final boolean same = floorToNearestHundredth(Math.abs(top - bottom)) == 0;
        MutableText text = new LiteralText("");
        if (bottom < 0) {
            text = text.append("(");
        }
        text = text.append(format(bottom));
        if (bottom < 0) {
            text.append(")");
        }
        if (!same) {
            text = text.append(new LiteralText(" - "));
            if (top < 0) {
                text.append(new LiteralText("("));
            }
            text = text.append(format(top));
            if (top < 0) {
                text.append(new LiteralText(")"));
            }
        }

        renderFitText(matrices, stat.getName(), x, y, width * 0.35, entryHeight, true, NEUTRAL_COLOUR, 255, vertexConsumers);
        renderFitText(matrices, text, x + width * 0.4, y, width * 0.6, entryHeight, true, NEUTRAL_COLOUR, 255, vertexConsumers);
    }

    private static double calcEffect(double perceptionDelta) {
        perceptionDelta += 5;
        if (perceptionDelta <= 0) {
            return 0;
        } else if (perceptionDelta <= 10) {
            return 2 * Math.log(perceptionDelta);
        } else {
            return 0.5 * perceptionDelta - 0.4;
        }
    }
}
