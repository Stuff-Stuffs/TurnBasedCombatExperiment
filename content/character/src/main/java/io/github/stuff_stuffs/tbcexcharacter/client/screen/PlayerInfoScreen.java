package io.github.stuff_stuffs.tbcexcharacter.client.screen;

import io.github.stuff_stuffs.tbcexcharacter.client.screen.widget.PlayerStatsWidget;
import io.github.stuff_stuffs.tbcexgui.client.screen.TBCExScreen;
import io.github.stuff_stuffs.tbcexgui.client.widget.ParentWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.BasicPanelWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;

public class PlayerInfoScreen extends TBCExScreen {
    public PlayerInfoScreen() {
        super(new LiteralText("PlayerInfo"), new RootPanelWidget());
        final ParentWidget widget = (ParentWidget) this.widget;
        final double edgeStart = 0.015625;
        final double width = 1 - 2 * edgeStart;
        final ParentWidget panel = new BasicPanelWidget(WidgetPosition.of(edgeStart - 0.015, edgeStart - 0.015, 1), () -> false, () -> 2, width + 0.03, width + 0.03);
        widget.addWidget(panel);
        final PlayerStatsWidget statsWidget = new PlayerStatsWidget(WidgetPosition.of(edgeStart + width / 2, edgeStart + width / 2, 1), width / 2, width / 2, 0.0625, MinecraftClient.getInstance().player);
        panel.addWidget(statsWidget);
    }
}
