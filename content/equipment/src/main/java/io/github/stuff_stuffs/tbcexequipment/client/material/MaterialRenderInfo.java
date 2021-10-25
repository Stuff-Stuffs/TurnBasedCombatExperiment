package io.github.stuff_stuffs.tbcexequipment.client.material;

import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import io.github.stuff_stuffs.tbcexutil.common.colour.Colour;
import io.github.stuff_stuffs.tbcexutil.common.colour.IntRgbColour;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.util.Identifier;

import java.util.Map;

public final class MaterialRenderInfo {
    private static final Map<Identifier, MaterialRenderInfo> RENDER_INFO_MAP = new Object2ReferenceOpenHashMap<>();
    private static final MaterialRenderInfo MISSING;
    private final MaterialPalette palette;

    private MaterialRenderInfo(final MaterialPalette palette) {
        this.palette = palette;
    }

    public MaterialPalette getPalette() {
        return palette;
    }

    public static MaterialRenderInfo get(final Identifier id) {
        return RENDER_INFO_MAP.getOrDefault(id, MISSING);
    }

    public static void register(final Identifier id, final MaterialPalette palette) {
        if (RENDER_INFO_MAP.put(id, new MaterialRenderInfo(palette)) != null) {
            throw new TBCExException("Material render info already present");
        }
    }

    static {
        final Colour black = new IntRgbColour(0, 0, 0);
        final Colour purple = new IntRgbColour(255, 0, 255);
        final MaterialPalette.Entry blackEntry = new MaterialPalette.Entry(black, false, 255);
        final MaterialPalette.Entry purpleEntry = new MaterialPalette.Entry(purple, false, 255);
        final MaterialPalette missingPalette = new MaterialPalette(purpleEntry, blackEntry, purpleEntry, blackEntry, purpleEntry, blackEntry, purpleEntry, blackEntry);
        MISSING = new MaterialRenderInfo(missingPalette);
    }
}
