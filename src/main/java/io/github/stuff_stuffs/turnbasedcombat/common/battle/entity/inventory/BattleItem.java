package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.inventory;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.action.EntityAction;
import net.minecraft.item.ItemStack;

public interface BattleItem {
    BattleItemType getType();

    ItemStack toItemStack();

    EntityAction useAction(EntityStateView entityState);
}
