package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.entity.equipment.PostEquipmentEquipEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.entity.equipment.PostEquipmentUnEquipEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.entity.equipment.PreEquipmentEquipEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.entity.equipment.PreEquipmentUnEquipEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.BattleEntity;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class BattleEquipmentState {
    public static final Codec<BattleEquipmentState> CODEC = Codec.unboundedMap(BattleEquipmentSlot.REGISTRY, BattleEquipmentType.CODEC).xmap(map -> new BattleEquipmentState(new Reference2ObjectOpenHashMap<>(map)), state -> state.equipmentMap);
    private final Map<BattleEquipmentSlot, BattleEquipment> equipmentMap;

    private BattleEquipmentState(final Map<BattleEquipmentSlot, BattleEquipment> equipmentMap) {
        this.equipmentMap = equipmentMap;
    }

    public BattleEquipmentState(final BattleEntity entity, final EntityState state) {
        equipmentMap = new Reference2ObjectOpenHashMap<>();
        for (final BattleEquipmentSlot slot : BattleEquipmentSlot.REGISTRY) {
            final BattleEquipment equipment = slot.applyExtractor(entity);
            if (equipment != null) {
                if (equipment.getType() != slot.type()) {
                    throw new RuntimeException();
                }
                equipmentMap.put(slot, equipment);
            }
        }
    }

    public @Nullable BattleEquipment get(final BattleEquipmentSlot type) {
        return equipmentMap.get(type);
    }

    public boolean remove(final BattleEquipmentSlot slot, final EntityState state) {
        if (!slot.type().canEquipMidBattle()) {
            throw new RuntimeException();
        }
        final BattleEquipment equipment = equipmentMap.get(slot);
        if (equipment != null) {
            final boolean canceled = state.getEvent(PreEquipmentUnEquipEvent.class).invoker().onUnEquip(state, slot, equipment);
            if (!canceled) {
                equipmentMap.remove(slot);
                equipment.deinitEvents();
                state.getEvent(PostEquipmentUnEquipEvent.class).invoker().onUnEquip(state, slot, equipment);
                return true;
            }
        }
        return false;
    }

    public boolean put(final BattleEquipmentSlot slot, final BattleEquipment equipment, final EntityState entityState) {
        if (!equipment.getType().canEquipMidBattle() || slot.type() != equipment.getType()) {
            throw new RuntimeException();
        }
        final BattleEquipment old = equipmentMap.get(slot);
        boolean canContinue = false;
        if (old != null) {
            final boolean canceled = entityState.getEvent(PreEquipmentUnEquipEvent.class).invoker().onUnEquip(entityState, slot, old);
            if (!canceled) {
                canContinue = true;
            }
        } else {
            canContinue = true;
        }
        final boolean valid = canContinue && entityState.getEvent(PreEquipmentEquipEvent.class).invoker().onEquip(entityState, slot, equipment);
        if (valid) {
            final BattleEquipment removed = equipmentMap.remove(slot);
            if (removed != null) {
                removed.deinitEvents();
                entityState.getEvent(PostEquipmentUnEquipEvent.class).invoker().onUnEquip(entityState, slot, old);
            }
            equipmentMap.put(slot, equipment);
            equipment.initEvents(entityState);
            entityState.getEvent(PostEquipmentEquipEvent.class).invoker().onEquip(entityState, slot, equipment);
            return true;
        }
        return false;
    }
}
