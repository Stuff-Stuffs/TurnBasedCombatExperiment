package io.github.stuff_stuffs.tbcexequipment.client.render.model.part;

import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.tbcexequipment.client.material.MaterialPalette;
import io.github.stuff_stuffs.tbcexequipment.client.render.model.ModelUtil;
import io.github.stuff_stuffs.tbcexequipment.common.material.Material;
import io.github.stuff_stuffs.tbcexequipment.common.part.Part;
import io.github.stuff_stuffs.tbcexequipment.common.part.PartInstance;
import io.github.stuff_stuffs.tbcexutil.client.ClientUtil;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
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
    private final Map<Part<?>, Map<MaterialPalette.EntryType, Sprite>> sprites;
    private final Map<Pair<Material, Part<?>>, Mesh> cache = new Object2ReferenceOpenHashMap<>();

    public PartItemModel(Map<Part<?>, Map<MaterialPalette.EntryType, Sprite>> sprites) {
        this.sprites = sprites;
    }

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
            final Pair<Material, Part<?>> key = Pair.of(instance.getData().getMaterial(), instance.getPart());
            final Mesh mesh = cache.computeIfAbsent(key, k -> ModelUtil.buildMesh(k, sprites.get(instance.getPart())));
            context.meshConsumer().accept(mesh);
        }
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
