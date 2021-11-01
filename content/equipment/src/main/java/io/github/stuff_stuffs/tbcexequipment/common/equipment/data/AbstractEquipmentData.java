package io.github.stuff_stuffs.tbcexequipment.common.equipment.data;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexequipment.common.part.PartInstance;
import net.minecraft.util.Identifier;

import java.util.Map;

public abstract class AbstractEquipmentData implements EquipmentData {
    protected final Map<Identifier, PartInstance> parts;

    protected AbstractEquipmentData(final Map<Identifier, PartInstance> parts) {
        this.parts = parts;
    }

    @Override
    public Map<Identifier, PartInstance> getParts() {
        return parts;
    }

    @Override
    public void initEvents(final BattleParticipantState state, final BattleEquipmentSlot slot) {
        for (final PartInstance part : parts.values()) {
            part.getData().initEvents(state, slot);
        }
    }

    @Override
    public void deinitEvents() {
        for (final PartInstance part : parts.values()) {
            part.getData().deinitEvents();
        }
    }
}
