package io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.inventory;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;

public final class BattleParticipantInventory implements Iterable<Int2ReferenceMap.Entry<BattleParticipantItemStack>> {
    public static final Codec<BattleParticipantInventory> CODEC = Codec.unboundedMap(Codec.INT, BattleParticipantItemStack.CODEC).xmap(BattleParticipantInventory::new, inventory -> inventory.stacks);
    private final Int2ReferenceMap<BattleParticipantItemStack> stacks;

    private BattleParticipantInventory(final Map<Integer, BattleParticipantItemStack> map) {
        stacks = new Int2ReferenceOpenHashMap<>(map);
    }

    public BattleParticipantInventory() {
        stacks = new Int2ReferenceOpenHashMap<>();
    }

    public int give(final BattleParticipantItemStack stack) {
        for (final Int2ReferenceMap.Entry<BattleParticipantItemStack> entry : stacks.int2ReferenceEntrySet()) {
            final BattleParticipantItemStack value = entry.getValue();
            final BattleParticipantItemStack merged = BattleParticipantItemType.merge(stack, value);
            if (merged != null) {
                entry.setValue(merged);
                return entry.getIntKey();
            }
        }
        int i = 0;
        while (true) {
            if (!stacks.containsKey(i)) {
                stacks.put(i, stack);
                return i;
            } else {
                i++;
            }
        }
    }

    public @Nullable BattleParticipantItemStack get(final int handle) {
        return stacks.get(handle);
    }

    public int take(final int handle, final int amount) {
        final BattleParticipantItemStack stack = stacks.get(handle);
        if (stack == null) {
            return 0;
        }
        final int taken = Math.min(stack.getCount(), amount);
        final int remainder = stack.getCount() - taken;
        if (remainder == 0) {
            stacks.remove(handle);
        } else {
            stacks.put(handle, stack.withCount(remainder));
        }
        return taken;
    }

    @Override
    public Iterator<Int2ReferenceMap.Entry<BattleParticipantItemStack>> iterator() {
        return stacks.int2ReferenceEntrySet().iterator();
    }

    @Override
    public Spliterator<Int2ReferenceMap.Entry<BattleParticipantItemStack>> spliterator() {
        return stacks.int2ReferenceEntrySet().spliterator();
    }
}
