package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import net.minecraft.text.Text;

import java.util.UUID;

public interface BattleParticipantView {
    Text getName();

    UUID getId();

    Team getTeam();
}
