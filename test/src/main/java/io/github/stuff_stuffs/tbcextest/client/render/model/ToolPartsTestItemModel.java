package io.github.stuff_stuffs.tbcextest.client.render.model;

import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.tbcexequipment.client.render.model.ModelUtil;
import io.github.stuff_stuffs.tbcexequipment.common.material.Material;
import io.github.stuff_stuffs.tbcexequipment.common.part.Part;
import io.github.stuff_stuffs.tbcexequipment.common.part.PartInstance;
import io.github.stuff_stuffs.tbcextest.common.item.ToolPartsTestItem;
import io.github.stuff_stuffs.tbcexutil.client.ClientUtil;
import io.github.stuff_stuffs.tbcexutil.client.DebugRenderers;
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
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class ToolPartsTestItemModel implements FabricBakedModel, BakedModel, UnbakedModel {
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
        final NbtElement parts = stack.getOrCreateNbt().get("parts");
        if (parts == null) {
            return;
        }
        final Optional<List<PartInstance>> result = ToolPartsTestItem.CODEC.parse(NbtOps.INSTANCE, parts).result();
        if (result.isEmpty()) {
            return;
        }
        final List<PartInstance> partList = result.get();
        final double stretchZInc = 0.001;
        double stretchZStart = 1 - partList.size() * stretchZInc;
        for (final PartInstance instance : partList) {
            final float factor = (float) stretchZStart;
            context.pushTransform(quad -> {
                for (int i = 0; i < 4; i++) {
                    quad.pos(i, (quad.x(i)-0.5f) * factor+0.5f, (quad.y(i)-0.5f) * factor+0.5f, (quad.z(i)-0.5f) * factor+0.5f);
                }
                return true;
            });
            final Mesh mesh = cache.computeIfAbsent(Pair.of(instance.getMaterial(), instance.getPart()), ModelUtil::buildMesh);
            context.meshConsumer().accept(mesh);
            context.popTransform();
            stretchZStart += stretchZInc;
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
