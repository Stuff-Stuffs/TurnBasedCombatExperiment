package io.github.stuff_stuffs.tbcexequipment.common.item;

import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;

public final class Items {
    public static final ItemGroup PART_GROUP = FabricItemGroupBuilder.create(TBCExEquipment.createId("parts")).build();
    public static final PartInstanceItem PART_INSTANCE = new PartInstanceItem();

    public static void init() {
        Registry.register(Registry.ITEM, TBCExEquipment.createId("part_instance"), PART_INSTANCE);
    }

    private Items() {
    }
}
