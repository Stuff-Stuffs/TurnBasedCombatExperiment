package io.github.stuff_stuffs.turnbasedcombat.common.entity;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.Team;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface BattleEntity {
    Team getTeam();

    Iterable<ItemStack> tbcex_getInventory();

    double tbcex_getMaxHealth();

    double tbcex_getCurrentHealth();

    double tbcex_getStrength();

    double tbcex_getIntelligence();

    double tbcex_getVitality();

    double tbcex_getDexterity();

    @Nullable ItemStack tbcex_getEquipped(BattleEquipmentSlot slot);
}
