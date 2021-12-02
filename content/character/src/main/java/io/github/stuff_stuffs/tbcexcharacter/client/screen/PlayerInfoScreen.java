package io.github.stuff_stuffs.tbcexcharacter.client.screen;

import io.github.stuff_stuffs.tbcexcharacter.client.screen.widget.PlayerStatsWidget;
import io.github.stuff_stuffs.tbcexcharacter.common.entity.CharacterInfo;
import io.github.stuff_stuffs.tbcexcharacter.mixin.api.PlayerCharacterInfoSupplier;
import io.github.stuff_stuffs.tbcexgui.client.screen.TBCExScreen;
import io.github.stuff_stuffs.tbcexgui.client.widget.ParentWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.TabsWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class PlayerInfoScreen extends TBCExScreen {
    public PlayerInfoScreen() {
        super(new LiteralText("PlayerInfo"), new RootPanelWidget());
        final ParentWidget widget = (ParentWidget) this.widget;
        final double edgeStart = 0.015625;
        final double width = 1 - 2 * edgeStart;
        final TabsWidget tabsWidget = new TabsWidget(WidgetPosition.of(1 - edgeStart - 0.015 + 0.0375, edgeStart - 0.015, 1), TabsWidget.Side.RIGHT, 0.125);
        widget.addWidget(tabsWidget);
        tabsWidget.addPanel("main", new LiteralText("Character View"), width + 0.03, width + 0.03, new Identifier("tbcexgui", "gui/transparent"));
        final ParentWidget mainPanel = tabsWidget.getPanel("main");
        final PlayerStatsWidget statsWidget = new PlayerStatsWidget(WidgetPosition.of(edgeStart + width / 2, edgeStart + width / 2, 1), width / 2, width / 2, 0.0625, MinecraftClient.getInstance().player);
        mainPanel.addWidget(statsWidget);
        final CharacterInfo characterInfo = ((PlayerCharacterInfoSupplier) MinecraftClient.getInstance().player).tbcex_getCharacterInfo();
        if (characterInfo.getCurrentLevelProgress() >= 1) {
            tabsWidget.addPanel("level_up", new LiteralText("Level Up"), width + 0.03, width + 0.03, new Identifier("tbcexgui", "gui/transparent"));
            final ParentWidget levelUpPanel = tabsWidget.getPanel("level_up");
        }
    }
}
