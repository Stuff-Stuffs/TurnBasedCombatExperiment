package io.github.stuff_stuffs.tbcexequipment.common.material;

import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.tag.RequiredTagList;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.Tag;

public final class MaterialTags {
    public static final RequiredTagList<Material> REQUIRED_TAG_LIST = RequiredTagListRegistry.register(Materials.REGISTRY_KEY, "tags/tbcex/materials");
    public static final Tag.Identified<Material> BLADE_MATERIALS = TagRegistry.create(TBCExEquipment.createId("blade_materials"), REQUIRED_TAG_LIST::getGroup);

    public static void init() {
    }

    private MaterialTags() {
    }
}
