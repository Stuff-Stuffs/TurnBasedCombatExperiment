package io.github.stuff_stuffs.turnbasedcombat.common.api;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class BattleBounds {
    //inclusive
    private final BlockPos min;
    //inclusive
    private final BlockPos max;
    private final Box box;

    public BattleBounds(final BlockPos min, final BlockPos max) {
        this.min = new BlockPos(Math.min(min.getX(), max.getX()), Math.min(min.getY(), max.getY()), Math.min(min.getZ(), max.getZ()));
        this.max = new BlockPos(Math.max(min.getX(), max.getX()), Math.max(min.getY(), max.getY()), Math.max(min.getZ(), max.getZ()));
        box = new Box(min, max);
        check();
    }

    private void check() {
        //TODO
        if (min.getManhattanDistance(max) > 400) {
            throw new RuntimeException();
        }
    }

    private BattleBounds(final IntArrayTag tag) {
        min = new BlockPos(tag.get(0).getInt(), tag.get(1).getInt(), tag.get(2).getInt());
        max = new BlockPos(tag.get(3).getInt(), tag.get(4).getInt(), tag.get(5).getInt());
        box = new Box(min, max);
        check();
    }

    private BattleBounds(final int minX, final int minY, final int minZ, final int maxX, final int maxY, final int maxZ) {
        min = new BlockPos(minX, minY, minZ);
        max = new BlockPos(maxX, maxY, maxZ);
        box = new Box(min, max);
        check();
    }

    public boolean isIn(final BlockPos pos) {
        return isIn(pos.getX(), pos.getY(), pos.getZ());
    }

    public boolean isIn(final int x, final int y, final int z) {
        return min.getX() <= x && x <= max.getX() && min.getY() <= y && y <= max.getY() && min.getZ() <= z && z <= max.getZ();
    }

    public boolean isIn(final Vec3d vec) {
        return isIn(vec.x, vec.y, vec.z);
    }

    public boolean isIn(final double x, final double y, final double z) {
        return min.getX() <= x && x <= max.getX() && min.getY() <= y && y <= max.getY() && min.getZ() <= z && z <= max.getZ();
    }

    public IntArrayTag toTag() {
        return new IntArrayTag(new int[]{min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ()});
    }

    public boolean isIn(final Box box) {
        return this.box.union(box).equals(this.box);
    }

    public Box getBox() {
        return box;
    }

    public BlockPos getMin() {
        return min;
    }

    public BlockPos getMax() {
        return max;
    }

    public static BattleBounds fromTag(final IntArrayTag tag) {
        return new BattleBounds(tag);
    }

    public void toBuf(final PacketByteBuf buf) {
        buf.writeInt(min.getX());
        buf.writeInt(min.getY());
        buf.writeInt(min.getZ());
        buf.writeInt(max.getX());
        buf.writeInt(max.getY());
        buf.writeInt(max.getZ());
    }

    public static BattleBounds fromBuf(final PacketByteBuf buf) {
        return new BattleBounds(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static BattleBounds fromEntities(final Iterable<? extends Entity> entities, final int horizontalMargin, final int verticalMargin) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (final Entity entity : entities) {
            minX = Math.min(entity.getBlockX(), minX);
            minY = Math.min(entity.getBlockY(), minY);
            minZ = Math.min(entity.getBlockZ(), minZ);
            maxX = Math.max(entity.getBlockX(), maxX);
            maxY = Math.max(entity.getBlockY(), maxY);
            maxZ = Math.max(entity.getBlockZ(), maxZ);
        }
        return new BattleBounds(minX - horizontalMargin, minY - verticalMargin, minZ - horizontalMargin, maxX + horizontalMargin, maxY + verticalMargin, maxZ + horizontalMargin);
    }
}
