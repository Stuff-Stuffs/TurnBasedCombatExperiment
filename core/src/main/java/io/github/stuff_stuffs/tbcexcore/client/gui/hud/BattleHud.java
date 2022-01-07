package io.github.stuff_stuffs.tbcexcore.client.gui.hud;

import io.github.stuff_stuffs.tbcexcore.client.gui.widget.hud.BattleHudCurrentTurnWidget;
import io.github.stuff_stuffs.tbcexcore.client.gui.widget.hud.BattleHudEnergyWidget;
import io.github.stuff_stuffs.tbcexcore.client.gui.widget.hud.BattleHudHealthWidget;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.hud.TBCExHud;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetModifiers;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public final class BattleHud extends TBCExHud {
    private final BattleHandle handle;
    private final PlayerEntity entity;
    private final BattleHudContext context;

    public BattleHud(final BattleHandle handle, final PlayerEntity entity) {
        super(new RootPanelWidget(false));
        this.handle = handle;
        this.entity = entity;
        context = new ContextImpl();
        ((RootPanelWidget) root).addChild(WidgetModifiers.positioned(new BattleHudCurrentTurnWidget(0.5, 0.05, handle, entity.world), () -> 0.25, () -> 0.05));
        ((RootPanelWidget) root).addChild(WidgetModifiers.positioned(new BattleHudEnergyWidget(0.5, 0.025, context, new BattleParticipantHandle(handle, entity.getUuid()), entity.world), () -> 0.25, () -> 0.975));
        ((RootPanelWidget) root).addChild(WidgetModifiers.positioned(new BattleHudHealthWidget(handle, entity.world), () -> 0, () -> 0));
    }

    public boolean matches(final BattleHandle handle) {
        return this.handle.equals(handle);
    }

    public BattleHudContext getContext() {
        return context;
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float tickDelta) {
        super.render(matrices, mouseX, mouseY, tickDelta);
        context.setPotentialActionCost(0);
    }

    private final class ContextImpl implements BattleHudContext {
        private double potentialActionCost = 0;

        @Override
        public void setPotentialActionCost(final double cost) {
            potentialActionCost = cost;
        }

        @Override
        public double getPotentialActionCost() {
            return potentialActionCost;
        }

        @Override
        public double getEnergy() {
            final Battle battle = ((BattleWorldSupplier) entity.world).tbcex_getBattleWorld().getBattle(handle);
            if (battle == null) {
                return 0;
            }
            final BattleParticipantStateView participant = battle.getState().getParticipant(new BattleParticipantHandle(handle, entity.getUuid()));
            if (participant == null) {
                return 0;
            }
            return participant.getEnergy();
        }

        @Override
        public double getMaxEnergy() {
            final Battle battle = ((BattleWorldSupplier) entity.world).tbcex_getBattleWorld().getBattle(handle);
            if (battle == null) {
                return 0;
            }
            final BattleParticipantStateView participant = battle.getState().getParticipant(new BattleParticipantHandle(handle, entity.getUuid()));
            if (participant == null) {
                return 0;
            }
            return participant.getStat(BattleParticipantStat.ENERGY_PER_TURN_STAT);
        }
    }
}
