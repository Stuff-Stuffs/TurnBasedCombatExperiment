package io.github.stuff_stuffs.tbcexequipment.client.render.model.part;

import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.tbcexequipment.client.material.MaterialPalette;
import io.github.stuff_stuffs.tbcexequipment.client.part.PartRenderInfo;
import io.github.stuff_stuffs.tbcexequipment.common.part.Part;
import io.github.stuff_stuffs.tbcexequipment.common.part.Parts;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class UnbakedPartItemModel implements UnbakedModel {
    @Override
    public Collection<Identifier> getModelDependencies() {
        return Collections.emptySet();
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(final Function<Identifier, UnbakedModel> unbakedModelGetter, final Set<Pair<String, String>> unresolvedTextureReferences) {
        final List<SpriteIdentifier> textures = new ArrayList<>();
        for (final Identifier id : Parts.REGISTRY.getIds()) {
            final PartRenderInfo info = PartRenderInfo.get(id);
            for (final MaterialPalette.EntryType type : MaterialPalette.EntryType.values()) {
                final Identifier texture = info.getTexture(type);
                textures.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, texture));
            }
        }
        return textures;
    }

    @Nullable
    @Override
    public BakedModel bake(final ModelLoader loader, final Function<SpriteIdentifier, Sprite> textureGetter, final ModelBakeSettings rotationContainer, final Identifier modelId) {
        final Map<Part<?>, Map<MaterialPalette.EntryType, Sprite>> sprites = new Reference2ObjectOpenHashMap<>();
        for (final Identifier id : Parts.REGISTRY.getIds()) {
            final PartRenderInfo info = PartRenderInfo.get(id);
            final Map<MaterialPalette.EntryType, Sprite> partSprites = new Reference2ObjectOpenHashMap<>();
            for (final MaterialPalette.EntryType type : MaterialPalette.EntryType.values()) {
                final Identifier texture = info.getTexture(type);
                final SpriteIdentifier spriteTexture = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, texture);
                partSprites.put(type, textureGetter.apply(spriteTexture));
            }
            sprites.put(Parts.REGISTRY.get(id), partSprites);
        }
        return new PartItemModel(sprites);
    }
}
