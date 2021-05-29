package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.inventory;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;

//TODO redo this
public final class EntityInventory implements EntityInventoryView, Iterable<BattleItem> {
    public static final Codec<EntityInventory> CODEC = Codec.unboundedMap(Codec.INT, BattleItemType.CODEC).xmap(EntityInventory::new, inventory -> inventory.items);
    private final Int2ReferenceMap<BattleItem> items;

    private EntityInventory(final Map<Integer, BattleItem> map) {
        items = new Int2ReferenceOpenHashMap<>(map);
    }

    public EntityInventory() {
        items = new Int2ReferenceOpenHashMap<>();
    }

    public void setSlot(final int slot, final BattleItem item) {
        if (slot < 0) {
            throw new RuntimeException();
        }
        if (item == null) {
            items.remove(slot);
        } else {
            items.put(slot, item);
        }
    }

    @Override
    public int getSlot(final BattleItem item) {
        for (final Int2ReferenceMap.Entry<BattleItem> entry : items.int2ReferenceEntrySet()) {
            if (entry.getValue() == item) {
                return entry.getIntKey();
            }
        }
        return -1;
    }

    @Override
    public @Nullable BattleItem getSlot(final int slot) {
        return items.get(slot);
    }

    @Override
    public Iterator<BattleItem> iterator() {
        return items.values().iterator();
    }
}
