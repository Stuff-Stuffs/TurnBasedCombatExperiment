package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import net.minecraft.text.Text;

public interface BattleParticipantView {
    Text getName();

    int getId();

    Team getTeam();
}
