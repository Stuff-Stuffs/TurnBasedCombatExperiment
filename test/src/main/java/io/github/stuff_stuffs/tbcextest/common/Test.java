package io.github.stuff_stuffs.tbcextest.common;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemType;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Test implements ModInitializer {
    public static final Item TEST_ITEM = new TestItem();
    public static final BattleParticipantItemType TEST_ITEM_TYPE = new BattleParticipantItemType(TestBattleParticipantItem.CODEC, TestBattleParticipantItem.CAN_MERGE, TestBattleParticipantItem.MERGER);

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier("tbcextest", "test_item"), TEST_ITEM);
        Registry.register(BattleParticipantItemType.REGISTRY, new Identifier("tbcextest", "test_item"), TEST_ITEM_TYPE);
    }
}
