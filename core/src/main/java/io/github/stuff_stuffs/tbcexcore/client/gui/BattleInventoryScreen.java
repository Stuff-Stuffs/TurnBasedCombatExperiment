package io.github.stuff_stuffs.tbcexcore.client.gui;

import io.github.stuff_stuffs.tbcexcore.client.gui.widget.BattleInventoryFilterWidget;
import io.github.stuff_stuffs.tbcexcore.client.gui.widget.BattleInventoryTabWidget;
import io.github.stuff_stuffs.tbcexcore.client.util.ItemStackInfo;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexgui.client.screen.TBCExScreen;
import io.github.stuff_stuffs.tbcexgui.client.widget.SuppliedWidgetPosition;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BattleInventoryScreen extends TBCExScreen {
    private final BattleParticipantHandle handle;
    private BattleInventoryFilterWidget navigationWidget;
    private BattleInventoryTabWidget inventoryWidget;
    private final World world;
    private boolean init = false;
    private final List<ItemStackInfo> stackInfos = new ArrayList<>();

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
            final List<ItemStackInfo> infos = new ArrayList<>();
            inventoryWidget = new BattleInventoryTabWidget(new SuppliedWidgetPosition(() -> 0.25, () -> 0, () -> 0), infos, 1 / 128.0, 1 / 16.0, 1 / 128.0, () -> 0.5, () -> 1, value -> {

            });
            navigationWidget = new BattleInventoryFilterWidget(new SuppliedWidgetPosition(() -> 0, () -> 0, () -> 0), () -> 0.25, () -> 1, 1 / 128.0, 1 / 16.0, 1 / 128.0, world, handle, BattleInventoryFilterWidget.DEFAULTS, value -> {
                infos.clear();
                infos.addAll(navigationWidget.getFiltered());
                inventoryWidget.setSelectedIndex(-1);
            });
            navigationWidget.setSelectedIndex(0);
            infos.addAll(navigationWidget.getFiltered());
            widget.addWidget(inventoryWidget);
            widget.addWidget(navigationWidget);
        }
        inventoryWidget.tick();
    }
}
