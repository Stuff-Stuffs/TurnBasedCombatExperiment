package io.github.stuff_stuffs.tbcexcore.client.gui;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexgui.client.screen.TBCExScreen;
import io.github.stuff_stuffs.tbcexgui.client.util.ItemStackLike;
import io.github.stuff_stuffs.tbcexgui.client.widget.Widget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import io.github.stuff_stuffs.tbcexgui.client.widget.interaction.BasicScrollableWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.interaction.InventorySlotsWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.BasicPanelWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BattleInventoryScreen extends TBCExScreen {
    private final BattleParticipantHandle handle;
    private final World world;
    private boolean init = false;

    public BattleInventoryScreen(final BattleParticipantHandle handle, final World world) {
        super(new LiteralText("Battle Inventory"), new RootPanelWidget());
        this.handle = handle;
        this.world = world;
    }

    @Override
    public void tick() {
        super.tick();
        if (!init) {
            init = true;
            final RootPanelWidget widget = (RootPanelWidget) this.widget;
            final BasicPanelWidget mainPanel = new BasicPanelWidget(WidgetPosition.of(0.05, 0.125, 0), () -> false, () -> 4, 0.9, 0.75);
            widget.addWidget(mainPanel);
            final List<BasicScrollableWidget.SizedWidget> sizedWidgets = new ArrayList<>();
            final BasicScrollableWidget scrollableWidget = new BasicScrollableWidget(WidgetPosition.of(0.25, 0.25, 0), sizedWidgets, 7/16.0, 0.375, 1 / 32.0, 1 / 16.0);
            sizedWidgets.add(new BasicScrollableWidget.SizedWidget() {
                private final InventorySlotsWidget widget = new InventorySlotsWidget(WidgetPosition.combine(() -> WidgetPosition.of(scrollableWidget.getScrollOffsetX() + 1/16.0, -scrollableWidget.getScrollOffsetY(), 0), WidgetPosition.of(0.25, 0.25, 0)), 1/16d, new ItemStackLike[5][20], 4, new InventorySlotsWidget.Handler() {
                });
                @Override
                public double getWidth() {
                    return 5/16d;
                }

                @Override
                public double getHeight() {
                    return 20/16d;
                }

                @Override
                public Widget getWidget() {
                    return widget;
                }
            });
            mainPanel.addWidget(scrollableWidget);
        }
    }
}
