package io.github.stuff_stuffs.tbcextest.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.*;
import io.github.stuff_stuffs.tbcexutil.common.CodecUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;

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
    public static final Function<BattleParticipantItemStack, Collection<ItemStack>> TO_ITEM_STACK = stack -> {
        if (stack.getItem() instanceof TestBattleParticipantItem) {
            return Collections.singletonList(new ItemStack(Test.TEST_ITEM, stack.getCount()));
        }
        return null;
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
    public List<Text> getTooltip() {
        final Random random = new Random(hashCode());
        final int i = random.nextInt(3);
        if (i == 0) {
            return List.of(new LiteralText("adsdasddgfsdfgdfgdfhggfdhf"), new LiteralText("sad"), new LiteralText("asdasdsdgfdfgd fhgdfhgfghfghfg hfghfghasdsda a dsadasd"));
        }
        if (i == 1) {
            return List.of(new LiteralText("adsdasddgfsdfgdfgdfhggfdhfdddddddddddddddddddddddddddddddddddddd"), new LiteralText("sad"), new LiteralText("asdasdsdgfdfgd fhgdfhgfghfghfg hfghfghasdsda a dsadasd"));
        }
        return List.of(new LiteralText("adsdasddgfsdf"), new LiteralText("sad"), new LiteralText("asdasdsdgfdfgd fhgdfhgfghfghfg"));
    }

    @Override
    public RarityInstance getRarity() {
        return rarity;
    }
}
