package io.github.stuff_stuffs.tbcexcore.common.util;

import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.HashCommon;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RegistryWorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

public final class WorldShapeCache {
    private final BlockView world;
    private final @Nullable Entity entity;
    private final Supplier<Collection<Entity>> entities;
    private final ShapeContext context;
    private final int size;
    private final BlockPos.Mutable mutable;
    private final long[] hashes;
    private final BlockState[] states;
    private final VoxelShape[] shapes;

    public WorldShapeCache(final RegistryWorldView world, final @Nullable Entity entity, final Box entitySearchBounds, final int size) {
        this.world = world;
        this.entity = entity;
        if (entity != null) {
            context = ShapeContext.of(entity);
        } else {
            context = ShapeContext.absent();
        }
        this.size = size;
        mutable = new BlockPos.Mutable();
        hashes = new long[size];
        states = new BlockState[size];
        shapes = new VoxelShape[size];
        entities = Suppliers.memoize(() -> Collections.unmodifiableCollection(world.getOtherEntities(entity, entitySearchBounds)));
    }

    private static long hash(final BlockPos pos) {
        return HashCommon.mix(pos.asLong());
    }

    public @Nullable Entity getEntity() {
        return entity;
    }

    public Collection<Entity> getEntities() {
        return entities.get();
    }

    public BlockState getState(final BlockPos pos) {
        final long hash = hash(pos);
        final int index = Math.abs((int) hash) % size;
        lookup(pos, hash, index);
        return states[index];
    }

    public VoxelShape getShape(final BlockPos pos) {
        final long hash = hash(pos);
        final int index = Math.abs((int) hash) % size;
        lookup(pos, hash, index);
        return shapes[index];
    }

    public VoxelShape getShape(final int x, final int y, final int z) {
        final long hash = HashCommon.mix(BlockPos.asLong(x, y, z));
        final int index = Math.abs((int) hash) % size;
        lookup(x, y, z, hash, index);
        return shapes[index];
    }

    private void lookup(final BlockPos pos, final long hash, final int index) {
        if (hashes[index] != hash) {
            hashes[index] = hash;
            final BlockState state = world.getBlockState(pos);
            states[index] = state;
            shapes[index] = state.getCollisionShape(world, pos, context);
        }
    }

    private void lookup(final int x, final int y, final int z, final long hash, final int index) {
        if (hashes[index] != hash) {
            hashes[index] = hash;
            final BlockState state = world.getBlockState(mutable.set(x, y, z));
            states[index] = state;
            shapes[index] = state.getCollisionShape(world, mutable, context);
        }
    }
}
