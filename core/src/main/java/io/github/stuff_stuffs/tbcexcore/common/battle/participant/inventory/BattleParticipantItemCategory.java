package io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

import java.util.Map;
import java.util.function.Function;

public abstract class BattleParticipantItemCategory {
    public static final BattleParticipantItemCategory CONSUMABLE_CATEGORY = new BattleParticipantItemCategory() {
        @Override
        public String toString() {
            return "CONSUMABLE";
        }
    };
    public static final BattleParticipantItemCategory INVALID_CATEGORY = new BattleParticipantItemCategory() {
        @Override
        public String toString() {
            return "INVALID";
        }
    };
    public static final Function<BattleEquipmentSlot, BattleParticipantItemCategory> BATTLE_EQUIPMENT_CATEGORY = new Function<>() {
        private final Map<BattleEquipmentSlot, BattleParticipantItemCategory> cached = new Reference2ObjectOpenHashMap<>();

        @Override
        public BattleParticipantItemCategory apply(final BattleEquipmentSlot battleEquipmentSlot) {
            return cached.computeIfAbsent(battleEquipmentSlot, s -> new BattleParticipantItemCategory() {
                @Override
                public String toString() {
                    return "EQUIPMENT(" + s.name().toString() + ")";
                }
            });
        }
    };

    private BattleParticipantItemCategory() {
    }
}
