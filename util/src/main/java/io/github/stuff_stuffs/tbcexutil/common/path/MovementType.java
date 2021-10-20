package io.github.stuff_stuffs.tbcexutil.common.path;

import com.mojang.serialization.DynamicOps;
import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import io.github.stuff_stuffs.tbcexutil.common.HorizontalDirection;
import io.github.stuff_stuffs.tbcexutil.common.WorldShapeCache;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.minecraft.util.math.MathHelper.ceil;
import static net.minecraft.util.math.MathHelper.floor;

public interface MovementType {
    @Nullable Movement modify(BattleParticipantBounds bounds, BlockPos pos, Box pathBounds, World world, WorldShapeCache cache);

    <T> T serialize(DynamicOps<T> ops, Movement movement);

    <T> Movement deserialize(DynamicOps<T> ops, T serialized);

    List<MovementType> LAND = Util.make(new ArrayList<>(), l -> {
        Collections.addAll(l, BasicMovements.values());
        Collections.addAll(l, DiagonalMovements.values());
        Collections.addAll(l, SimpleJumpMovements.values());
    });

    static boolean doesCollideWith(final BattleParticipantBounds bounds, final WorldShapeCache cache) {
        for (final BattleParticipantBounds.Part part : bounds) {
            final Box box = part.box;
            final int minX = floor(box.minX);
            final int minY = floor(box.minY);
            final int minZ = floor(box.minZ);
            final int maxX = ceil(box.maxX);
            final int maxY = ceil(box.maxY);
            final int maxZ = ceil(box.maxZ);
            final VoxelShape cuboid = VoxelShapes.cuboid(box);
            for (int i = minX; i <= maxX; i++) {
                for (int j = minY; j <= maxY; j++) {
                    for (int k = minZ; k <= maxZ; k++) {
                        final VoxelShape shape = cache.getShape(i, j, k);
                        if (shape.isEmpty()) {
                            continue;
                        }
                        if (shape == VoxelShapes.fullCube()) {
                            if (box.intersects(i, j, k, i + 1, j + 1, k + 1)) {
                                return true;
                            }
                        }
                        if (VoxelShapes.matchesAnywhere(shape.offset(i, j, k), cuboid, BooleanBiFunction.AND)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    static double getGroundHeight(final BattleParticipantBounds bounds, final WorldShapeCache cache) {
        double dist = 0;
        for (final BattleParticipantBounds.Part part : bounds) {
            final Box box = part.box;
            final int minX = floor(box.minX);
            final int minY = floor(box.minY);
            final int minZ = floor(box.minZ);
            final int maxX = ceil(box.maxX);
            final int maxY = ceil(box.maxY);
            final int maxZ = ceil(box.maxZ);
            for (int i = minX; i <= maxX; i++) {
                for (int j = minY; j <= maxY; j++) {
                    for (int k = minZ; k <= maxZ; k++) {
                        final VoxelShape shape = cache.getShape(i, j, k);
                        if (!shape.isEmpty()) {
                            final double v = shape.calculateMaxDistance(Direction.Axis.Y, box, Double.POSITIVE_INFINITY);
                            if (v != Double.POSITIVE_INFINITY && v > 0) {
                                dist = Math.max(dist, v);
                            }
                        }
                    }
                }
            }
        }
        return dist;
    }
}
