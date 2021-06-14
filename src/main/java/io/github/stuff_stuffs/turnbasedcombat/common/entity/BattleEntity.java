package io.github.stuff_stuffs.turnbasedcombat.common.entity;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.Team;
import net.minecraft.item.ItemStack;

public interface BattleEntity {
    Team getTeam();

    Iterable<ItemStack> getBattleAccessibleInventory();
}
