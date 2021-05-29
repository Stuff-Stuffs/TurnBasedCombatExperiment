package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.inventory;

import org.jetbrains.annotations.Nullable;

public interface EntityInventoryView {
    int getSlot(BattleItem item);

    @Nullable BattleItem getSlot(int slot);
}
