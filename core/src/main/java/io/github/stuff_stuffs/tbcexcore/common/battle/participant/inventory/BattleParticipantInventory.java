package io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipment;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentState;
import io.github.stuff_stuffs.tbcexcore.common.entity.BattleEntity;
import io.github.stuff_stuffs.tbcexcore.common.item.BattleItem;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;

public final class BattleParticipantInventory implements Iterable<Int2ReferenceMap.Entry<BattleParticipantItemStack>> {
    public static final Codec<BattleParticipantInventory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, BattleParticipantItemStack.CODEC).fieldOf("stacks").forGetter(BattleParticipantInventory::createStackMap),
            Codec.unboundedMap(BattleEquipmentSlot.REGISTRY.getCodec(), BattleParticipantItemStack.CODEC).fieldOf("equipment_stacks").forGetter(inventory -> inventory.equipment),
            BattleEquipmentState.CODEC.fieldOf("equipment").forGetter(inventory -> inventory.equipmentState)
    ).apply(instance, BattleParticipantInventory::new));
    private final Int2ReferenceMap<BattleParticipantItemStack> stacks;
    private final Map<BattleEquipmentSlot, BattleParticipantItemStack> equipment;
    private final BattleEquipmentState equipmentState;

    private BattleParticipantInventory(final Map<String, BattleParticipantItemStack> map, final Map<BattleEquipmentSlot, BattleParticipantItemStack> equipment, final BattleEquipmentState equipmentState) {
        stacks = new Int2ReferenceOpenHashMap<>(map.size());
        for (final Map.Entry<String, BattleParticipantItemStack> entry : map.entrySet()) {
            stacks.put(Integer.parseInt(entry.getKey(), 16), entry.getValue());
        }
        this.equipment = new Reference2ObjectOpenHashMap<>(equipment);
        this.equipmentState = equipmentState;
    }

    public BattleParticipantInventory(final BattleEntity entity) {
        stacks = new Int2ReferenceOpenHashMap<>();
        equipment = new Reference2ObjectOpenHashMap<>();
        for (final ItemStack itemStack : entity.tbcex_getInventory()) {
            if (itemStack.getItem() instanceof BattleItem battleItem) {
                final BattleParticipantItemStack stack = battleItem.toBattleItem(itemStack);
                if (stack != null) {
                    giveStack(stack);
                }
            }
        }
        equipmentState = new BattleEquipmentState(entity);
        for (final BattleEquipmentSlot slot : BattleEquipmentSlot.REGISTRY) {
            final ItemStack itemStack = entity.tbcex_getEquipped(slot);
            if (itemStack != null && itemStack.getItem() instanceof BattleItem battleItem) {
                final BattleParticipantItemStack stack = battleItem.toBattleItem(itemStack);
                if (stack != null) {
                    equipment.put(slot, stack);
                }
            }
        }
    }

    private Map<String, BattleParticipantItemStack> createStackMap() {
        final Map<String, BattleParticipantItemStack> stackMap = new Object2ObjectOpenHashMap<>(stacks.size());
        for (final Int2ReferenceMap.Entry<BattleParticipantItemStack> entry : stacks.int2ReferenceEntrySet()) {
            stackMap.put(Integer.toString(entry.getIntKey(), 16), entry.getValue());
        }
        return stackMap;
    }

    private void giveStack(final BattleParticipantItemStack stack) {
        int i = 0;
        while (true) {
            if (!stacks.containsKey(i)) {
                stacks.put(i, stack);
                return;
            } else {
                i++;
            }
        }
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

    public @Nullable BattleParticipantItemStack take(final int handle, final int amount) {
        final BattleParticipantItemStack stack = stacks.get(handle);
        if (stack == null) {
            return null;
        }
        final int taken = Math.min(stack.getCount(), amount);
        final int remainder = stack.getCount() - taken;
        if (remainder == 0) {
            stacks.remove(handle);
        } else {
            stacks.put(handle, stack.withCount(remainder));
        }
        return stack.withCount(taken);
    }

    @Override
    public Iterator<Int2ReferenceMap.Entry<BattleParticipantItemStack>> iterator() {
        return stacks.int2ReferenceEntrySet().iterator();
    }

    @Override
    public Spliterator<Int2ReferenceMap.Entry<BattleParticipantItemStack>> spliterator() {
        return stacks.int2ReferenceEntrySet().spliterator();
    }

    public boolean equip(final BattleParticipantState state, final BattleEquipmentSlot slot, @Nullable final BattleParticipantItemStack equipment) {
        if (equipment == null) {
            if (equipmentState.equip(state, slot, null)) {
                final BattleParticipantItemStack removed = this.equipment.remove(slot);
                if (removed != null) {
                    give(removed);
                    return true;
                }
            }
        } else if (equipment.getItem() instanceof BattleParticipantEquipmentItem equipmentItem) {
            final BattleEquipment instance = equipmentItem.createEquipmentInstance(equipment);
            if (equipmentState.equip(state, slot, instance)) {
                final BattleParticipantItemStack stack = this.equipment.put(slot, equipment);
                if (stack != null) {
                    give(stack);
                }
                return true;
            }
        }
        return false;
    }

    public boolean canEquip(final BattleParticipantInventoryHandle handle, final BattleEquipmentSlot slot) {
        final BattleParticipantItemStack stack = stacks.get(handle.id());
        if (stack.getItem() instanceof BattleParticipantEquipmentItem equipmentItem) {
            return equipmentState.canEquip(equipmentItem.createEquipmentInstance(stack), slot);
        }
        return false;
    }

    public boolean unequip(final BattleParticipantState state, final BattleEquipmentSlot slot) {
        return equip(state, slot, null);
    }

    public @Nullable BattleEquipment getEquipment(final BattleEquipmentSlot slot) {
        return equipmentState.get(slot);
    }

    public @Nullable BattleParticipantItemStack getEquipmentStack(final BattleEquipmentSlot slot) {
        return equipment.get(slot);
    }

    public void initEvents(final BattleParticipantState state) {
        for (final BattleEquipmentSlot slot : BattleEquipmentSlot.REGISTRY) {
            final BattleParticipantItemStack stack = equipment.get(slot);
            if (stack != null && stack.getItem() instanceof BattleParticipantEquipmentItem equipmentItem) {
                final BattleEquipment instance = equipmentItem.createEquipmentInstance(stack);
                if (instance.validSlot(slot)) {
                    equipmentState.equip(state, slot, instance);
                }
            }
        }
        equipmentState.initEvents(state);
    }

    public void deinitEvents() {
        equipmentState.deinitEvents();
    }
}
