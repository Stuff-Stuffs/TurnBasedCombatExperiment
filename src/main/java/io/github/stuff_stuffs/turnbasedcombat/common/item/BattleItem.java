package io.github.stuff_stuffs.turnbasedcombat.common.item;

import net.minecraft.item.ItemStack;

public interface BattleItem {
    //TODO fix this naming mess
    io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.inventory.BattleItem getBattleItem(ItemStack stack);
}
