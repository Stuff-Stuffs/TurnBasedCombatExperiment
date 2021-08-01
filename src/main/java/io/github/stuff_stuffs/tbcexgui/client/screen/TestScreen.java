package io.github.stuff_stuffs.tbcexgui.client.screen;

import io.github.stuff_stuffs.tbcexgui.client.widget.Widget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import io.github.stuff_stuffs.tbcexgui.client.widget.interaction.PressableButtonWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.BasicPanelWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class TestScreen extends TBCExScreen {
    private TestScreen(final Text title, final Widget widget) {
        super(title, widget);
    }

    public static TestScreen build() {
        final RootPanelWidget root = new RootPanelWidget();
        final BasicPanelWidget panel = new BasicPanelWidget(WidgetPosition.of(0.25, 0.25, 0), () -> true, () -> 1, 0.5, 0.5);
        root.addWidget(panel);
        PressableButtonWidget button = new PressableButtonWidget(WidgetPosition.combine(panel::getWidgetPosition, WidgetPosition.of(0,0,0)), () -> 1, () -> true, 0.25, 0.1, () -> new LiteralText("Long Text that should be centered"), () -> {});
        panel.addWidget(button);
        return new TestScreen(new LiteralText("adsd"), root);
    }
}
