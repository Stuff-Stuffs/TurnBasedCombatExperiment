package io.github.stuff_stuffs.tbcexcore.client.gui;

import io.github.stuff_stuffs.tbcexcore.client.gui.widget.BattleInventoryFilterWidget;
import io.github.stuff_stuffs.tbcexcore.client.gui.widget.BattleInventoryPreviewWidget;
import io.github.stuff_stuffs.tbcexcore.client.gui.widget.BattleInventorySorterWidget;
import io.github.stuff_stuffs.tbcexcore.client.gui.widget.BattleInventoryTabWidget;
import io.github.stuff_stuffs.tbcexcore.client.util.ItemStackInfo;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.screen.TBCExScreen;
import io.github.stuff_stuffs.tbcexgui.client.widget.SuppliedWidgetPosition;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleSupplier;

public class BattleInventoryScreen extends TBCExScreen {
    private final BattleParticipantHandle handle;
    private BattleInventoryFilterWidget navigationWidget;
    private BattleInventorySorterWidget sorterWidget;
    private BattleInventoryTabWidget inventoryWidget;
    private BattleInventoryPreviewWidget previewWidget;
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
            final List<ItemStackInfo> infos = new ArrayList<>();
            final DoubleSupplier startX = () -> -(widget.getScreenWidth() - 1) / 2.0;
            final DoubleSupplier startY = () -> -(widget.getScreenHeight() - 1) / 2.0;
            MutableInt selected = new MutableInt(0);
            inventoryWidget = new BattleInventoryTabWidget(new SuppliedWidgetPosition(() -> startX.getAsDouble() + widget.getScreenWidth() * 1 / 4.0, () -> startY.getAsDouble() + widget.getScreenHeight() * 1 / 8.0, () -> 0), infos, 1 / 128.0, 1 / 16.0, 1 / 128.0, () -> widget.getScreenWidth() * 3 / 8.0, () -> widget.getScreenHeight() * 7 / 8.0, selected::setValue);

            sorterWidget = new BattleInventorySorterWidget(new SuppliedWidgetPosition(() -> startX.getAsDouble() + widget.getScreenWidth() * 1 / 4.0, startY, () -> 0), () -> widget.getScreenWidth() * 3 / 8.0, () -> widget.getScreenHeight() * 1 / 8.0, 1 / 128.0, 1 / 8.0, 1 / 128.0, BattleInventorySorterWidget.DEFAULTS, i -> {
                sorterWidget.sort(infos);
                inventoryWidget.setSelectedIndex(-1);
            });

            navigationWidget = new BattleInventoryFilterWidget(new SuppliedWidgetPosition(startX, startY, () -> 0), () -> widget.getScreenWidth() * 1 / 4.0, () -> 1, 1 / 128.0, 1 / 16.0, 1 / 128.0, world, handle, BattleInventoryFilterWidget.DEFAULTS, value -> {
                infos.clear();
                infos.addAll(navigationWidget.getFiltered());
                inventoryWidget.setSelectedIndex(-1);
            });
            navigationWidget.setSelectedIndex(0);
            infos.addAll(navigationWidget.getFiltered());

            previewWidget = new BattleInventoryPreviewWidget(new SuppliedWidgetPosition(() -> startX.getAsDouble() + widget.getScreenWidth() * 5 / 8.0, startY, () -> 0), () -> widget.getScreenWidth()*3/8.0, widget::getScreenHeight, 0.45, ((BattleWorldSupplier)world).tbcex_getBattleWorld().getBattle(handle.battleId()).getState(), () -> {
                if(selected.intValue()>=0&&selected.intValue()<infos.size()) {
                    return infos.get(selected.intValue());
                }
                return null;
            });

            widget.addWidget(inventoryWidget);
            widget.addWidget(sorterWidget);
            widget.addWidget(navigationWidget);
            widget.addWidget(previewWidget);
        }
        inventoryWidget.tick();
    }
}
