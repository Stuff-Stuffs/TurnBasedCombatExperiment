package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.BattleEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;
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
        for (final BattleEquipment equipment : equipmentMap.values()) {
            equipment.onEquip(state);
        }
    }

    public @Nullable BattleEquipment get(final BattleEquipmentType type) {
        return equipmentMap.get(type);
    }

    public void remove(final BattleEquipmentType type, final EntityState state) {
        if (!type.canEquipMidBattle()) {
            throw new RuntimeException();
        }
        final BattleEquipment equipment = equipmentMap.remove(type);
        if (equipment != null) {
            equipment.onUnEquip(state);
        }
    }

    public void put(final BattleEquipment equipment, final EntityState state) {
        if (!equipment.getType().canEquipMidBattle()) {
            throw new RuntimeException();
        }
        final BattleEquipment old = equipmentMap.put(equipment.getType(), equipment);
        if (old != null) {
            old.onUnEquip(state);
        }
    }
}
