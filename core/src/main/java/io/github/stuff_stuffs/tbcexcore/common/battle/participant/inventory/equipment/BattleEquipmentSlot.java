package io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment;

import io.github.stuff_stuffs.tbcexcore.common.TBCExCore;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

public record BattleEquipmentSlot(Text name) {
    public static final Registry<BattleEquipmentSlot> REGISTRY = FabricRegistryBuilder.createSimple(BattleEquipmentSlot.class, TBCExCore.createId("equipment_slots")).buildAndRegister();
    public static final BattleEquipmentSlot HEAD_SLOT = new BattleEquipmentSlot(new LiteralText("head_slot"));
    public static final BattleEquipmentSlot CHEST_SLOT = new BattleEquipmentSlot(new LiteralText("chest_slot"));
    public static final BattleEquipmentSlot LEGS_SLOT = new BattleEquipmentSlot(new LiteralText("legs_slot"));
    public static final BattleEquipmentSlot FEET_SLOT = new BattleEquipmentSlot(new LiteralText("feet_slot"));
    public static final BattleEquipmentSlot MAIN_HAND_SLOT = new BattleEquipmentSlot(new LiteralText("main_hand_slot"));
    public static final BattleEquipmentSlot OFF_HAND_SLOT = new BattleEquipmentSlot(new LiteralText("off_hand_slot"));

    public static void init() {
        Registry.register(REGISTRY, TBCExCore.createId("head"), HEAD_SLOT);
        Registry.register(REGISTRY, TBCExCore.createId("chest"), CHEST_SLOT);
        Registry.register(REGISTRY, TBCExCore.createId("legs"), LEGS_SLOT);
        Registry.register(REGISTRY, TBCExCore.createId("feet"), FEET_SLOT);
        Registry.register(REGISTRY, TBCExCore.createId("main_hand"), MAIN_HAND_SLOT);
        Registry.register(REGISTRY, TBCExCore.createId("off_hand"), OFF_HAND_SLOT);
    }
}
