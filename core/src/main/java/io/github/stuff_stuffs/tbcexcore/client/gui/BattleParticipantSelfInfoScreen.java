package io.github.stuff_stuffs.tbcexcore.client.gui;

import io.github.stuff_stuffs.tbcexcore.client.gui.widget.info.ParticipantSelfStatListWidget;
import io.github.stuff_stuffs.tbcexcore.client.gui.widget.info.ParticipantStatusEffectListWidget;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexgui.client.screen.TBCExScreen;
import io.github.stuff_stuffs.tbcexgui.client.widget.Widget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetModifiers;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.GriddedPanelWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import io.github.stuff_stuffs.tbcexutil.common.colour.IntRgbColour;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;

public class BattleParticipantSelfInfoScreen extends TBCExScreen {
    private final World world;
    private final BattleParticipantHandle participantHandle;

    public BattleParticipantSelfInfoScreen(final World world, final BattleParticipantHandle participantHandle) {
        super(new LiteralText("Participant Info"), new RootPanelWidget(true));
        this.world = world;
        this.participantHandle = participantHandle;
        final RootPanelWidget root = (RootPanelWidget) widget;
        final GriddedPanelWidget panelWidget = new GriddedPanelWidget(2, 2, 0.375, 0.375, false, () -> IntRgbColour.BLACK.pack(127));
        final Widget statList = new ParticipantSelfStatListWidget(0.365, 0.365, 0.05, participantHandle, participantHandle, world);
        final ParticipantStatusEffectListWidget statusEffectWidget = new ParticipantStatusEffectListWidget(0.365, 0.365, 0.05, participantHandle, world);
        panelWidget.setSlot(statList, 0, 1);
        panelWidget.setSlot(statusEffectWidget, 1, 1);
        root.addChild(WidgetModifiers.positioned(panelWidget, () -> 0.125, () -> 0.125));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
