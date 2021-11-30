package io.github.stuff_stuffs.tbcexequipment.common.material;

import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.tag.Tag;

public final class MaterialTags {
    public static final TagFactory<Material> TAG_FACTORY = TagFactory.of(Materials.REGISTRY_KEY, "tags/tbcex/materials");
    public static final Tag.Identified<Material> BLADE_MATERIALS = TAG_FACTORY.create(TBCExEquipment.createId("blade_materials"));
    public static final Tag.Identified<Material> SIMPLE_POMMEL_MATERIALS = TAG_FACTORY.create(TBCExEquipment.createId("simple_pommel_materials"));
    public static final Tag.Identified<Material> HANDLE_MATERIALS = TAG_FACTORY.create(TBCExEquipment.createId("handle_materials"));
    public static final Tag.Identified<Material> SIMPLE_SWORD_GUARD_MATERIALS = TAG_FACTORY.create(TBCExEquipment.createId("simple_sword_guard_materials"));
    public static final Tag.Identified<Material> FANCY_SWORD_GUARD_MATERIALS = TAG_FACTORY.create(TBCExEquipment.createId("fancy_sword_guard_materials"));

    public static void init() {
    }

    private MaterialTags() {
    }
}
