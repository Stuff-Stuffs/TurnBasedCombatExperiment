package io.github.stuff_stuffs.tbcexequipment.common;

import io.github.stuff_stuffs.tbcexequipment.common.item.Items;
import io.github.stuff_stuffs.tbcexequipment.common.material.Materials;
import io.github.stuff_stuffs.tbcexequipment.common.part.Parts;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class TBCExEquipment implements ModInitializer {
    public static final String MOD_ID = "tbcexequipment";

    @Override
    public void onInitialize() {
        Parts.init();
        Items.init();
        Materials.init();
    }

    public static Identifier createId(final String path) {
        return new Identifier(MOD_ID, path);
    }
}
