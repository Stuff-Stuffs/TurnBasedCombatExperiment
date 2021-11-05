package io.github.stuff_stuffs.tbcexequipment.common.equipment;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.actions.LongSwordActions;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.data.EquipmentData;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class EquipmentActions {
    private static final Map<EquipmentType<?>, List<Extractor<?>>> EXTRACTORS = new Reference2ObjectOpenHashMap<>();

    public static <T extends EquipmentData> void register(final EquipmentType<T> type, final Extractor<T> extractor) {
        EXTRACTORS.computeIfAbsent(type, k -> new ArrayList<>()).add(extractor);
    }

    public static <T extends EquipmentData> List<ParticipantAction> getActions(final EquipmentType<T> type, final T data, final BattleStateView battleState, final BattleParticipantStateView participantView, final BattleEquipmentSlot slot) {
        final List<Extractor<?>> extractors = EXTRACTORS.get(type);
        if (extractors == null) {
            return List.of();
        } else {
            final List<ParticipantAction> actionInstances = new ArrayList<>(extractors.size());
            for (final Extractor<?> extractor : extractors) {
                final ParticipantAction instance = ((Extractor<T>) extractor).create(data, battleState, participantView, slot);
                if (instance != null) {
                    actionInstances.add(instance);
                }
            }
            return actionInstances;
        }
    }

    public interface Extractor<T extends EquipmentData> {
        @Nullable ParticipantAction create(T data, final BattleStateView battleState, final BattleParticipantStateView handle, final BattleEquipmentSlot slot);
    }

    public static void init() {
        LongSwordActions.init();
    }

    private EquipmentActions() {
    }
}
