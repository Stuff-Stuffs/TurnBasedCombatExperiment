package io.github.stuff_stuffs.tbcexanimation.client.model;

import io.github.stuff_stuffs.tbcexanimation.common.util.DoubleQuaternion;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class ModelBone {
    private final String name;
    private final Vec3d defaultPos;
    private final DoubleQuaternion defaultRotation;
    private final List<Pair<Vec3d, Vec3d>> boneLines;
    private final @Nullable ModelBone parent;

    public ModelBone(final String name, final Vec3d defaultPos, final DoubleQuaternion defaultRotation, List<Pair<Vec3d, Vec3d>> boneLines, @Nullable final ModelBone parent) {
        this.name = name;
        this.defaultPos = defaultPos;
        this.defaultRotation = defaultRotation;
        this.boneLines = boneLines;
        this.parent = parent;
    }

    public List<Pair<Vec3d, Vec3d>> getBoneLines() {
        return boneLines;
    }

    public String getName() {
        return name;
    }

    public Vec3d getDefaultPos() {
        return defaultPos;
    }

    public DoubleQuaternion getDefaultRotation() {
        return defaultRotation;
    }

    public @Nullable ModelBone getParent() {
        return parent;
    }
}
