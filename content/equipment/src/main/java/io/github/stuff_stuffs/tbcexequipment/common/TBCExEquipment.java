package io.github.stuff_stuffs.tbcexequipment.common;

import io.github.stuff_stuffs.tbcexcore.common.TBCExCore;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemType;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentType;
import io.github.stuff_stuffs.tbcexequipment.common.battle.equipment.BattleEquipmentSlots;
import io.github.stuff_stuffs.tbcexequipment.common.battle.equipment.EquipmentBattleEquipment;
import io.github.stuff_stuffs.tbcexequipment.common.battle.item.ParticipantEquipmentInstanceItem;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.EquipmentTypes;
import io.github.stuff_stuffs.tbcexequipment.common.item.EquipmentInstanceItem;
import io.github.stuff_stuffs.tbcexequipment.common.item.Items;
import io.github.stuff_stuffs.tbcexequipment.common.material.MaterialTags;
import io.github.stuff_stuffs.tbcexequipment.common.material.Materials;
import io.github.stuff_stuffs.tbcexequipment.common.part.PartTags;
import io.github.stuff_stuffs.tbcexequipment.common.part.Parts;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class TBCExEquipment implements ModInitializer {
    public static final String MOD_ID = "tbcexequipment";
    public static final BattleParticipantItemType EQUIPMENT_INSTANCE_ITEM_TYPE = new BattleParticipantItemType(ParticipantEquipmentInstanceItem.CODEC, (i, j) -> false, (i, j) -> {
        throw new UnsupportedOperationException();
    }, itemStack -> {
        final ItemStack stack = new ItemStack(Items.EQUIPMENT_INSTANCE, itemStack.getCount());
        stack.setSubNbt(EquipmentInstanceItem.INSTANCE_KEY, ((ParticipantEquipmentInstanceItem) itemStack.getItem()).toNbt());
        return List.of(stack);
    });
    public static final BattleEquipmentType EQUIPMENT_BATTLE_EQUIPMENT_TYPE = new BattleEquipmentType(new LiteralText("Equipment"), EquipmentBattleEquipment.CODEC);

    @Override
    public void onInitialize() {
        Parts.init();
        Items.init();
        Materials.init();
        MaterialTags.init();
        PartTags.init();
        EquipmentTypes.init();
        BattleEquipmentSlots.init();
        Registry.register(BattleEquipmentType.REGISTRY, TBCExEquipment.createId("equipment"), EQUIPMENT_BATTLE_EQUIPMENT_TYPE);

        TBCExCore.registerPlayerExtractor(BattleEquipmentSlots.HEAD_SLOT, player -> player.getEquippedStack(EquipmentSlot.HEAD));
        TBCExCore.registerPlayerExtractor(BattleEquipmentSlots.CHEST_SLOT, player -> player.getEquippedStack(EquipmentSlot.CHEST));
        TBCExCore.registerPlayerExtractor(BattleEquipmentSlots.LEGS_SLOT, player -> player.getEquippedStack(EquipmentSlot.LEGS));
        TBCExCore.registerPlayerExtractor(BattleEquipmentSlots.FEET_SLOT, player -> player.getEquippedStack(EquipmentSlot.FEET));
        TBCExCore.registerPlayerExtractor(BattleEquipmentSlots.MAIN_HAND_SLOT, player -> player.getEquippedStack(EquipmentSlot.MAINHAND));
        TBCExCore.registerPlayerExtractor(BattleEquipmentSlots.OFF_HAND_SLOT, player -> player.getEquippedStack(EquipmentSlot.OFFHAND));
    }

    public static Identifier createId(final String path) {
        return new Identifier(MOD_ID, path);
    }
}
