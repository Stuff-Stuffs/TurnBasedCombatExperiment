package io.github.stuff_stuffs.tbcexgui.client.screen;

import io.github.stuff_stuffs.tbcexgui.client.widget.*;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.BasicPanelWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class TestScreen extends TBCExScreen {
    private TestScreen(final Text title, final Widget widget) {
        super(title, widget);
    }

    public static TestScreen build() {
        final RootPanelWidget root = new RootPanelWidget();
        final BasicPanelWidget panelWidget = new BasicPanelWidget(WidgetPosition.of(0, 0, 0, 1), () -> true, 1, 1);
        final MutableBoolean toggled = new MutableBoolean(false);
        final ToggleButtonWidget toggleButtonWidget = new ToggleButtonWidget(WidgetPosition.combine(panelWidget::getWidgetPosition, WidgetPosition.of(0.025, 0.1, 0, 1)), 0.45, 0.2, toggled::setValue, () -> {
            final long time = MinecraftClient.getInstance().world.getTime() / 20;
            return time % 10 >= 5;
        }, () -> toggled.booleanValue() ? new LiteralText("Pressed") : new LiteralText("UnPressed"));
        panelWidget.addWidget(toggleButtonWidget);
        TextWidget textWidget = new TextWidget(WidgetPosition.combine(panelWidget::getWidgetPosition, WidgetPosition.of(0.5,0,0,1)), 0.45, 0.5, () -> new LiteralText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean rhoncus eget sapien ut malesuada. Proin rhoncus augue nunc, vel tristique augue elementum a. Cras facilisis sed mauris at tempus. In ut mauris a sem consequat finibus. Nulla at diam non mi bibendum porta eget at mi. Morbi blandit nulla nec enim placerat lobortis. Phasellus faucibus gravida suscipit. Nullam ipsum enim, euismod eget ligula a, congue cursus neque. Fusce vitae elit id turpis maximus posuere. Quisque in quam eget elit aliquam fringilla nec eu odio. Duis ut egestas tellus, sit amet egestas ante. "), () -> 0xff000000, () -> false, () -> Double.MAX_VALUE);
        panelWidget.addWidget(textWidget);
        root.addWidget(panelWidget);
        return new TestScreen(new LiteralText("adsd"), root);
    }
}
