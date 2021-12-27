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
import net.minecraft.text.Text;
import net.minecraft.world.World;

import static io.github.stuff_stuffs.tbcexcore.client.gui.widget.BattleInventoryFilterWidget.FIRST_BACKGROUND_COLOUR;
import static io.github.stuff_stuffs.tbcexcore.client.gui.widget.BattleInventoryFilterWidget.SECOND_BACKGROUND_COLOUR;

//TODO Stat icons
//TODO Scrollbar
public class ParticipantSelfStatListWidget extends AbstractParticipantStatListWidget {

    public ParticipantSelfStatListWidget(final WidgetPosition position, final double width, final double height, final double entryHeight, final BattleParticipantHandle handle, final BattleParticipantHandle target, final World world) {
        super(position, width, height, entryHeight, handle, target, world);
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        final Battle battle = ((BattleWorldSupplier) world).tbcex_getBattleWorld().getBattle(handle.battleId());
        if (battle == null) {
            return;
        }
        final BattleParticipantStateView participantState = battle.getState().getParticipant(handle);
        if (participantState == null) {
            return;
        }
        final ParticipantInfoComponentView component = participantState.getComponent(ParticipantComponents.INFO_COMPONENT_TYPE.key);
        if (component == null) {
            return;
        }
        final double x = position.getX();
        final double y = position.getY();
        render(vertexConsumers -> {
            final VertexConsumer vertexConsumer = vertexConsumers.getBuffer(GuiRenderLayers.POSITION_COLOUR_LAYER);
            matrices.translate(0, 0, 2);
            renderFitText(matrices, new LiteralText("Stat"), x, y, width * 0.45, entryHeight, false, NEUTRAL_COLOUR, 255, vertexConsumers);
            renderFitText(matrices, new LiteralText("Value(Base+Bonus)"), x + width * 0.5, y, width * 0.5, entryHeight, false, NEUTRAL_COLOUR, 255, vertexConsumers);
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
                renderStatEntry(component.getRawStat(stat), component.getStat(stat), stat, matrices, vertexConsumers);
                h += entryHeight;
                matrices.pop();
                odd = !odd;
            }
        });
        ScissorStack.pop();
    }

    private void renderStatEntry(double val, double raw, final BattleParticipantStat stat, final MatrixStack matrices, final VertexConsumerProvider vertexConsumers) {
        val = floorToNearestHundredth(val);
        raw = floorToNearestHundredth(raw);
        final double x = position.getX();
        final double y = position.getY();
        final double diff = val - raw;
        final Text valText = format(val);
        final Text rawText = format(raw);
        final Text diffText = format(diff);
        MutableText text = new LiteralText("");
        text = text.append(valText);
        text = text.append("(");
        text = text.append(rawText);
        if (diff >= 0) {
            text = text.append("+");
        }
        text = text.append(diffText);
        text = text.append(")");
        renderFitText(matrices, stat.getName(), x, y, width * 0.45, entryHeight, true, NEUTRAL_COLOUR, 255, vertexConsumers);
        renderFitText(matrices, text, x + width * 0.5, y, width * 0.5, entryHeight, true, NEUTRAL_COLOUR, 255, vertexConsumers);
    }
}
