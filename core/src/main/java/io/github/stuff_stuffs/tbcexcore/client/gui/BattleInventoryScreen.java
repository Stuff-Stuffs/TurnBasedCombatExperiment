package io.github.stuff_stuffs.tbcexcore.client.gui;

import io.github.stuff_stuffs.tbcexcore.client.gui.widget.BattleInventoryTabWidget;
import io.github.stuff_stuffs.tbcexcore.client.util.ItemStackInfo;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantInventoryHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemStack;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.screen.TBCExScreen;
import io.github.stuff_stuffs.tbcexgui.client.widget.SuppliedWidgetPosition;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BattleInventoryScreen extends TBCExScreen {
    private final BattleParticipantHandle handle;
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
        stackInfos.clear();
        final Battle battle = ((BattleWorldSupplier) world).tbcex_getBattleWorld().getBattle(handle.battleId());
        if (battle != null) {
            final BattleParticipantStateView participant = battle.getState().getParticipant(handle);
            if (participant != null) {
                final Iterator<BattleParticipantInventoryHandle> iterator = participant.getInventoryIterator();
                while (iterator.hasNext()) {
                    final BattleParticipantInventoryHandle next = iterator.next();
                    stackInfos.add(new ItemStackInfo(next, participant.getItemStack(next)));
                }
                for (final BattleEquipmentSlot slot : BattleEquipmentSlot.REGISTRY) {
                    final BattleParticipantItemStack stack = participant.getEquipmentStack(slot);
                    if (stack != null) {
                        stackInfos.add(new ItemStackInfo(handle, slot, stack));
                    }
                }
            }
        }
        if (!init) {
            init = true;
            final RootPanelWidget widget = (RootPanelWidget) this.widget;
            inventoryWidget = new BattleInventoryTabWidget(new SuppliedWidgetPosition(() -> 0, () -> 0, () -> 0), stackInfos, 1 / 32.0, 1 / 16.0, 1 / 128.0, () -> 1, () -> 1, value -> {

            });
            widget.addWidget(inventoryWidget);
        }
        inventoryWidget.tick();
    }
}
