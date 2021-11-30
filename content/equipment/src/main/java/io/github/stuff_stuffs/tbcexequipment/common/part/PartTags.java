package io.github.stuff_stuffs.tbcexequipment.common.part;

import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.tag.Tag;

public final class PartTags {
    public static final TagFactory<Part<?>> TAG_FACTORY = TagFactory.of(Parts.REGISTRY_KEY, "tags/tbcex/parts");
    public static final Tag.Identified<Part<?>> SWORD_BLADES = TAG_FACTORY.create(TBCExEquipment.createId("sword_blades"));
    public static final Tag.Identified<Part<?>> HANDLES = TAG_FACTORY.create(TBCExEquipment.createId("handles"));
    public static final Tag.Identified<Part<?>> SWORD_GUARDS = TAG_FACTORY.create(TBCExEquipment.createId("sword_guards"));
    public static final Tag.Identified<Part<?>> SWORD_POMMELS = TAG_FACTORY.create(TBCExEquipment.createId("sword_pommels"));

    public static void init() {
    }

    private PartTags() {
    }
}
