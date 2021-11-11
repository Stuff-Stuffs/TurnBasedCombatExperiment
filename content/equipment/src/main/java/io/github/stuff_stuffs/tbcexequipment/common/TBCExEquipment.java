package io.github.stuff_stuffs.tbcexequipment.common;

import io.github.stuff_stuffs.tbcexcore.common.TBCExCore;
import io.github.stuff_stuffs.tbcexequipment.common.battle.equipment.BattleEquipmentSlots;
import io.github.stuff_stuffs.tbcexequipment.common.battle.item.BattleEquipmentItemTypes;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.EquipmentActions;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.EquipmentTypes;
import io.github.stuff_stuffs.tbcexequipment.common.item.Items;
import io.github.stuff_stuffs.tbcexequipment.common.material.MaterialTags;
import io.github.stuff_stuffs.tbcexequipment.common.material.Materials;
import io.github.stuff_stuffs.tbcexequipment.common.material.stats.MaterialStatManager;
import io.github.stuff_stuffs.tbcexequipment.common.material.stats.MaterialStats;
import io.github.stuff_stuffs.tbcexequipment.common.part.PartTags;
import io.github.stuff_stuffs.tbcexequipment.common.part.Parts;
import io.github.stuff_stuffs.tbcexequipment.common.part.stats.PartStatManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class TBCExEquipment implements ModInitializer {
    public static final String MOD_ID = "tbcexequipment";
    public static final MaterialStatManager MATERIAL_STAT_MANAGER = new MaterialStatManager();
    public static final PartStatManager PART_STAT_MANAGER = new PartStatManager();

    @Override
    public void onInitialize() {
        Parts.init();
        Items.init();
        Materials.init();
        MaterialTags.init();
        PartTags.init();
        EquipmentTypes.init();
        BattleEquipmentSlots.init();
        BattleEquipmentItemTypes.init();
        EquipmentActions.init();

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(MATERIAL_STAT_MANAGER);
        ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> MATERIAL_STAT_MANAGER.sync(sender));
        ServerLoginNetworking.registerGlobalReceiver(MaterialStatManager.CHANNEL_ID, (server, handler, understood, buf, synchronizer, responseSender) -> {
        });
        MaterialStats.init();

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
