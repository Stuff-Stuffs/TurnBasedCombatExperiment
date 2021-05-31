package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.BattleEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.entity.equipment.PostEquipmentEquipEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.entity.equipment.PostEquipmentUnEquipEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.entity.equipment.PreEquipmentEquipEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.entity.equipment.PreEquipmentUnEquipEvent;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class BattleEquipmentState {
    public static final Codec<BattleEquipmentState> CODEC = Codec.unboundedMap(BattleEquipmentType.REGISTRY, BattleEquipmentType.CODEC).xmap(map -> new BattleEquipmentState(new Reference2ObjectOpenHashMap<>(map)), state -> state.equipmentMap);
    private final Map<BattleEquipmentType, BattleEquipment> equipmentMap;

    private BattleEquipmentState(final Map<BattleEquipmentType, BattleEquipment> equipmentMap) {
        this.equipmentMap = equipmentMap;
    }

    public BattleEquipmentState(final BattleEntity entity, final EntityState state) {
        equipmentMap = new Reference2ObjectOpenHashMap<>();
        for (final BattleEquipmentType type : BattleEquipmentType.REGISTRY) {
            final BattleEquipment equipment = type.applyExtractor(entity);
            if (equipment != null) {
                if (equipment.getType() != type) {
                    throw new RuntimeException();
                }
                equipmentMap.put(type, equipment);
            }
        }
    }

    public @Nullable BattleEquipment get(final BattleEquipmentType type) {
        return equipmentMap.get(type);
    }

    public boolean remove(final BattleEquipmentType type, final EntityState state) {
        if (!type.canEquipMidBattle()) {
            throw new RuntimeException();
        }
        final BattleEquipment equipment = equipmentMap.get(type);
        if (equipment != null) {
            final boolean canceled = state.getEvent(PreEquipmentUnEquipEvent.class).invoker().onUnEquip(state, equipment);
            if (!canceled) {
                equipmentMap.remove(type);
                equipment.deinitEvents();
                state.getEvent(PostEquipmentUnEquipEvent.class).invoker().onUnEquip(state, equipment);
                return true;
            }
        }
        return false;
    }

    public boolean put(final BattleEquipment equipment, final EntityState entityState) {
        if (!equipment.getType().canEquipMidBattle()) {
            throw new RuntimeException();
        }
        final BattleEquipmentType type = equipment.getType();
        final BattleEquipment old = equipmentMap.get(type);
        boolean canContinue = false;
        if (old != null) {
            final boolean canceled = entityState.getEvent(PreEquipmentUnEquipEvent.class).invoker().onUnEquip(entityState, old);
            if (!canceled) {
                canContinue = true;
            }
        } else {
            canContinue = true;
        }
        final boolean valid = canContinue && entityState.getEvent(PreEquipmentEquipEvent.class).invoker().onEquip(entityState, equipment);
        if (valid) {
            final BattleEquipment removed = equipmentMap.remove(type);
            if (removed != null) {
                removed.deinitEvents();
                entityState.getEvent(PostEquipmentUnEquipEvent.class).invoker().onUnEquip(entityState, old);
            }
            equipmentMap.put(type, equipment);
            equipment.initEvents(entityState);
            entityState.getEvent(PostEquipmentEquipEvent.class).invoker().onEquip(entityState, equipment);
            return true;
        }
        return false;
    }
}
