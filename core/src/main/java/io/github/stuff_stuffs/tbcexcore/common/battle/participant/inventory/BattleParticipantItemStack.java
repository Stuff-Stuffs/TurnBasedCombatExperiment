package io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public final class BattleParticipantItemStack {
    public static final Codec<BattleParticipantItemStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BattleParticipantItemType.CODEC.fieldOf("item").forGetter(stack -> stack.item),
            Codec.INT.fieldOf("count").forGetter(stack -> stack.count)
    ).apply(instance, BattleParticipantItemStack::new));
    private final BattleParticipantItem item;
    private final int count;


    public BattleParticipantItemStack(final BattleParticipantItem item, final int count) {
        if (count < 1) {
            throw new RuntimeException("Can't have stack with less than one item");
        }
        this.item = item;
        this.count = count;
    }

    public BattleParticipantItemStack withCount(final int count) {
        return new BattleParticipantItemStack(item, count);
    }

    public int getCount() {
        return count;
    }

    public BattleParticipantItem getItem() {
        return item;
    }

    @Override
    public String toString() {
        return "BattleParticipantItemStack{" +
                "item=" + item +
                ", count=" + count +
                '}';
    }
}
