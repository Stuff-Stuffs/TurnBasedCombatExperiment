package io.github.stuff_stuffs.tbcexequipment.common.part;

import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.tag.RequiredTagList;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.Tag;

public final class PartTags {
    public static final RequiredTagList<Part> REQUIRED_TAG_LIST = RequiredTagListRegistry.register(Parts.REGISTRY_KEY, "tags/tbcex/part");
    public static final Tag.Identified<Part> SWORD_GUARDS = TagRegistry.create(TBCExEquipment.createId("sword_guards"), REQUIRED_TAG_LIST::getGroup);

    public static void init() {
    }

    private PartTags() {
    }
}
