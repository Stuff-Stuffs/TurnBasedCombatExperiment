package io.github.stuff_stuffs.tbcextest.common;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.*;
import io.github.stuff_stuffs.tbcexcore.common.util.CodecUtil;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;

public class TestBattleParticipantItem implements BattleParticipantItem {
    public static final Codec<BattleParticipantItem> CODEC = CodecUtil.TEXT_CODEC.xmap(TestBattleParticipantItem::new, BattleParticipantItem::getName);
    public static final BiPredicate<BattleParticipantItemStack, BattleParticipantItemStack> CAN_MERGE = (stack, stack2) -> {
        if(stack.getItem() instanceof TestBattleParticipantItem first && stack2.getItem() instanceof TestBattleParticipantItem second) {
            return first.name.toString().equals(second.name.toString());
        }
        return false;
    };
    public static final BinaryOperator<BattleParticipantItemStack> MERGER = (stack, stack2) -> {
        if(stack.getItem() instanceof TestBattleParticipantItem && stack2.getItem() instanceof TestBattleParticipantItem) {
            return stack.withCount(stack.getCount() + stack2.getCount());
        }
        throw new RuntimeException();
    };
    private final Text name;

    public TestBattleParticipantItem(Text name) {
        this.name = name;
    }

    @Override
    public List<ParticipantAction> getActions(BattleStateView battleState, BattleParticipantStateView participantState, BattleParticipantInventoryHandle handle) {
        return new ArrayList<>();
    }

    @Override
    public BattleParticipantItemType getType() {
        return Test.TEST_ITEM_TYPE;
    }

    @Override
    public BattleParticipantItemCategory getCategory() {
        return BattleParticipantItemCategory.CONSUMABLE_CATEGORY;
    }

    @Override
    public Text getName() {
        return name;
    }

    @Override
    public List<TooltipComponent> getTooltip() {
        return new ArrayList<>();
    }
}
