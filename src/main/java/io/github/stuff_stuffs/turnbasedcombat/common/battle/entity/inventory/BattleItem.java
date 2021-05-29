package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.inventory;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.action.EntityAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityStateView;
import net.minecraft.item.ItemStack;

public interface BattleItem {
    EntityAction useAction(EntityStateView entityState);

    BattleItemType getType();

    ItemStack toItemStack();
}
