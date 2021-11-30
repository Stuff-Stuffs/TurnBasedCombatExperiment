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
        ParentWidget widget = (ParentWidget) this.widget;
        ParentWidget panel = new BasicPanelWidget(WidgetPosition.of(0.125, 0.125, 1), () -> false, () -> 1, 0.75, 0.75);
        widget.addWidget(panel);
        PlayerStatsWidget statsWidget = new PlayerStatsWidget(WidgetPosition.of(0.125, 0.125, 1),0.375,0.375, 0.125, MinecraftClient.getInstance().player);
        panel.addWidget(statsWidget);
    }
}
