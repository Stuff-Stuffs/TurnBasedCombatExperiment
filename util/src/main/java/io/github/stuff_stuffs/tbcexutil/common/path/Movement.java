package io.github.stuff_stuffs.tbcexutil.common.path;

import io.github.stuff_stuffs.tbcexutil.common.HorizontalDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public interface Movement {
    double getCost();

    BlockPos getStartPos();

    BlockPos getEndPos();

    double getLength();

    Vec3d interpolate(Vec3d start, double t);

    HorizontalDirection getRotation(double t);
}
