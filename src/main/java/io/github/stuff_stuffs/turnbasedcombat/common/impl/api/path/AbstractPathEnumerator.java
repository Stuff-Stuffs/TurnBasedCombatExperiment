package io.github.stuff_stuffs.turnbasedcombat.common.impl.api.path;

import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleBounds;
import io.github.stuff_stuffs.turnbasedcombat.common.api.path.PathEnumerator;
import io.github.stuff_stuffs.turnbasedcombat.common.api.path.PathNode;
import io.github.stuff_stuffs.turnbasedcombat.common.util.WorldShapeCache;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import net.minecraft.entity.Entity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.RegistryWorldView;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractPathEnumerator implements PathEnumerator {
    @Override
    public Map<BlockPos, PathNode> getPaths(final RegistryWorldView blockView, final BlockPos startPos, final Entity entity, final BattleBounds bounds, final int maxLength) {
        if (!bounds.isIn(startPos)) {
            throw new RuntimeException();
        }
        final WorldShapeCache cache = new WorldShapeCache(blockView, entity, bounds.getBox(), 1024);
        final Map<BlockPos, PathNode> map = new Object2ReferenceOpenHashMap<>();
        final ObjectArrayFIFOQueue<PathNode> positions = new ObjectArrayFIFOQueue<>(maxLength * 4);
        positions.enqueue(new PathNode(startPos, 0, null));
        while (!positions.isEmpty()) {
            final PathNode prev = positions.dequeue();
            map.put(prev.getPos(), prev);
            final Collection<PathNode> adjacent = getAdjacent(prev, cache, bounds);
            for (final PathNode pathNode : adjacent) {
                if (pathNode.getDistance() <= maxLength && !map.containsKey(pathNode.getPos())) {
                    positions.enqueue(pathNode);
                }
            }
        }
        return map;
    }

    protected abstract Collection<PathNode> getAdjacent(PathNode prev, WorldShapeCache cache, BattleBounds bounds);

    protected static boolean checkPosition(final Entity entity, final BlockPos pos, final BattleBounds bounds, final WorldShapeCache cache) {
        return checkPosition(entity, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, bounds, cache);
    }

    protected static boolean checkPosition(final Entity entity, final Vec3d pos, final BattleBounds bounds, final WorldShapeCache cache) {
        return checkPosition(entity, pos.x, pos.y, pos.z, bounds, cache);
    }

    protected static boolean checkPosition(final Entity entity, final double xPos, final double yPos, final double zPos, final BattleBounds bounds, final WorldShapeCache cache) {
        final Box box = entity.getDimensions(entity.getPose()).getBoxAt(xPos, yPos, zPos);
        if (!bounds.isIn(box)) {
            return false;
        }
        final boolean b = box.getAverageSideLength() <= 1;
        if (!b) {
            for (final Entity cachedEntity : cache.getEntities()) {
                if (cachedEntity.getBoundingBox().intersects(box)) {
                    return false;
                }
            }
        }
        final VoxelShape entityBounds = VoxelShapes.cuboid(box);
        for (int x = MathHelper.floor(box.minX); x < MathHelper.ceil(box.maxX); x++) {
            for (int y = MathHelper.floor(box.minY); y < MathHelper.ceil(box.maxY); y++) {
                for (int z = MathHelper.floor(box.minZ); z < MathHelper.ceil(box.maxZ); z++) {
                    final VoxelShape shape = cache.getShape(x, y, z);
                    if (!shape.isEmpty() || VoxelShapes.matchesAnywhere(entityBounds, shape, BooleanBiFunction.AND)) {
                        return false;
                    }
                }
            }
        }
        if(b) {
            for (final Entity cachedEntity : cache.getEntities()) {
                if (cachedEntity.getBoundingBox().intersects(box)) {
                    return false;
                }
            }
        }
        return true;
    }

    protected static boolean checkFloorPosition(final Entity entity, final BlockPos pos, final BattleBounds bounds, final WorldShapeCache cache) {
        return checkFloorPosition(entity, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, bounds, cache);
    }

    protected static boolean checkFloorPosition(final Entity entity, final Vec3d pos, final BattleBounds bounds, final WorldShapeCache cache) {
        return checkFloorPosition(entity, pos.x, pos.y, pos.z, bounds, cache);
    }

    protected static boolean checkFloorPosition(final Entity entity, final double xPos, final double yPos, final double zPos, final BattleBounds bounds, final WorldShapeCache cache) {
        final Box box = entity.getDimensions(entity.getPose()).getBoxAt(xPos, yPos, zPos);
        if (!bounds.isIn(box)) {
            return false;
        }
        final int top = (int) Math.ceil(yPos - 1);

        final VoxelShape entityBounds = VoxelShapes.cuboid(box);
        for (int x = MathHelper.floor(box.minX); x < MathHelper.ceil(box.maxX); x++) {
            for (int z = MathHelper.floor(box.minZ); z < MathHelper.ceil(box.maxZ); z++) {
                final VoxelShape shape = cache.getShape(x, top, z);
                if (!shape.isEmpty() || VoxelShapes.matchesAnywhere(entityBounds, shape, BooleanBiFunction.AND)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected static double getFeetY(final Entity entity, final BlockPos pos, final WorldShapeCache cache) {
        return getFeetY(entity, pos.getX(), pos.getY(), pos.getZ(), cache);
    }

    protected static double getFeetY(final Entity entity, final int xPos, final int yPos, final int zPos, final WorldShapeCache cache) {
        final Box box = entity.getDimensions(entity.getPose()).getBoxAt(xPos, yPos - 1, zPos);
        double feetY = 0;
        for (int x = MathHelper.floor(box.minX); x < MathHelper.ceil(box.maxX); x++) {
            for (int z = MathHelper.floor(box.minZ); z < MathHelper.ceil(box.maxZ); z++) {
                final VoxelShape shape = cache.getShape(x, yPos - 1, z);
                if (!shape.isEmpty()) {
                    feetY = Math.max(feetY, shape.getMax(Direction.Axis.Y));
                }
            }
        }
        return feetY + yPos - 1;
    }
}
