package io.github.stuff_stuffs.tbcexequipment.client.render.model;

import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.tbcexequipment.client.material.MaterialPalette;
import io.github.stuff_stuffs.tbcexequipment.client.material.MaterialRenderInfo;
import io.github.stuff_stuffs.tbcexequipment.client.part.PartRenderInfo;
import io.github.stuff_stuffs.tbcexequipment.common.material.Material;
import io.github.stuff_stuffs.tbcexequipment.common.material.Materials;
import io.github.stuff_stuffs.tbcexequipment.common.part.Part;
import io.github.stuff_stuffs.tbcexequipment.common.part.PartInstance;
import io.github.stuff_stuffs.tbcexequipment.common.part.Parts;
import io.github.stuff_stuffs.tbcexutil.client.ClientUtil;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import io.github.stuff_stuffs.tbcexutil.common.colour.Colour;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class PartItemModel implements BakedModel, FabricBakedModel, UnbakedModel {
    private final Map<Pair<Material, Part>, Mesh> cache = new Object2ReferenceOpenHashMap<>();

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(final BlockRenderView blockView, final BlockState state, final BlockPos pos, final Supplier<Random> randomSupplier, final RenderContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void emitItemQuads(final ItemStack stack, final Supplier<Random> randomSupplier, final RenderContext context) {
        final NbtCompound nbt = stack.getSubNbt("partInstance");
        if (nbt != null) {
            final PartInstance instance = PartInstance.CODEC.parse(NbtOps.INSTANCE, nbt).getOrThrow(false, s -> {
                throw new TBCExException(s);
            });
            final Pair<Material, Part> key = Pair.of(instance.getMaterial(), instance.getPart());
            final Mesh mesh = cache.computeIfAbsent(key, PartItemModel::build);
            context.meshConsumer().accept(mesh);
        }
    }

    private static Mesh build(final Pair<Material, Part> key) {
        final Function<Identifier, Sprite> atlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        final Part part = key.getSecond();
        final PartRenderInfo partRenderInfo = PartRenderInfo.get(Parts.REGISTRY.getId(part));
        int maxSize = 0;
        for (final MaterialPalette.EntryType type : MaterialPalette.EntryType.values()) {
            final Sprite sprite = atlas.apply(partRenderInfo.getTexture(type));
            if (sprite.getWidth() != sprite.getHeight()) {
                throw new TBCExException("Non square part mask!");
            }
            if ((sprite.getWidth() & sprite.getWidth() - 1) != 0) {
                throw new TBCExException("Non power of two mask!");
            }
            maxSize = Math.max(maxSize, sprite.getWidth());
        }
        final boolean[][] transparent = new boolean[maxSize][maxSize];
        final Material material = key.getFirst();
        final MaterialRenderInfo materialRenderInfo = MaterialRenderInfo.get(Materials.REGISTRY.getId(material));
        for (final MaterialPalette.EntryType type : MaterialPalette.EntryType.values()) {
            final Sprite sprite = atlas.apply(partRenderInfo.getTexture(type));
            final int width = sprite.getWidth();
            final int height = sprite.getHeight();
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    final boolean maskTranslucent = sprite.isPixelTransparent(0, i / (maxSize / width), j / (maxSize / height));
                    final boolean paletteTranslucent = materialRenderInfo.getPalette().isTranslucent(type);
                    if (!maskTranslucent && !paletteTranslucent) {
                        transparent[i][j] = true;
                    }
                }
            }
        }
        final MeshBuilder meshBuilder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
        final QuadEmitter quadEmitter = meshBuilder.getEmitter();
        for (final MaterialPalette.EntryType type : MaterialPalette.EntryType.values()) {
            final Sprite sprite = atlas.apply(partRenderInfo.getTexture(type));
            final MaterialPalette.Entry entry = materialRenderInfo.getPalette().getEntry(type);
            final Colour colour = entry.colour();
            final int alpha = entry.alpha();
            final boolean emissive = entry.emissive();
            final RenderMaterial renderMaterial = RendererAccess.INSTANCE.getRenderer().materialFinder().disableAo(0, true).emissive(0, emissive).blendMode(0, alpha == 255 ? BlendMode.SOLID : BlendMode.TRANSLUCENT).find();
            final int packedColour = colour.pack(alpha);
            quadEmitter.material(renderMaterial).square(Direction.NORTH, 0, 0, 1, 1, 0.5f - 1 / 32f).spriteColor(0, packedColour, packedColour, packedColour, packedColour).spriteUnitSquare(0).spriteBake(0, sprite, MutableQuadView.BAKE_NORMALIZED | MutableQuadView.BAKE_FLIP_U).emit();
            quadEmitter.material(renderMaterial).square(Direction.SOUTH, 0, 0, 1, 1, 0.5f - 1 / 32f).spriteColor(0, packedColour, packedColour, packedColour, packedColour).spriteUnitSquare(0).spriteBake(0, sprite, MutableQuadView.BAKE_NORMALIZED).emit();
            final int factor = maxSize / sprite.getWidth();
            for (int i = 0; i < maxSize; i++) {
                for (int j = 0; j < maxSize; j++) {
                    if (!transparent[i][j] && (i + 1 == maxSize || transparent[i + 1][j]) && sprite.isPixelTransparent(0, i / factor, j / factor) && (i + 1 == maxSize || !sprite.isPixelTransparent(0, (i) / factor + 1, j / factor))) {
                        quadEmitter.material(renderMaterial);
                        quadEmitter.square(Direction.WEST, 0.5f - 1 / 32f, (maxSize - j - 1) / (float) maxSize, 0.5f + 1 / 32f, (maxSize - j) / (float) maxSize, (i + 1) / (float) maxSize);
                        quadEmitter.spriteColor(0, packedColour, packedColour, packedColour, packedColour);
                        quadEmitter.sprite(0, 0, (i + 1f) / (float) maxSize, j / (float) maxSize);
                        quadEmitter.sprite(1, 0, (i + 1f) / (float) maxSize, (j + 1) / (float) maxSize);
                        quadEmitter.sprite(2, 0, (i + 1.01f) / (float) maxSize, (j + 1) / (float) maxSize);
                        quadEmitter.sprite(3, 0, (i + 1.01f) / (float) maxSize, j / (float) maxSize);
                        quadEmitter.spriteBake(0, sprite, MutableQuadView.BAKE_NORMALIZED).emit();
                    }
                    if (transparent[i][j] && (i == maxSize - 1 || !transparent[i + 1][j]) && !sprite.isPixelTransparent(0, i / factor, j / factor) && (i == maxSize - 1 || sprite.isPixelTransparent(0, (i) / factor + 1, j / factor))) {
                        quadEmitter.material(renderMaterial);
                        quadEmitter.square(Direction.EAST, 0.5f - 1 / 32f, (maxSize - j - 1) / (float) maxSize, 0.5f + 1 / 32f, (maxSize - j) / (float) maxSize, (maxSize - i - 1) / (float) maxSize);
                        quadEmitter.spriteColor(0, packedColour, packedColour, packedColour, packedColour);
                        quadEmitter.sprite(0, 0, (i + 0f) / (float) maxSize, j / (float) maxSize);
                        quadEmitter.sprite(1, 0, (i + 0f) / (float) maxSize, (j + 1) / (float) maxSize);
                        quadEmitter.sprite(2, 0, (i + 0.01f) / (float) maxSize, (j + 1) / (float) maxSize);
                        quadEmitter.sprite(3, 0, (i + 0.01f) / (float) maxSize, j / (float) maxSize);
                        quadEmitter.spriteBake(0, sprite, MutableQuadView.BAKE_NORMALIZED).emit();
                    }
                    if ((j == 0 || !transparent[i][j - 1]) && transparent[i][j] && (j == 0 || sprite.isPixelTransparent(0, i / factor, j / factor - 1)) && !sprite.isPixelTransparent(0, i / factor, j / factor)) {
                        quadEmitter.material(renderMaterial);
                        quadEmitter.square(Direction.UP, i / (float) maxSize, 0.5f - 1 / 32f, (i + 1) / (float) maxSize, 0.5f + 1 / 32f, j / (float) maxSize);
                        quadEmitter.spriteColor(0, packedColour, packedColour, packedColour, packedColour);
                        quadEmitter.sprite(0, 0, (i) / (float) maxSize, (j + 0f) / (float) maxSize);
                        quadEmitter.sprite(1, 0, (i) / (float) maxSize, (j + 0.01f) / (float) maxSize);
                        quadEmitter.sprite(2, 0, (i + 1) / (float) maxSize, (j + 0.01f) / (float) maxSize);
                        quadEmitter.sprite(3, 0, (i + 1) / (float) maxSize, (j + 0f) / (float) maxSize);
                        quadEmitter.spriteBake(0, sprite, MutableQuadView.BAKE_NORMALIZED).emit();
                    }
                    if ((j + 1 == maxSize || !transparent[i][j + 1]) && transparent[i][j] && (j + 1 == maxSize || sprite.isPixelTransparent(0, i / factor, j / factor + 1)) && !sprite.isPixelTransparent(0, i / factor, j / factor)) {
                        quadEmitter.material(renderMaterial);
                        quadEmitter.square(Direction.DOWN, i / (float) maxSize, 0.5f - 1 / 32f, (i + 1) / (float) maxSize, 0.5f + 1 / 32f, (maxSize - j - 1) / (float) maxSize);
                        quadEmitter.spriteColor(0, packedColour, packedColour, packedColour, packedColour);
                        quadEmitter.sprite(0, 0, (i) / (float) maxSize, (j + 0f) / (float) maxSize);
                        quadEmitter.sprite(1, 0, (i) / (float) maxSize, (j + 0.01f) / (float) maxSize);
                        quadEmitter.sprite(2, 0, (i + 1) / (float) maxSize, (j + 0.01f) / (float) maxSize);
                        quadEmitter.sprite(3, 0, (i + 1) / (float) maxSize, (j + 0f) / (float) maxSize);
                        quadEmitter.spriteBake(0, sprite, MutableQuadView.BAKE_NORMALIZED).emit();
                    }
                }
            }
        }
        return meshBuilder.build();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable final BlockState state, @Nullable final Direction face, final Random random) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean hasDepth() {
        return false;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getParticleSprite() {
        return null;
    }

    @Override
    public ModelTransformation getTransformation() {
        return ClientUtil.ITEM_TRANSFORMATION;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        return Collections.emptySet();
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(final Function<Identifier, UnbakedModel> unbakedModelGetter, final Set<Pair<String, String>> unresolvedTextureReferences) {
        return Collections.emptySet();
    }

    @Nullable
    @Override
    public BakedModel bake(final ModelLoader loader, final Function<SpriteIdentifier, Sprite> textureGetter, final ModelBakeSettings rotationContainer, final Identifier modelId) {
        return this;
    }
}
