package io.github.stuff_stuffs.tbcexcore.client.gui;

import io.github.stuff_stuffs.tbcexcore.client.gui.widget.hud.BattleHudCurrentTurnWidget;
import io.github.stuff_stuffs.tbcexcore.client.gui.widget.hud.BattleHudEnergyWidget;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexgui.client.hud.TBCExHud;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import net.minecraft.entity.player.PlayerEntity;

public final class BattleHud extends TBCExHud {
    private final BattleHandle handle;
    private final PlayerEntity entity;

    public BattleHud(final BattleHandle handle, final PlayerEntity entity) {
        super(new RootPanelWidget());
        this.handle = handle;
        this.entity = entity;
        root.addWidget(new BattleHudCurrentTurnWidget(WidgetPosition.of(0.25, 0.05, 1), 0.5, 0.05, handle, entity.world));
        root.addWidget(new BattleHudEnergyWidget(WidgetPosition.of(0.25, 0.975, 1), 0.5, 0.025, new BattleParticipantHandle(handle, entity.getUuid()), entity.world));
    }

    public boolean matches(final BattleHandle handle) {
        return this.handle.equals(handle);
    }
}
