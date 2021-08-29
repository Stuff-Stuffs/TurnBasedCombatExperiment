package io.github.stuff_stuffs.tbcexanimation.client.model;

import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.tbcexanimation.client.TBCExAnimationClient;
import io.github.stuff_stuffs.tbcexanimation.client.animation.Animation;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.ModelPart;
import io.github.stuff_stuffs.tbcexutil.client.DebugRenderers;
import io.github.stuff_stuffs.tbcexutil.common.DoubleQuaternion;
import io.github.stuff_stuffs.tbcexutil.common.Easing;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class ModelBoneInstance {
    private final ModelBone bone;
    private final Map<String, ModelPart> parts;
    private final @Nullable ModelBoneInstance parent;
    private Vec3d offset;
    private DoubleQuaternion rotation;
    private Vec3d scale;

    public ModelBoneInstance(final ModelBone bone, @Nullable final ModelBoneInstance parent) {
        this.bone = bone;
        parts = new Object2ReferenceOpenHashMap<>();
        this.parent = parent;
        offset = bone.getDefaultPos();
        rotation = bone.getDefaultRotation();
        scale = bone.getDefaultScale();
    }

    public boolean containsPart(final String part) {
        return parts.containsKey(part);
    }

    public boolean containsPart(final String partName, final ModelPart part) {
        if (parts.containsKey(partName)) {
            return parts.get(partName).equals(part);
        }
        return false;
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
        scale = bone.getDefaultScale();
    }

    public void transform(final MatrixStack matrices) {
        if (parent != null) {
            parent.transform(matrices);
        }
        matrices.translate(offset.x - bone.getPivotPoint().x, offset.y - bone.getPivotPoint().y, offset.z - bone.getPivotPoint().z);
        matrices.multiply(rotation.toFloatQuat());
        matrices.translate(bone.getPivotPoint().x, bone.getPivotPoint().y, bone.getPivotPoint().z);
        matrices.scale((float) scale.x, (float) scale.y, (float) scale.z);
    }

    public Animation resetAnimationAutomatic(final double scale, final Easing easing) {
        final double angleDifference = Math.toDegrees(DoubleQuaternion.angularDistance(rotation, bone.getDefaultRotation())) / 135.0;
        final double distance = offset.distanceTo(bone.getDefaultPos());
        final double scaleDistance = this.scale.distanceTo(bone.getDefaultScale());
        final double max = Math.max(distance, Math.max(angleDifference, scaleDistance));
        return resetAnimation(max * scale, easing);
    }

    public Animation resetAnimation(final double time, final Easing easing) {
        return new Animation() {
            private final DoubleQuaternion startingRotation = rotation;
            private final Vec3d startingOffset = offset;
            private final Vec3d startingScale = scale;
            private final String name = bone.getName();
            private double progress = 0;
            private boolean cancelled = false;

            @Override
            public void update(final Skeleton skeleton, final double timeSinceLast) {
                if (!cancelled && progress < time) {
                    progress = Math.min(progress + timeSinceLast, time);
                    final ModelBoneInstance boneInstance = skeleton.getBone(name);
                    if (boneInstance == null) {
                        cancelled = true;
                        return;
                    }
                    final double eased = easing.apply(progress / time);
                    boneInstance.setRotation(DoubleQuaternion.slerp(eased, startingRotation, bone.getDefaultRotation()));
                    boneInstance.setOffset(startingOffset.multiply(1 - eased).add(bone.getDefaultPos().multiply(eased)));
                    boneInstance.setScale(startingScale.multiply(1 - eased).add(bone.getDefaultScale().multiply(eased)));
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

    public Vec3d getScale() {
        return scale;
    }

    public void setOffset(final Vec3d offset) {
        this.offset = offset;
    }

    public void setRotation(final DoubleQuaternion rotation) {
        this.rotation = rotation;
    }

    public void setScale(final Vec3d scale) {
        this.scale = scale;
    }

    public void offset(final Vec3d offset) {
        this.offset = this.offset.add(offset);
    }

    public void rotate(final DoubleQuaternion quaternion) {
        rotation = rotation.multiply(quaternion).normalize();
    }

    public void scale(final Vec3d scale) {
        this.scale = scale.multiply(this.scale);
    }

    public void render(final MatrixStack matrices, final VertexConsumerProvider vertexConsumers, final World world, final Vec3d pos) {
        matrices.push();
        transform(matrices);
        if (DebugRenderers.get(TBCExAnimationClient.BONE_DEBUG_RENDERER)) {
            final VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.LINES);
            for (final Pair<Vec3d, Vec3d> boneLine : bone.getBoneLines()) {
                final Vec3d start = boneLine.getFirst();
                final Vec3d end = boneLine.getSecond();
                final Vec3d normal = end.subtract(start).normalize();
                buffer.vertex(matrices.peek().getModel(), (float) start.x, (float) start.y, (float) start.z).color(255, 255, 255, 255).normal(matrices.peek().getNormal(), (float) normal.x, (float) normal.y, (float) normal.z).next();
                buffer.vertex(matrices.peek().getModel(), (float) end.x, (float) end.y, (float) end.z).color(255, 255, 255, 255).normal(matrices.peek().getNormal(), (float) normal.x, (float) normal.y, (float) normal.z).next();
            }
        } else {
            for (final ModelPart part : parts.values()) {
                matrices.push();
                part.render(matrices, vertexConsumers, world, pos);
                matrices.pop();
            }
        }
        matrices.pop();
    }

    public void tick(final double timeSinceLast) {
        for (final ModelPart part : parts.values()) {
            part.tick(timeSinceLast);
        }
    }
}
