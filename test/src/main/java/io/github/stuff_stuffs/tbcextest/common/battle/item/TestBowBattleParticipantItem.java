package io.github.stuff_stuffs.tbcextest.common.battle.item;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantEquipActionUtil;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.*;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipment;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcexequipment.common.battle.equipment.BattleEquipmentSlots;
import io.github.stuff_stuffs.tbcextest.common.Test;
import io.github.stuff_stuffs.tbcextest.common.battle.equipment.TestBowEquipment;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Function;

public class TestBowBattleParticipantItem implements BattleParticipantEquipmentItem {
    public static final Codec<BattleParticipantItem> CODEC = Codec.unit(TestBowBattleParticipantItem::new).xmap(Function.identity(), o -> (TestBowBattleParticipantItem) o);
    private static final BattleParticipantItem.RarityInstance RARITY_INSTANCE = new BattleParticipantItem.RarityInstance(BattleParticipantItem.Rarity.LEGENDARY, 1);

    @Override
    public List<ParticipantAction> getActions(final BattleStateView battleState, final BattleParticipantStateView participantState, final BattleParticipantInventoryHandle handle) {
        return ParticipantEquipActionUtil.getActions(participantState, handle);
    }

    @Override
    public BattleParticipantItemType getType() {
        return Test.TEST_BOW_ITEM_TYPE;
    }

    @Override
    public boolean isInCategory(final BattleParticipantItemCategory category) {
        return category == BattleParticipantItemCategory.BATTLE_EQUIPMENT_CATEGORY.apply(BattleEquipmentSlots.MAIN_HAND_SLOT);
    }

    @Override
    public Text getName() {
        return new LiteralText("Test Bow");
    }

    @Override
    public List<Text> getTooltip() {
        return List.of();
    }

    @Override
    public BattleParticipantItem.RarityInstance getRarity() {
        return RARITY_INSTANCE;
    }

    @Override
    public BattleEquipment createEquipmentInstance(final BattleParticipantItemStack stack) {
        return new TestBowEquipment();
    }
}
