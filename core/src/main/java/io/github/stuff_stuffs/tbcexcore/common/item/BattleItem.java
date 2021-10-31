package io.github.stuff_stuffs.tbcexcore.common.item;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemStack;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface BattleItem {
    @Nullable BattleParticipantItemStack toBattleItem(ItemStack stack);
}
