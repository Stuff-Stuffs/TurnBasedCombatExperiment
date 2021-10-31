package io.github.stuff_stuffs.tbcexequipment.common.equipment.data;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexequipment.common.part.PartInstance;

import java.util.List;

public abstract class AbstractEquipmentData implements EquipmentData {
    protected final List<PartInstance> parts;

    protected AbstractEquipmentData(final List<PartInstance> parts) {
        this.parts = parts;
    }

    @Override
    public List<PartInstance> getParts() {
        return parts;
    }

    @Override
    public void initEvents(final BattleParticipantState state, final BattleEquipmentSlot slot) {
        for (final PartInstance part : parts) {
            part.getData().initEvents(state, slot);
        }
    }

    @Override
    public void deinitEvents() {
        for (final PartInstance part : parts) {
            part.getData().deinitEvents();
        }
    }
}
