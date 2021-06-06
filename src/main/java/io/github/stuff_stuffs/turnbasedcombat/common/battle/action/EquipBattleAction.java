package io.github.stuff_stuffs.turnbasedcombat.common.battle.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment.BattleEquipment;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment.BattleEquipmentType;

public final class EquipBattleAction extends BattleAction {
    public static final Codec<EquipBattleAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BattleParticipantHandle.CODEC.fieldOf("handle").forGetter(action -> action.handle),
            BattleEquipmentSlot.REGISTRY.fieldOf("slot").forGetter(action -> action.slot),
            BattleEquipmentType.CODEC.fieldOf("equipment").forGetter(action -> action.equipment),
            Codec.INT.fieldOf("slot").forGetter(action -> action.inventorySlot)
    ).apply(instance, EquipBattleAction::new));
    private final BattleEquipmentSlot slot;
    private final BattleEquipment equipment;
    private final int inventorySlot;

    public EquipBattleAction(final BattleParticipantHandle handle, BattleEquipmentSlot slot, final BattleEquipment equipment, final int inventorySlot) {
        super(handle);
        this.slot = slot;
        this.equipment = equipment;
        this.inventorySlot = inventorySlot;
    }

    @Override
    public void applyToState(final BattleState state) {
        final EntityState participant = state.getParticipant(handle);
        if (participant == null) {
            throw new RuntimeException();
        }
        final BattleEquipment old = participant.getEquiped(slot);
        if (participant.equip(slot, equipment)) {
            participant.getInventory().setSlot(inventorySlot, null);
            if (old != null) {
                participant.getInventory().setSlot(inventorySlot, old.toBattleItem());
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
