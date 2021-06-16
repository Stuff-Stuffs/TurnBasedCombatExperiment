package io.github.stuff_stuffs.turnbasedcombat.common.entity;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.Team;
import net.minecraft.item.ItemStack;

public interface BattleEntity {
    Team getTeam();

    Iterable<ItemStack> tbcex_getInventory();

    double tbcex_getMaxHealth();

    double tbcex_getCurrentHealth();

    double tbcex_getStrength();

    double tbcex_getIntelligence();

    double tbcex_getVitality();

    double tbcex_getDexterity();
}
