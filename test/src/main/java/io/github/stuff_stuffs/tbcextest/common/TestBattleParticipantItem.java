package io.github.stuff_stuffs.tbcextest.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
    public static final Codec<BattleParticipantItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CodecUtil.TEXT_CODEC.fieldOf("name").forGetter(BattleParticipantItem::getName),
            RarityInstance.CODEC.fieldOf("rarity").forGetter(BattleParticipantItem::getRarity)
    ).apply(instance, TestBattleParticipantItem::new));
    public static final BiPredicate<BattleParticipantItemStack, BattleParticipantItemStack> CAN_MERGE = (stack, stack2) -> {
        if (stack.getItem() instanceof TestBattleParticipantItem first && stack2.getItem() instanceof TestBattleParticipantItem second) {
            return first.name.toString().equals(second.name.toString());
        }
        return false;
    };
    public static final BinaryOperator<BattleParticipantItemStack> MERGER = (stack, stack2) -> {
        if (stack.getItem() instanceof TestBattleParticipantItem && stack2.getItem() instanceof TestBattleParticipantItem) {
            return stack.withCount(stack.getCount() + stack2.getCount());
        }
        throw new RuntimeException();
    };
    private final Text name;
    private final RarityInstance rarity;

    public TestBattleParticipantItem(final Text name, final RarityInstance rarity) {
        this.name = name;
        this.rarity = rarity;
    }

    @Override
    public List<ParticipantAction> getActions(final BattleStateView battleState, final BattleParticipantStateView participantState, final BattleParticipantInventoryHandle handle) {
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

    @Override
    public RarityInstance getRarity() {
        return rarity;
    }
}
