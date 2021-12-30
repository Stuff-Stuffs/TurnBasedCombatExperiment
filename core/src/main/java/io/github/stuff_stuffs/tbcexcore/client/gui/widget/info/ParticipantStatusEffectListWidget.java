package io.github.stuff_stuffs.tbcexcore.client.gui.widget.info;

import com.google.common.collect.Iterators;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.ParticipantComponents;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.status.ParticipantStatusEffect;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.status.ParticipantStatusEffectComponentView;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.world.World;

//TODO test :(
public class ParticipantStatusEffectListWidget extends AbstractWidget {
    private final double width;
    private final double height;
    private final double entryHeight;
    private final BattleParticipantHandle handle;
    private final World world;
    private double scrollPos;

    public ParticipantStatusEffectListWidget(final double width, final double height, final double entryHeight, final BattleParticipantHandle handle, final World world) {
        this.width = width;
        this.height = height;
        this.entryHeight = entryHeight;
        this.handle = handle;
        this.world = world;
    }

    private double getScrollBarMax() {
        final Battle battle = ((BattleWorldSupplier) world).tbcex_getBattleWorld().getBattle(handle.battleId());
        if (battle == null) {
            return 0;
        }
        final BattleParticipantStateView participant = battle.getState().getParticipant(handle);
        if (participant == null) {
            return 0;
        }
        final ParticipantStatusEffectComponentView component = participant.getComponent(ParticipantComponents.STATUS_EFFECT_COMPONENT_TYPE.key);
        if (component == null) {
            return 0;
        }
        return Math.max(Iterators.size(component.getActiveStatusEffects().iterator()) * entryHeight - height + entryHeight, 0);
    }

    public void setScrollPos(final double scrollPos) {
        this.scrollPos = Math.min(Math.max(scrollPos, 0), getScrollBarMax());
    }
    //@Override
    //public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
    //    if (new Rect2d(position.getX(), position.getY(), position.getX() + width, position.getY() + height).isIn(mouseX, mouseY)) {
    //        setScrollPos(scrollPos + amount);
    //        return true;
    //    }
    //    return false;
    //}

    @Override
    public void render(final GuiContext context) {
        //final Battle battle = ((BattleWorldSupplier) world).tbcex_getBattleWorld().getBattle(handle.battleId());
        //if (battle == null) {
        //    return;
        //}
        //final BattleParticipantStateView participantState = battle.getState().getParticipant(handle);
        //if (participantState == null) {
        //    return;
        //}
        //final ParticipantStatusEffectComponentView component = participantState.getComponent(ParticipantComponents.STATUS_EFFECT_COMPONENT_TYPE.key);
        //if (component == null) {
        //    return;
        //}
        //final double x = position.getX();
        //final double y = position.getY();
        //ScissorStack.push(matrices, x, y, x + width, y + height);
        //render(vertexConsumers -> {
        //    double h = -scrollPos;
        //    boolean odd = false;
        //    final VertexConsumer buffer = vertexConsumers.getBuffer(GuiRenderLayers.POSITION_COLOUR_LAYER);
        //    for (final ParticipantStatusEffects.Type type : component.getActiveStatusEffects()) {
        //        final ParticipantStatusEffect statusEffect = component.getStatusEffect(type);
        //        if (statusEffect != null) {
        //            matrices.push();
        //            matrices.translate(0, h, 1);
        //            RenderUtil.renderRectangle(matrices, x, y, width, entryHeight, odd ? FIRST_BACKGROUND_COLOUR : SECOND_BACKGROUND_COLOUR, 255, buffer);
        //            final boolean hovered = new Rect2d(x, y + h, x + width, y + entryHeight + h).isIn(mouseX, mouseY);
        //            renderStatus(statusEffect, hovered, matrices, vertexConsumers);
        //            matrices.pop();
        //            h += entryHeight;
        //            odd = !odd;
        //        }
        //    }
        //});
        //ScissorStack.pop();
    }

    private void renderStatus(final ParticipantStatusEffect effect, final boolean hovered, final MatrixStack matrices, final VertexConsumerProvider vertexConsumers) {
        //final double x = position.getX();
        //final double y = position.getY();
        //renderFitText(matrices, effect.getName(), x, y, width, entryHeight, true, IntRgbColour.WHITE, 255, vertexConsumers);
        //if (hovered) {
        //    renderTooltip(matrices, effect.getDescription(), x, y);
        //}
    }
}
