package io.github.stuff_stuffs.turnbasedcombat.common.item;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.inventory.BattleParticipantItemStack;
import net.minecraft.item.ItemStack;

public interface BattleItem {
    BattleParticipantItemStack toBattleItem(ItemStack stack);
}
