package io.github.stuff_stuffs.tbcexcore.client.gui.hud;

import io.github.stuff_stuffs.tbcexcore.client.gui.widget.hud.BattleHudCurrentTurnWidget;
import io.github.stuff_stuffs.tbcexcore.client.gui.widget.hud.BattleHudEnergyWidget;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.hud.TBCExHud;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public final class BattleHud extends TBCExHud {
    private final BattleHandle handle;
    private final PlayerEntity entity;
    private final BattleHudContext context;

    public BattleHud(final BattleHandle handle, final PlayerEntity entity) {
        super(new RootPanelWidget());
        this.handle = handle;
        this.entity = entity;
        context = new ContextImpl();
        root.addWidget(new BattleHudCurrentTurnWidget(WidgetPosition.of(0.25, 0.05, 1), 0.5, 0.05, handle, entity.world));
        root.addWidget(new BattleHudEnergyWidget(WidgetPosition.of(0.25, 0.975, 1), 0.5, 0.025, context, new BattleParticipantHandle(handle, entity.getUuid()), entity.world));
    }

    public boolean matches(final BattleHandle handle) {
        return this.handle.equals(handle);
    }

    public BattleHudContext getContext() {
        return context;
    }

    @Override
    public void render(MatrixStack matrices, double mouseX, double mouseY, float tickDelta) {
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
            Battle battle = ((BattleWorldSupplier)entity.world).tbcex_getBattleWorld().getBattle(handle);
            if(battle==null) {
                return 0;
            }
            BattleParticipantStateView participant = battle.getState().getParticipant(new BattleParticipantHandle(handle, entity.getUuid()));
            if(participant==null) {
                return 0;
            }
            return participant.getEnergy();
        }

        @Override
        public double getMaxEnergy() {
            Battle battle = ((BattleWorldSupplier)entity.world).tbcex_getBattleWorld().getBattle(handle);
            if(battle==null) {
                return 0;
            }
            BattleParticipantStateView participant = battle.getState().getParticipant(new BattleParticipantHandle(handle, entity.getUuid()));
            if(participant==null) {
                return 0;
            }
            return participant.getStat(BattleParticipantStat.ENERGY_PER_TURN_STAT);
        }
    }
}
