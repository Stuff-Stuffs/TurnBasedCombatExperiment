package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.Team;
import net.minecraft.text.Text;

public interface BattleEntity {
    SkillInfo getSkillInfo();
    Team getTeam();
    Text getBattleName();
}
