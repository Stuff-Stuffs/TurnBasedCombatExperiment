package io.github.stuff_stuffs.tbcexcore.common.item;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemStack;
import net.minecraft.item.ItemStack;

public interface BattleItem {
    BattleParticipantItemStack toBattleItem(ItemStack stack);
}
