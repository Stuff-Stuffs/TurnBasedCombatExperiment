package io.github.stuff_stuffs.tbcexequipment.client;

import io.github.stuff_stuffs.tbcexequipment.client.material.MaterialPalette;
import io.github.stuff_stuffs.tbcexequipment.client.material.MaterialRenderInfo;
import io.github.stuff_stuffs.tbcexequipment.client.part.PartRenderInfo;
import io.github.stuff_stuffs.tbcexequipment.client.render.model.PartItemModel;
import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import io.github.stuff_stuffs.tbcexequipment.common.material.Materials;
import io.github.stuff_stuffs.tbcexequipment.common.part.Parts;
import io.github.stuff_stuffs.tbcexutil.client.DebugRenderers;
import io.github.stuff_stuffs.tbcexutil.common.colour.IntRgbColour;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

public class TBCExEquipmentClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
            for (final Identifier id : Parts.REGISTRY.getIds()) {
                final PartRenderInfo info = PartRenderInfo.get(id);
                for (final MaterialPalette.EntryType type : MaterialPalette.EntryType.values()) {
                    final Identifier texture = info.getTexture(type);
                    registry.register(texture);
                }
            }
        });
        final Identifier id = TBCExEquipment.createId("item/part_instance");
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(resourceManager -> (resourceId, context) -> {
            if (resourceId.equals(id)) {
                return new PartItemModel();
            }
            return null;
        });
        MaterialRenderInfo.register(Materials.REGISTRY.getId(Materials.WOOD), new MaterialPalette(
                new MaterialPalette.Entry(new IntRgbColour(0xFF493615), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFF281e0b), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFFFFFFFF), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFF896727), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFF684e1e), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFF684e1e), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFF281e0b), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFF493615), false, 255)
        ));
        MaterialRenderInfo.register(Materials.REGISTRY.getId(Materials.GOLD), new MaterialPalette(
                new MaterialPalette.Entry(new IntRgbColour(0xFFb26411), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFF752802), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFFFFFFFF), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFFFFFDE0), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFFFDF55F), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFFFAD64A), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFFE9B115), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFFDC9613), false, 255)
        ));
        MaterialRenderInfo.register(Materials.REGISTRY.getId(Materials.STONE), new MaterialPalette(
                new MaterialPalette.Entry(new IntRgbColour(0xFF494949), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFF212121), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFFFFFFFF), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFFD8D8D8), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFFC6C6C6), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFF969696), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFF6B6B6B), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFF292929), false, 255)
        ));
        MaterialRenderInfo.register(Materials.REGISTRY.getId(Materials.IRON), new MaterialPalette(
                new MaterialPalette.Entry(new IntRgbColour(0xFF5e5e5e), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFF353535), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFFFFFFFF), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFFD8D8D8), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFFA8A8A8), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFF828282), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFF727272), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFF585858), false, 255)
        ));
        MaterialRenderInfo.register(Materials.REGISTRY.getId(Materials.DIAMOND), new MaterialPalette(
                new MaterialPalette.Entry(new IntRgbColour(0xFF0C3F36), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFF082520), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFFFFFFFF), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFFA1FBE8), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFF4AEDD9), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFF20C5B5), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFF1AAAA7), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFF145E53), false, 255)
        ));
        PartRenderInfo.register(Parts.REGISTRY.getId(Parts.HANDLE_PART), TBCExEquipment.createId("part/handle"));
        PartRenderInfo.register(Parts.REGISTRY.getId(Parts.AXE_HEAD_PART), TBCExEquipment.createId("part/axe_head"));
        PartRenderInfo.register(Parts.REGISTRY.getId(Parts.SWORD_BLADE_PART), TBCExEquipment.createId("part/sword_blade"));
        PartRenderInfo.register(Parts.REGISTRY.getId(Parts.SIMPLE_SWORD_GUARD_PART), TBCExEquipment.createId("part/simple_guard"));
        PartRenderInfo.register(Parts.REGISTRY.getId(Parts.FANCY_SWORD_GUARD), TBCExEquipment.createId("part/fancy_guard"));
    }
}
