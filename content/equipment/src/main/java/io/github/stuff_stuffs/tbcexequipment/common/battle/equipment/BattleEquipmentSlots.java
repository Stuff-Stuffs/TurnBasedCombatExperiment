package io.github.stuff_stuffs.tbcexequipment.common.battle.equipment;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import net.minecraft.text.LiteralText;
import net.minecraft.util.registry.Registry;

public final class BattleEquipmentSlots {
    public static final BattleEquipmentSlot HEAD_SLOT = new BattleEquipmentSlot(new LiteralText("head_slot"));
    public static final BattleEquipmentSlot CHEST_SLOT = new BattleEquipmentSlot(new LiteralText("chest_slot"));
    public static final BattleEquipmentSlot LEGS_SLOT = new BattleEquipmentSlot(new LiteralText("legs_slot"));
    public static final BattleEquipmentSlot FEET_SLOT = new BattleEquipmentSlot(new LiteralText("feet_slot"));
    public static final BattleEquipmentSlot MAIN_HAND_SLOT = new BattleEquipmentSlot(new LiteralText("main_hand_slot"));
    public static final BattleEquipmentSlot OFF_HAND_SLOT = new BattleEquipmentSlot(new LiteralText("off_hand_slot"));

    public static void init() {
        Registry.register(BattleEquipmentSlot.REGISTRY, TBCExEquipment.createId("head"), HEAD_SLOT);
        Registry.register(BattleEquipmentSlot.REGISTRY, TBCExEquipment.createId("chest"), CHEST_SLOT);
        Registry.register(BattleEquipmentSlot.REGISTRY, TBCExEquipment.createId("legs"), LEGS_SLOT);
        Registry.register(BattleEquipmentSlot.REGISTRY, TBCExEquipment.createId("feet"), FEET_SLOT);
        Registry.register(BattleEquipmentSlot.REGISTRY, TBCExEquipment.createId("main_hand"), MAIN_HAND_SLOT);
        Registry.register(BattleEquipmentSlot.REGISTRY, TBCExEquipment.createId("off_hand"), OFF_HAND_SLOT);
    }

    private BattleEquipmentSlots() {
    }
}
