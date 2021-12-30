package io.github.stuff_stuffs.tbcexcore.client.gui;

import io.github.stuff_stuffs.tbcexcore.client.gui.hud.BattleHudContext;
import io.github.stuff_stuffs.tbcexcore.client.network.BattleActionSender;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.BattleAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexgui.client.screen.TBCExScreen;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableInt;

//fixme
public class BattleInventoryScreen extends TBCExScreen {
    private final BattleParticipantHandle handle;
    private final BattleHudContext hudContext;
    //private WidgetHandle selectionWidgetHandle;
    private final MutableInt selected = new MutableInt(0);
    private final World world;
    //private BattleInventoryFilterWidget navigationWidget;
    //private BattleInventorySorterWidget sorterWidget;
    //private BattleInventoryTabWidget inventoryWidget;
    //private BattleInventoryPreviewWidget previewWidget;
    //private BattleInventoryActionSelectionWidget selectionWidget;
    private final boolean init = false;

    public BattleInventoryScreen(final BattleParticipantHandle handle, final BattleHudContext hudContext, final World world) {
        super(new LiteralText("Battle Inventory"), new RootPanelWidget());
        this.handle = handle;
        this.hudContext = hudContext;
        this.world = world;
    }

    @Override
    public void render(final MatrixStack matrices, final int mouseX, final int mouseY, final float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        //if (selectionWidget != null && selectionWidget.shouldClose()) {
        //    selectionWidget = null;
        //((ParentWidget) widget).removeWidget(selectionWidgetHandle);
        //}
    }

    @Override
    public void onClose() {
        MinecraftClient.getInstance().setScreen(new BattleMenuScreen(handle, world, hudContext));
    }

    @Override
    public void tick() {
        super.tick();
        //final Battle battle = ((BattleWorldSupplier) world).tbcex_getBattleWorld().getBattle(handle.battleId());
        //if (!init && battle != null) {
        //    init = true;
        //    final RootPanelWidget widget = (RootPanelWidget) this.widget;
        //    final List<ItemStackInfo> infos = new ArrayList<>();
        //    final DoubleSupplier startX = () -> -(widget.getScreenWidth() - 1) / 2.0;
        //    final DoubleSupplier startY = () -> -(widget.getScreenHeight() - 1) / 2.0;
        //    final SuppliedWidgetPosition tabPosition = new SuppliedWidgetPosition(() -> startX.getAsDouble() + widget.getScreenWidth() * 1 / 4.0, () -> startY.getAsDouble() + widget.getScreenHeight() * 1 / 8.0, () -> 0);
        //    inventoryWidget = new BattleInventoryTabWidget(tabPosition, () -> {
        //        infos.clear();
        //        infos.addAll(navigationWidget.getFiltered());
        //        sorterWidget.sort(infos);
        //        return infos;
        //    }, 1 / 128.0, 1 / 16.0, 1 / 128.0, () -> widget.getScreenWidth() * 3 / 8.0, () -> widget.getScreenHeight() * 7 / 8.0, this::select, (index, mouseX, mouseY) -> {
        //        if (index >= 0 && index < infos.size()) {
        //            final BattleParticipantStateView participantState;
        //            final Battle b = ((BattleWorldSupplier) world).tbcex_getBattleWorld().getBattle(handle.battleId());
        //            if (b != null) {
        //                participantState = b.getState().getParticipant(handle);
        //            } else {
        //                throw new RuntimeException();
        //            }
        //            if (participantState == null) {
        //                throw new RuntimeException();
        //            }
        //            final Either<BattleParticipantInventoryHandle, Pair<BattleParticipantHandle, BattleEquipmentSlot>> location = infos.get(index).location;
        //            final List<ParticipantAction> actions = new ArrayList<>();
        //            actions.addAll(location.map(
        //                    h -> participantState.getItemStack(h).getItem().getActions(b.getState(), participantState, h),
        //                    p -> participantState.getEquipment(p.getSecond()).getActions(b.getState(), participantState, p.getSecond())
        //            ));
        //            if (actions.size() > 0) {
        //                selectionWidget = new BattleInventoryActionSelectionWidget(WidgetPosition.of(mouseX, mouseY, 10), b.getState(), handle, battle, actions);
        //                if (selectionWidgetHandle != null) {
        //                    widget.removeWidget(selectionWidgetHandle);
        //                }
        //                selectionWidgetHandle = widget.addWidget(selectionWidget);
        //            } else {
        //                selectionWidget = null;
        //                if (selectionWidgetHandle != null) {
        //                    widget.removeWidget(selectionWidgetHandle);
        //                }
        //            }
        //        } else {
        //            selectionWidget = null;
        //            if (selectionWidgetHandle != null) {
        //                widget.removeWidget(selectionWidgetHandle);
        //            }
        //        }
        //    });

        //    sorterWidget = new BattleInventorySorterWidget(new SuppliedWidgetPosition(() -> startX.getAsDouble() + widget.getScreenWidth() * 1 / 4.0, startY, () -> 1), () -> widget.getScreenWidth() * 3 / 8.0, () -> widget.getScreenHeight() * 1 / 8.0, 1 / 128.0, 1 / 8.0, 1 / 128.0, BattleInventorySorterWidget.DEFAULTS, i -> {
        //        sorterWidget.sort(infos);
        //        inventoryWidget.resetSelectedIndex();
        //    });

        //    navigationWidget = new BattleInventoryFilterWidget(new SuppliedWidgetPosition(startX, startY, () -> 3), () -> widget.getScreenWidth() * 1 / 4.0, widget::getScreenHeight, 1 / 128.0, 1 / 16.0, 1 / 128.0, world, handle, BattleInventoryFilterWidget.DEFAULTS, value -> {
        //        infos.clear();
        //        infos.addAll(navigationWidget.getFiltered());
        //        inventoryWidget.resetSelectedIndex();
        //    });
        //    navigationWidget.setSelectedIndex(0);
        //    infos.addAll(navigationWidget.getFiltered());

        //    previewWidget = new BattleInventoryPreviewWidget(new SuppliedWidgetPosition(() -> startX.getAsDouble() + widget.getScreenWidth() * 5 / 8.0, startY, () -> 2), () -> widget.getScreenWidth() * 3 / 8.0, widget::getScreenHeight, 0.45, battle.getState(), () -> {
        //        if (selected.intValue() >= 0 && selected.intValue() < infos.size()) {
        //            return infos.get(selected.intValue());
        //        }
        //        return null;
        //    });
        //    if (selectionWidget != null && selectionWidget.shouldClose()) {
        //        widget.removeWidget(selectionWidgetHandle);
        //    }

        //    widget.addWidget(inventoryWidget);
        //    widget.addWidget(sorterWidget);
        //    widget.addWidget(navigationWidget);
        //    widget.addWidget(previewWidget);
        //}
        //if (init) {
        //    inventoryWidget.tick();
        //}
        //if (battle == null || !handle.equals(battle.getState().getCurrentTurn())) {
        //    MinecraftClient.getInstance().setScreen(null);
        //}
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private void select(final int index) {
        selected.setValue(index);
    }

    private void send(final BattleAction<?> battleAction) {
        BattleActionSender.send(handle.battleId(), battleAction);
    }
}
