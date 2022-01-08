package io.github.stuff_stuffs.tbcexcharacter.client.screen;

import io.github.stuff_stuffs.tbcexcharacter.client.screen.widget.PlayerStatsWidget;
import io.github.stuff_stuffs.tbcexcore.client.gui.widget.BattleInventoryFilterWidget;
import io.github.stuff_stuffs.tbcexgui.client.screen.TBCExScreen;
import io.github.stuff_stuffs.tbcexgui.client.widget.PositionedStackWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetModifiers;
import io.github.stuff_stuffs.tbcexgui.client.widget.interaction.SelectionWheelWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.GriddedPanelWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import io.github.stuff_stuffs.tbcexutil.common.colour.IntRgbColour;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;

import java.util.List;

public class PlayerInfoScreen extends TBCExScreen {
    public PlayerInfoScreen() {
        super(new LiteralText("PlayerInfo"), new RootPanelWidget(true));
        final RootPanelWidget widget = (RootPanelWidget) this.widget;
        final PositionedStackWidget stackWidget = new PositionedStackWidget();
        final GriddedPanelWidget characterInfoPanel = new GriddedPanelWidget(2, 2, 0.375, 0.375, false, () -> IntRgbColour.BLACK.pack(127));
        final PlayerStatsWidget statsWidget = new PlayerStatsWidget(0.375, 0.375, 0.0625, MinecraftClient.getInstance().player);
        characterInfoPanel.setSlot(statsWidget, 0, 1);
        //TODO level up button
        final SelectionWheelWidget selection = SelectionWheelWidget.builder().addEntry(
                BattleInventoryFilterWidget.FIRST_BACKGROUND_COLOUR, 127, BattleInventoryFilterWidget.SECOND_BACKGROUND_COLOUR, 127,
                () -> new LiteralText("Character Info"), List.of(), () -> true, () -> stackWidget.push(WidgetModifiers.positioned(characterInfoPanel, () -> 0.125, () -> 0.125))
        ).build(0.15, 0.3, 0.3125);
        stackWidget.setFallback(WidgetModifiers.positioned(selection, () -> 0.5, () -> 0.5));
        widget.addChild(stackWidget);
    }
}
