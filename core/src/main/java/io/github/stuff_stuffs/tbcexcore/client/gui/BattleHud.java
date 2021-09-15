package io.github.stuff_stuffs.tbcexcore.client.gui;

import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexgui.client.hud.TBCExHud;
import io.github.stuff_stuffs.tbcexgui.client.widget.Widget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetHandle;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;

public final class BattleHud extends TBCExHud {
    private final BattleHandle handle;
    private final PlayerEntity entity;

    public BattleHud(final BattleHandle handle, final PlayerEntity entity) {
        super(new RootPanelWidget());
        this.handle = handle;
        this.entity = entity;
    }

    public boolean matches(final BattleHandle handle) {
        return this.handle.equals(handle);
    }
}
