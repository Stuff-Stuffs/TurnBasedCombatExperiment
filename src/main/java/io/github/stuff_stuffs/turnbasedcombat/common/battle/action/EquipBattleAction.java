package io.github.stuff_stuffs.turnbasedcombat.common.battle.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment.BattleEquipment;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment.BattleEquipmentType;

public final class EquipBattleAction extends BattleAction {
    public static final Codec<EquipBattleAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BattleParticipantHandle.CODEC.fieldOf("handle").forGetter(action -> action.handle),
            BattleEquipmentType.CODEC.fieldOf("equipment").forGetter(action -> action.equipment),
            Codec.INT.fieldOf("slot").forGetter(action -> action.slot)
    ).apply(instance, EquipBattleAction::new));
    private final BattleEquipment equipment;
    private final int slot;

    public EquipBattleAction(final BattleParticipantHandle handle, final BattleEquipment equipment, final int slot) {
        super(handle);
        this.equipment = equipment;
        this.slot = slot;
    }

    @Override
    public void applyToState(final BattleState state) {
        final EntityState participant = state.getParticipant(handle);
        if (participant == null) {
            throw new RuntimeException();
        }
        final BattleEquipment old = participant.getEquiped(equipment.getType());
        if (participant.equip(equipment)) {
            participant.getInventory().setSlot(slot, null);
            if (old != null) {
                participant.getInventory().setSlot(slot, old.toBattleItem());
            }
        }
    }

    @Override
    protected <T> T encode(final DynamicOps<T> ops) {
        return CODEC.encodeStart(ops, this).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
    }

    public static <T> EquipBattleAction decode(final T o, final DynamicOps<T> ops) {
        return CODEC.parse(ops, o).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
    }
}
