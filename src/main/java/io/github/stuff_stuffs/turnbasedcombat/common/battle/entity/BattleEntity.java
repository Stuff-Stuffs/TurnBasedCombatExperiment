package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.Team;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.inventory.EntityInventory;
import net.minecraft.text.Text;

public interface BattleEntity {
    SkillInfo getSkillInfo();

    Team getTeam();

    Text getBattleName();

    //should be a fresh instance
    EntityInventory getBattleInventory();
}
