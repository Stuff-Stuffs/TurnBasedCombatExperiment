package io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.inventory.equipment;

import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

public record BattleEquipmentSlot(Text name) {
    public static final Registry<BattleEquipmentSlot> REGISTRY = FabricRegistryBuilder.createSimple(BattleEquipmentSlot.class, TurnBasedCombatExperiment.createId("equipment_slots")).buildAndRegister();
    //TODO Don't return null
    public static BattleEquipmentSlot HEAD_SLOT = new BattleEquipmentSlot(new LiteralText("head_slot"));
    public static BattleEquipmentSlot CHEST_SLOT = new BattleEquipmentSlot(new LiteralText("chest_slot"));
    public static BattleEquipmentSlot LEGS_SLOT = new BattleEquipmentSlot(new LiteralText("legs_slot"));
    public static BattleEquipmentSlot FEET_SLOT = new BattleEquipmentSlot(new LiteralText("feet_slot"));

    public static void init() {
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("head"), HEAD_SLOT);
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("chest"), CHEST_SLOT);
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("legs"), LEGS_SLOT);
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("feet"), FEET_SLOT);
    }
}
