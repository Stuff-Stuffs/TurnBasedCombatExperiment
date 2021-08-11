package io.github.stuff_stuffs.tbcexcore.client.gui;

import io.github.stuff_stuffs.tbcexcore.client.gui.widget.BattleInventoryWidget;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexgui.client.screen.TBCExScreen;
import io.github.stuff_stuffs.tbcexgui.client.widget.SuppliedWidgetPosition;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;

public class BattleInventoryScreen extends TBCExScreen {
    private final BattleParticipantHandle handle;
    private BattleInventoryWidget inventoryWidget;
    private final World world;
    private boolean init = false;

    public BattleInventoryScreen(final BattleParticipantHandle handle, final World world) {
        super(new LiteralText("Battle Inventory"), new RootPanelWidget());
        this.handle = handle;
        this.world = world;
    }

    @Override
    public void tick() {
        super.tick();
        if (!init) {
            init = true;
            final RootPanelWidget widget = (RootPanelWidget) this.widget;
            inventoryWidget = new BattleInventoryWidget(new SuppliedWidgetPosition(() -> 0, () -> 0, () -> 0), handle, world, 1 / 32.0, 1 / 16.0, 1 / 128.0, 1, 1);
            widget.addWidget(inventoryWidget);
        }
        inventoryWidget.tick();
    }
}
