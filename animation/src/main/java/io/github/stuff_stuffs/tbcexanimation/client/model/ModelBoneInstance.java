package io.github.stuff_stuffs.tbcexanimation.client.model;

import io.github.stuff_stuffs.tbcexanimation.client.animation.Animation;
import io.github.stuff_stuffs.tbcexanimation.common.util.DoubleQuaternion;
import io.github.stuff_stuffs.tbcexanimation.common.util.Easing;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class ModelBoneInstance {
    private final ModelBone bone;
    private final Map<String, ModelPart> parts;
    private final @Nullable ModelBoneInstance parent;
    private Vec3d offset;
    private DoubleQuaternion rotation;

    public ModelBoneInstance(final ModelBone bone, @Nullable final ModelBoneInstance parent) {
        this.bone = bone;
        parts = new Object2ReferenceOpenHashMap<>();
        this.parent = parent;
        offset = bone.getDefaultPos();
        rotation = bone.getDefaultRotation();
    }

    public boolean containsPart(final String part) {
        return parts.containsKey(part);
    }

    public boolean addPart(final String name, final ModelPart part) {
        if (!parts.containsKey(name)) {
            parts.put(name, part);
            return true;
        }
        return false;
    }

    public void removePart(final String name) {
        parts.remove(name);
    }

    public void reset() {
        offset = bone.getDefaultPos();
        rotation = bone.getDefaultRotation();
    }

    public void transform(final MatrixStack matrices) {
        if (parent != null) {
            parent.transform(matrices);
        }
        matrices.translate(offset.x, offset.y, offset.z);
        matrices.multiply(rotation.toFloatQuat());
    }

    public Animation resetAnimationAutomatic(final double scale, final Easing easing) {
        final double angleDifference = Math.toDegrees(DoubleQuaternion.angularDistance(rotation, bone.getDefaultRotation())) / 135.0;
        final double distance = offset.distanceTo(bone.getDefaultPos());
        final double max = Math.max(distance, angleDifference);
        return resetAnimation(max * scale, easing);
    }

    public Animation resetAnimation(final double time, final Easing easing) {
        return new Animation() {
            private final DoubleQuaternion startingRotation = rotation;
            private final Vec3d startingOffset = offset;
            private final String name = bone.getName();
            private double progress = 0;
            private boolean cancelled = false;

            @Override
            public void update(final Model model, final double timeSinceLast) {
                if (!cancelled && progress < time) {
                    progress = Math.min(progress + timeSinceLast, time);
                    final ModelBoneInstance boneInstance = model.getBone(name);
                    if (boneInstance == null) {
                        cancelled = true;
                        return;
                    }
                    final double eased = easing.apply(progress / time);
                    boneInstance.setRotation(DoubleQuaternion.slerp(eased, startingRotation, bone.getDefaultRotation()));
                    boneInstance.setOffset(startingOffset.multiply(1 - eased).add(bone.getDefaultPos().multiply(eased)));
                }
            }

            @Override
            public double getLength() {
                return time;
            }

            @Override
            public double getTimeRemaining() {
                return time - progress;
            }

            @Override
            public boolean isCancelled() {
                return cancelled;
            }
        };
    }

    public @Nullable ModelBoneInstance getParent() {
        return parent;
    }

    public ModelBone getBone() {
        return bone;
    }

    public Vec3d getOffset() {
        return offset;
    }

    public DoubleQuaternion getRotation() {
        return rotation;
    }

    public void setOffset(final Vec3d offset) {
        this.offset = offset;
    }

    public void setRotation(final DoubleQuaternion rotation) {
        this.rotation = rotation;
    }

    public void offset(final Vec3d offset) {
        this.offset = this.offset.add(offset);
    }

    public void rotate(final DoubleQuaternion quaternion) {
        rotation = rotation.multiply(quaternion).normalize();
    }

    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        matrices.push();
        transform(matrices);
        for (ModelPart part : parts.values()) {
            matrices.push();
            part.render(matrices, vertexConsumers);
            matrices.pop();
        }
        matrices.pop();
    }
}
