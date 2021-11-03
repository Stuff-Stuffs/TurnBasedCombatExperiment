package io.github.stuff_stuffs.tbcexequipment.client;

import io.github.stuff_stuffs.tbcexequipment.client.material.MaterialPalette;
import io.github.stuff_stuffs.tbcexequipment.client.material.MaterialRenderInfo;
import io.github.stuff_stuffs.tbcexequipment.client.part.PartRenderInfo;
import io.github.stuff_stuffs.tbcexequipment.client.render.model.Models;
import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import io.github.stuff_stuffs.tbcexequipment.common.material.Materials;
import io.github.stuff_stuffs.tbcexequipment.common.material.stats.MaterialStatManager;
import io.github.stuff_stuffs.tbcexequipment.common.part.Parts;
import io.github.stuff_stuffs.tbcexutil.common.colour.IntRgbColour;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

import java.util.concurrent.CompletableFuture;

public class TBCExEquipmentClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Models.init();
        ClientLoginNetworking.registerGlobalReceiver(MaterialStatManager.CHANNEL_ID, (client, handler, buf, listenerAdder) -> {
            if (!handler.getConnection().isLocal()) {
                TBCExEquipment.MATERIAL_STAT_MANAGER.receive(buf);
            }
            return CompletableFuture.completedFuture(PacketByteBufs.create());
        });
        //TODO move these to json
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
        MaterialRenderInfo.register(Materials.REGISTRY.getId(Materials.COPPER), new MaterialPalette(
                new MaterialPalette.Entry(new IntRgbColour(0xFF9C4529), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFF6d3421), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFFfbc3b6), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFFfc9982), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFFe77c56), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFFc77c56), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFFc15a36), false, 255),
                new MaterialPalette.Entry(new IntRgbColour(0xFF9c4e31), false, 255)
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
        PartRenderInfo.register(Parts.REGISTRY.getId(Parts.SWORD_BLADE_PART), TBCExEquipment.createId("part/sword_blade"));
        PartRenderInfo.register(Parts.REGISTRY.getId(Parts.SIMPLE_SWORD_GUARD_PART), TBCExEquipment.createId("part/simple_guard"));
        PartRenderInfo.register(Parts.REGISTRY.getId(Parts.FANCY_SWORD_GUARD), TBCExEquipment.createId("part/fancy_guard"));
        PartRenderInfo.register(Parts.REGISTRY.getId(Parts.SIMPLE_POMMEL), TBCExEquipment.createId("part/simple_pommel"));
    }
}
