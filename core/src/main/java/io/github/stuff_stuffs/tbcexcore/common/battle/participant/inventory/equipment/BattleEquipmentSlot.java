package io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment;

import io.github.stuff_stuffs.tbcexcore.common.TBCExCore;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

public record BattleEquipmentSlot(Text name) {
    public static final Registry<BattleEquipmentSlot> REGISTRY = FabricRegistryBuilder.createSimple(BattleEquipmentSlot.class, TBCExCore.createId("equipment_slots")).buildAndRegister();
}
