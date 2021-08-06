package io.github.stuff_stuffs.tbcexgui.client.screen;

import io.github.stuff_stuffs.tbcexgui.client.util.ItemStackLike;
import io.github.stuff_stuffs.tbcexgui.client.widget.Widget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import io.github.stuff_stuffs.tbcexgui.client.widget.interaction.InventorySlotsWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.interaction.PressableButtonWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.interaction.SingleHotbarSlotWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.BasicPanelWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.ArrayList;
import java.util.List;

public class TestScreen extends TBCExScreen {
    private TestScreen(final Text title, final Widget widget) {
        super(title, widget);
    }

    public static TestScreen build() {
        final RootPanelWidget root = new RootPanelWidget();
        final BasicPanelWidget panel = new BasicPanelWidget(WidgetPosition.of(0.25, 0.25, 0), () -> true, () -> 1, 0.5, 0.5);
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
                () -> 2,
                () -> true,
                0.25,
                0.1,
                () -> new LiteralText("Long Text that should be centered"),
                () -> tooltipComponents,
                () -> {
                }
        );
        final MutableBoolean selected = new MutableBoolean(false);
        final SingleHotbarSlotWidget slot = new SingleHotbarSlotWidget(WidgetPosition.combine(panel::getWidgetPosition, WidgetPosition.of(0.5, 0, 0)), 1 / 16d, () -> 1, selected::booleanValue, (hotbarSlotWidget, integer) -> {
        }, (hotbarSlotWidget, integer) -> selected.setValue(!selected.booleanValue()), (hotbarSlotWidget, aDouble) -> {
        }, hotbarSlotWidget -> {
        }, hotbarSlotWidget -> {
        }, null);
        final InventorySlotsWidget slots = new InventorySlotsWidget(WidgetPosition.combine(panel::getWidgetPosition, WidgetPosition.of(0, 0.25, 0)), 1 / 16d, new ItemStackLike[5][6], 1, new InventorySlotsWidget.Handler() {
            @Override
            public void onClick(final InventorySlotsWidget widget, final int button, final int x, final int y) {
                if (button == 0) {
                    widget.setSelected(x, y);
                }
            }

            @Override
            public void focusChange(InventorySlotsWidget widget, boolean focused) {
                if(!focused) {
                    widget.setSelected(-1,-1);
                }
            }
        });
        panel.addWidget(button);
        panel.addWidget(slot);
        panel.addWidget(slots);
        return new TestScreen(new LiteralText("adsd"), root);
    }
}
