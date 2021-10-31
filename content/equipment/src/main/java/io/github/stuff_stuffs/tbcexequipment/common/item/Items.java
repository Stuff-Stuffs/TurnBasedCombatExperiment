package io.github.stuff_stuffs.tbcexequipment.common.item;

import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;

public final class Items {
    public static final ItemGroup PART_GROUP = FabricItemGroupBuilder.create(TBCExEquipment.createId("parts")).build();
    public static final PartInstanceItem PART_INSTANCE = new PartInstanceItem();
    public static final EquipmentInstanceItem EQUIPMENT_INSTANCE = new EquipmentInstanceItem(new FabricItemSettings().maxCount(1));

    public static void init() {
        Registry.register(Registry.ITEM, TBCExEquipment.createId("part_instance"), PART_INSTANCE);
        Registry.register(Registry.ITEM, TBCExEquipment.createId("equipment_instance"), EQUIPMENT_INSTANCE);
    }

    private Items() {
    }
}
