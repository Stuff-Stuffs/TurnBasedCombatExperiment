package io.github.stuff_stuffs.tbcextest.common.item;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemStack;
import io.github.stuff_stuffs.tbcexcore.common.item.BattleItem;
import io.github.stuff_stuffs.tbcextest.common.battle.item.TestBowBattleParticipantItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TestBowItem extends Item implements BattleItem {
    public TestBowItem() {
        super(new FabricItemSettings().maxCount(1));
    }

    @Override
    public BattleParticipantItemStack toBattleItem(final ItemStack stack) {
        return new BattleParticipantItemStack(new TestBowBattleParticipantItem(), stack.getCount());
    }
}
