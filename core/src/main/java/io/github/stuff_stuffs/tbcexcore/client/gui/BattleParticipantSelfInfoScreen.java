package io.github.stuff_stuffs.tbcexcore.client.gui;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexgui.client.screen.TBCExScreen;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;

public class BattleParticipantSelfInfoScreen extends TBCExScreen {
    private final World world;
    private final BattleParticipantHandle participantHandle;

    public BattleParticipantSelfInfoScreen(final World world, final BattleParticipantHandle participantHandle) {
        super(new LiteralText("Participant Info"), new RootPanelWidget());
        this.world = world;
        this.participantHandle = participantHandle;
        /*fixme
        final ParentWidget root = (ParentWidget) widget;
        final ParentWidget panel = new BasicPanelWidget(WidgetPosition.of(0.125, 0.125, 1), 0.75, 0.75);
        final Widget statList = new ParticipantSelfStatListWidget(WidgetPosition.combine(panel::getWidgetPosition, WidgetPosition.of(0.005, 0.380, 0)), 0.365, 0.365, 0.05, participantHandle, participantHandle, world);
        final ParticipantStatusEffectListWidget statusEffectWidget = new ParticipantStatusEffectListWidget(WidgetPosition.combine(panel::getWidgetPosition, WidgetPosition.of(0.380, 0.380, 0)), 0.365, 0.365, 0.05, participantHandle, world);
        panel.addWidget(statList);
        panel.addWidget(statusEffectWidget);
        root.addWidget(panel);
        */
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
