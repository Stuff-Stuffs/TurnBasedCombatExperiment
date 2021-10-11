package io.github.stuff_stuffs.tbcexcore.client.gui;

import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.screen.TBCExScreen;
import io.github.stuff_stuffs.tbcexgui.client.widget.ParentWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import io.github.stuff_stuffs.tbcexgui.client.widget.interaction.PressableButtonWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.BasicPanelWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.BooleanSupplier;

public class BattleMenuScreen extends TBCExScreen {
    private final BattleParticipantHandle handle;
    private final World world;
    private boolean init = false;


    public BattleMenuScreen(final BattleParticipantHandle handle, final World world) {
        super(new LiteralText("battle_menu_screen"), new RootPanelWidget());
        this.handle = handle;
        this.world = world;
    }

    @Override
    public void tick() {
        super.tick();
        if (!init) {
            init = true;
            final ParentWidget widget = (ParentWidget) this.widget;
            final int widgetCount = 2;
            BooleanSupplier isTurn = () -> {
                Battle battle = ((BattleWorldSupplier)world).tbcex_getBattleWorld().getBattle(handle.battleId());
                if(battle==null) {
                    return false;
                }
                final BattleParticipantStateView participant = battle.getState().getParticipant(handle);
                if(participant==null) {
                    return false;
                }
                return handle.equals(battle.getState().getCurrentTurn());
            };
            final WidgetPosition panelPos = WidgetPosition.of(0.5 - 0.08, 0.5 - (widgetCount * 0.1) - 0.05, 1);
            final BasicPanelWidget panelWidget = new BasicPanelWidget(panelPos, () -> false, () -> 1, 0.16, widgetCount * 0.1 + 0.05);
            final PressableButtonWidget inventoryWidget = new PressableButtonWidget(WidgetPosition.combine(panelPos, WidgetPosition.of(0.01, 0.025, 1)), () -> 1, isTurn, 0.14, 0.1, () -> new LiteralText("Inventory"), List::of, () -> MinecraftClient.getInstance().setScreen(new BattleInventoryScreen(handle, world)));
            final PressableButtonWidget movementWidget = new PressableButtonWidget(WidgetPosition.combine(panelPos, WidgetPosition.of(0.01, 0.1375, 1)), () -> 1, isTurn, 0.14, 0.1, () -> new LiteralText("Move"), List::of, () -> MinecraftClient.getInstance().setScreen(new BattleMoveScreen(handle, world)));
            widget.addWidget(panelWidget);
            panelWidget.addWidget(inventoryWidget);
            panelWidget.addWidget(movementWidget);
        }
    }
}
