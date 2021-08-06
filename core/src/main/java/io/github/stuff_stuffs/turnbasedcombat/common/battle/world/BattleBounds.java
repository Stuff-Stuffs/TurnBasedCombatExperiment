package io.github.stuff_stuffs.turnbasedcombat.common.battle.world;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.stream.IntStream;

public final class BattleBounds {
    public static final Codec<BattleBounds> CODEC = Codec.INT_STREAM.xmap(BattleBounds::new, BattleBounds::asStream);
    public final int minX;
    public final int minY;
    public final int minZ;
    public final int maxX;
    public final int maxY;
    public final int maxZ;
    private Box boxCache;

    private BattleBounds(final IntStream intStream) {
        final int[] arr = intStream.toArray();
        if (arr.length != 6) {
            throw new RuntimeException("Invalid stream length, expected: 6, got: " + arr.length);
        }
        final int x1 = arr[0];
        final int y1 = arr[1];
        final int z1 = arr[2];
        final int x2 = arr[3];
        final int y2 = arr[4];
        final int z2 = arr[5];
        minX = Math.min(x1, x2);
        minY = Math.min(y1, y2);
        minZ = Math.min(z1, z2);
        maxX = Math.max(x1, x2);
        maxY = Math.max(y1, y2);
        maxZ = Math.max(z1, z2);
    }

    public BattleBounds(final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
        minX = Math.min(x1, x2);
        minY = Math.min(y1, y2);
        minZ = Math.min(z1, z2);
        maxX = Math.max(x1, x2);
        maxY = Math.max(y1, y2);
        maxZ = Math.max(z1, z2);
    }

    public boolean isIn(final Vec3i vec) {
        return isIn(vec.getX(), vec.getY(), vec.getZ());
    }

    public boolean isIn(final int x, final int y, final int z) {
        return minX <= x && x <= maxX && minY <= y && y <= maxY && minZ <= z && z <= maxZ;
    }

    public boolean isIn(final Vec3d vec) {
        return isIn(vec.x, vec.y, vec.z);
    }

    public boolean isIn(final double x, final double y, final double z) {
        return minX <= x && x <= maxX && minY <= y && y <= maxY && minZ <= z && z <= maxZ;
    }

    public int getXLength() {
        return maxX - minX;
    }

    public int getYLength() {
        return maxY - minY;
    }

    public int getZLength() {
        return maxZ - minZ;
    }

    public int getNearestX(final int x) {
        return Math.max(Math.min(x, maxX), minX);
    }

    public int getNearestY(final int y) {
        return Math.max(Math.min(y, maxY), minY);
    }

    public int getNearestZ(final int z) {
        return Math.max(Math.min(z, maxZ), minZ);
    }

    public Box getBox() {
        return boxCache == null ? boxCache = new Box(minX, minY, minZ, maxX, maxY, maxZ) : boxCache;
    }

    public BlockPos getNearest(final BlockPos pos) {
        return new BlockPos(getNearestX(pos.getX()), getNearestY(pos.getY()), getNearestZ(pos.getZ()));
    }

    public IntStream asStream() {
        return IntStream.of(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
