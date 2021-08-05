package io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.inventory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.inventory.equipment.BattleEquipment;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.inventory.equipment.BattleEquipmentState;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.BattleEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.item.BattleItem;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;

public final class BattleParticipantInventory implements Iterable<Int2ReferenceMap.Entry<BattleParticipantItemStack>> {
    public static final Codec<BattleParticipantInventory> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.unboundedMap(Codec.INT, BattleParticipantItemStack.CODEC).fieldOf("stacks").forGetter(inventory -> inventory.stacks),Codec.unboundedMap(BattleEquipmentSlot.REGISTRY, BattleParticipantItemStack.CODEC).fieldOf("equipmentStacks").forGetter(inventory -> inventory.equipment), BattleEquipmentState.CODEC.fieldOf("equipment").forGetter(inventory -> inventory.equipmentState)).apply(instance, BattleParticipantInventory::new));
    private final Int2ReferenceMap<BattleParticipantItemStack> stacks;
    private final Map<BattleEquipmentSlot, BattleParticipantItemStack> equipment;
    private final BattleEquipmentState equipmentState;

    private BattleParticipantInventory(final Map<Integer, BattleParticipantItemStack> map, final Map<BattleEquipmentSlot, BattleParticipantItemStack> equipment, BattleEquipmentState equipmentState) {
        stacks = new Int2ReferenceOpenHashMap<>(map);
        this.equipment = new Reference2ObjectOpenHashMap<>(equipment);
        this.equipmentState = equipmentState;
    }

    public BattleParticipantInventory(final BattleEntity entity) {
        stacks = new Int2ReferenceOpenHashMap<>();
        equipment = new Reference2ObjectOpenHashMap<>();
        for (final ItemStack itemStack : entity.tbcex_getInventory()) {
            if (itemStack.getItem() instanceof BattleItem battleItem) {
                give(battleItem.toBattleItem(itemStack));
            }
        }
        for (final BattleEquipmentSlot slot : BattleEquipmentSlot.REGISTRY) {
            final ItemStack itemStack = entity.tbcex_getEquipped(slot);
            if (itemStack.getItem() instanceof BattleItem battleItem) {
                equipment.put(slot, battleItem.toBattleItem(itemStack));
            }
        }
        equipmentState = new BattleEquipmentState(entity);
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

    public boolean unequip(final BattleParticipantState state, final BattleEquipmentSlot slot) {
        return equip(state, slot, null);
    }

    public @Nullable BattleEquipment getEquipment(final BattleEquipmentSlot slot) {
        return equipmentState.get(slot);
    }

    public void initEvents(BattleParticipantState state) {
        equipmentState.initEvents(state);
    }

    public void uninitEvents() {
        equipmentState.uninitEvents();
    }
}
