package io.github.stuff_stuffs.tbcexequipment.client.part;

import io.github.stuff_stuffs.tbcexequipment.client.material.MaterialPalette;
import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import io.github.stuff_stuffs.tbcexutil.common.StringInterpolator;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.util.Identifier;

import java.util.Map;

public final class PartRenderInfo {
    private static final StringInterpolator ALREADY_PRESENT = new StringInterpolator("Part render info {} already present");
    private static final Map<Identifier, PartRenderInfo> RENDER_INFO_MAP = new Object2ReferenceOpenHashMap<>();
    private static final PartRenderInfo MISSING;
    private final Identifier textureDirectory;


    private PartRenderInfo(final Identifier textureDirectory) {
        this.textureDirectory = textureDirectory;
    }

    public Identifier getTexture(final MaterialPalette.EntryType type) {
        return type.findTexture(textureDirectory);
    }

    public static void register(final Identifier id, final Identifier textureDirectory) {
        if (RENDER_INFO_MAP.put(id, new PartRenderInfo(textureDirectory)) != null) {
            throw new TBCExException(ALREADY_PRESENT.interpolate(id));
        }
    }

    public static PartRenderInfo get(final Identifier id) {
        return RENDER_INFO_MAP.getOrDefault(id, MISSING);
    }

    static {
        MISSING = new PartRenderInfo(TBCExEquipment.createId("part/missing"));
    }
}
