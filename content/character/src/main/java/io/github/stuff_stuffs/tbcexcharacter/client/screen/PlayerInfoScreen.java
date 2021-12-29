package io.github.stuff_stuffs.tbcexcharacter.client.screen;

import io.github.stuff_stuffs.tbcexcharacter.client.screen.widget.PlayerStatsWidget;
import io.github.stuff_stuffs.tbcexcore.client.gui.widget.BattleInventoryFilterWidget;
import io.github.stuff_stuffs.tbcexgui.client.screen.TBCExScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;

import java.util.List;

public class PlayerInfoScreen extends TBCExScreen {
    public PlayerInfoScreen() {
        super(new LiteralText("PlayerInfo"), new RootPanelWidget());
        final ParentWidget widget = (ParentWidget) this.widget;
        final WidgetPosition panelPos = WidgetPosition.of(0.5, 0.5, 1);
        final StackWidget stackWidget = new StackWidget();
        final BasicPanelWidget characterInfoPanel = new BasicPanelWidget(WidgetPosition.of(0.125, 0.125, 1), 0.75, 0.75);
        final PlayerStatsWidget statsWidget = new PlayerStatsWidget(WidgetPosition.of(0.625, 0.125, 1), 0.375, 0.375, 0.0625, MinecraftClient.getInstance().player);
        characterInfoPanel.addWidget(statsWidget);
        //TODO level up button
        final SelectionWheelWidget selection = SelectionWheelWidget.builder().addEntry(
                BattleInventoryFilterWidget.FIRST_BACKGROUND_COLOUR, 127, BattleInventoryFilterWidget.SECOND_BACKGROUND_COLOUR, 127,
                () -> new LiteralText("Character Info"), List.of(), () -> true, () -> stackWidget.push(characterInfoPanel)
        ).build(0.15, 0.3, 0.3125, panelPos);
        stackWidget.setFallback(selection);
        widget.addWidget(stackWidget);
    }
}
