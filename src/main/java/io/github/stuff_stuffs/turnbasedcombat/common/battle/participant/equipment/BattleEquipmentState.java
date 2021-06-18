package io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.equipment;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.BattleEntity;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class BattleEquipmentState {
    public static final Codec<BattleEquipmentState> CODEC = Codec.unboundedMap(BattleEquipmentSlot.REGISTRY, BattleEquipmentType.CODEC).xmap(BattleEquipmentState::new, state -> state.map);
    private final Reference2ObjectMap<BattleEquipmentSlot, BattleEquipment> map;

    private BattleEquipmentState(final Map<BattleEquipmentSlot, BattleEquipment> map) {
        this.map = new Reference2ObjectOpenHashMap<>(map);
    }

    public BattleEquipmentState(final BattleEntity entity) {
        map = new Reference2ObjectOpenHashMap<>();
        for (final BattleEquipmentSlot slot : BattleEquipmentSlot.REGISTRY) {
            final BattleEquipment extracted = slot.extract(entity);
            if (extracted!=null && !extracted.validSlot(slot)) {
                throw new RuntimeException();
            }
            map.put(slot, extracted);
        }
    }

    public boolean equip(final BattleParticipantState state, final BattleEquipmentSlot slot, @Nullable final BattleEquipment equipment) {
        if (equipment != null) {
            if (equipment.validSlot(slot)) {
                throw new IllegalArgumentException();
            }
        }
        final BattleEquipment old = map.get(slot);
        if (!state.getEvent(BattleParticipantStateView.PRE_EQUIPMENT_CHANGE_EVENT).invoker().onEquipmentChange(state, slot, old, equipment)) {
            if (equipment != null) {
                equipment.initEvents(state);
                map.put(slot, equipment);
            } else {
                map.remove(slot);
            }
            if (old != null) {
                old.deinitEvents();
            }
            state.getEvent(BattleParticipantStateView.POST_EQUIPMENT_CHANGE_EVENT).invoker().onEquipmentChange(state, slot, old, equipment);
            return true;
        }
        return false;
    }

    public boolean unequip(final BattleParticipantState state, final BattleEquipmentSlot slot) {
        return equip(state, slot, null);
    }

    public @Nullable BattleEquipment get(final BattleEquipmentSlot slot) {
        return map.get(slot);
    }

    public void initEvents(final BattleParticipantState state) {
        for (final BattleEquipment equipment : map.values()) {
            equipment.initEvents(state);
        }
    }
}
