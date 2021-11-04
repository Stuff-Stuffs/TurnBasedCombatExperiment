package io.github.stuff_stuffs.tbcexequipment.client.part;

import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.tbcexequipment.client.material.MaterialPalette;
import io.github.stuff_stuffs.tbcexequipment.client.render.model.ModelUtil;
import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import io.github.stuff_stuffs.tbcexequipment.common.part.PartInstance;
import io.github.stuff_stuffs.tbcexutil.common.StringInterpolator;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

public final class PartRenderInfo {
    private static final StringInterpolator ALREADY_PRESENT = new StringInterpolator("Part render info {} already present");
    private static final Map<Identifier, PartRenderInfo> RENDER_INFO_MAP = new Object2ReferenceOpenHashMap<>();
    private static final PartRenderInfo MISSING;
    private final MeshCache meshCache;

    public PartRenderInfo(final MeshCache meshCache) {
        this.meshCache = meshCache;
    }

    public Mesh build(final PartInstance instance) {
        return meshCache.getMesh(instance);
    }

    public static void initialize(final Function<SpriteIdentifier, Sprite> spriteGetter) {
        for (final PartRenderInfo info : RENDER_INFO_MAP.values()) {
            info.meshCache.initialize(spriteGetter);
        }
    }

    public static Collection<SpriteIdentifier> getTextureDependencies() {
        final Collection<SpriteIdentifier> identifiers = new ArrayList<>();
        for (final PartRenderInfo info : RENDER_INFO_MAP.values()) {
            identifiers.addAll(info.meshCache.getTextureDependencies());
        }
        return identifiers;
    }

    public static void register(final Identifier id, final MeshCache meshCache) {
        if (RENDER_INFO_MAP.put(id, new PartRenderInfo(meshCache)) != null) {
            throw new TBCExException(ALREADY_PRESENT.interpolate(id));
        }
    }

    public static MeshCache createBasicMesher(final Identifier textureDirectory) {
        return new MeshCache() {
            private final Map<MaterialPalette.EntryType, Sprite> spriteCache = new EnumMap<>(MaterialPalette.EntryType.class);

            @Override
            public Collection<SpriteIdentifier> getTextureDependencies() {
                final Collection<SpriteIdentifier> identifiers = new ArrayList<>(MaterialPalette.EntryType.values().length);
                for (final MaterialPalette.EntryType type : MaterialPalette.EntryType.values()) {
                    final Identifier texture = type.findTexture(textureDirectory);
                    identifiers.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, texture));
                }
                return identifiers;
            }

            @Override
            public void initialize(final Function<SpriteIdentifier, Sprite> spriteGetter) {
                for (final MaterialPalette.EntryType type : MaterialPalette.EntryType.values()) {
                    final Identifier texture = type.findTexture(textureDirectory);
                    spriteCache.put(type, spriteGetter.apply(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, texture)));
                }
            }

            @Override
            public Mesh getMesh(final PartInstance part) {
                return ModelUtil.buildMesh(Pair.of(part.getData().getMaterial(), part.getPart()), spriteCache::get);
            }
        };
    }

    public static PartRenderInfo get(final Identifier id) {
        return RENDER_INFO_MAP.getOrDefault(id, MISSING);
    }

    public interface MeshCache {
        Collection<SpriteIdentifier> getTextureDependencies();

        void initialize(Function<SpriteIdentifier, Sprite> spriteGetter);

        Mesh getMesh(PartInstance part);
    }

    static {
        MISSING = new PartRenderInfo(createBasicMesher(TBCExEquipment.createId("part/missing")));
    }
}
