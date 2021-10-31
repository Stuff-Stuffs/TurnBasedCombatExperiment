package io.github.stuff_stuffs.tbcexequipment.client.render.model.equipment;

import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.tbcexequipment.client.render.model.ModelUtil;
import io.github.stuff_stuffs.tbcexequipment.client.render.model.Models;
import io.github.stuff_stuffs.tbcexequipment.client.render.model.part.PartPlacementInfo;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.EquipmentInstance;
import io.github.stuff_stuffs.tbcexequipment.common.item.EquipmentInstanceItem;
import io.github.stuff_stuffs.tbcexequipment.common.part.PartInstance;
import io.github.stuff_stuffs.tbcexequipment.common.part.Parts;
import io.github.stuff_stuffs.tbcexutil.client.ClientUtil;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2ReferenceLinkedOpenHashMap;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

public class EquipmentItemModel implements BakedModel, FabricBakedModel {
    private final Object2ReferenceLinkedOpenHashMap<ItemStack, Mesh> cache = new Object2ReferenceLinkedOpenHashMap<>(512);

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
        final NbtElement nbt = stack.getSubNbt(EquipmentInstanceItem.INSTANCE_KEY);
        if (nbt == null) {
            return;
        }
        Mesh mesh = cache.getAndMoveToFirst(stack);
        if (mesh == null) {
            mesh = buildMesh(stack);
            cache.putAndMoveToFirst(stack, mesh);
            if (cache.size() > 511) {
                cache.removeLast();
            }
        }
        context.meshConsumer().accept(mesh);
    }

    private static Mesh buildMesh(final ItemStack stack) {
        final NbtElement nbt = stack.getOrCreateNbt().get(EquipmentInstanceItem.INSTANCE_KEY);
        if (nbt == null) {
            throw new TBCExException();
        }
        final Optional<EquipmentInstance> optional = EquipmentInstance.CODEC.parse(NbtOps.INSTANCE, nbt).result();
        if (optional.isEmpty()) {
            //TODO load error model
            return ClientUtil.LAZY_EMPTY_MESH.get();
        }
        final EquipmentInstance equipment = optional.get();
        final List<PartInstance> parts = equipment.getData().getParts();
        final IntList indices = equipment.getType().generateIndices(parts);
        List<Mesh> toMerge = new ArrayList<>();
        for (int i = 0; i < parts.size(); i++) {
            final PartInstance part = parts.get(i);
            final Identifier pose = equipment.getType().getPose(indices.getInt(i));
            Mesh m = ModelUtil.buildMesh(Pair.of(part.getMaterial(), part.getPart()));
            final PartPlacementInfo info = Models.getPlacementInfo(pose, Parts.REGISTRY.getId(part.getPart()));
            toMerge.add(ClientUtil.transform(m, info));
        }
        return ClientUtil.merge(toMerge.toArray(new Mesh[0]));
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
        return ModelTransformation.NONE;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }
}
