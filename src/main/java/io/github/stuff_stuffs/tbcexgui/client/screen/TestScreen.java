package io.github.stuff_stuffs.tbcexgui.client.screen;

import io.github.stuff_stuffs.tbcexgui.client.widget.HotbarSlotWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.Widget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import io.github.stuff_stuffs.tbcexgui.client.widget.interaction.PressableButtonWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.BasicPanelWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class TestScreen extends TBCExScreen {
    private TestScreen(final Text title, final Widget widget) {
        super(title, widget);
    }

    public static TestScreen build() {
        final RootPanelWidget root = new RootPanelWidget();
        final BasicPanelWidget panel = new BasicPanelWidget(WidgetPosition.of(0.25, 0.25, 0), () -> true, () -> 0.25, 0.5, 0.5);
        root.addWidget(panel);
        final List<TooltipComponent> tooltipComponents = new ArrayList<>();
        tooltipComponents.add(TooltipComponent.of(new LiteralText("First").asOrderedText()));
        tooltipComponents.add(TooltipComponent.of(new LiteralText("Second").asOrderedText()));
        tooltipComponents.add(TooltipComponent.of(new LiteralText("Third: lorem ipsum sorem idot").asOrderedText()));
        final PressableButtonWidget button = new PressableButtonWidget(
                WidgetPosition.combine(
                        panel::getWidgetPosition,
                        WidgetPosition.of(0, 0, 0)
                ),
                () -> 1,
                () -> true,
                0.25,
                0.1,
                () -> new LiteralText("Long Text that should be centered"),
                () -> tooltipComponents,
                () -> {
                }
        );
        final HotbarSlotWidget slot = new HotbarSlotWidget(WidgetPosition.combine(panel::getWidgetPosition, WidgetPosition.of(0.5, 0, 0)), 1 / 16d, () -> 0.25, (hotbarSlotWidget, integer) -> {
        }, (hotbarSlotWidget, integer) -> {
        }, (hotbarSlotWidget, aDouble) -> {
        }, hotbarSlotWidget -> {
        }, hotbarSlotWidget -> {
        }, null);
        panel.addWidget(button);
        panel.addWidget(slot);
        return new TestScreen(new LiteralText("adsd"), root);
    }
}
