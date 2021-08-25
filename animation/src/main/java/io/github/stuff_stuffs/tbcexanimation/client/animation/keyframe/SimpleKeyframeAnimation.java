package io.github.stuff_stuffs.tbcexanimation.client.animation.keyframe;

import io.github.stuff_stuffs.tbcexanimation.client.animation.Animation;
import io.github.stuff_stuffs.tbcexanimation.client.model.ModelBoneInstance;
import io.github.stuff_stuffs.tbcexanimation.client.model.Skeleton;
import io.github.stuff_stuffs.tbcexutil.common.DoubleQuaternion;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.util.math.Vec3d;

import java.util.Map;

public class SimpleKeyframeAnimation implements Animation {
    private final KeyframeAnimationData data;
    private final Map<String, DoubleQuaternion> defaultRotation = new Object2ReferenceOpenHashMap<>();
    private final Map<String, Vec3d> defaultPosition = new Object2ReferenceOpenHashMap<>();
    private final Map<String, Vec3d> defaultScale = new Object2ReferenceOpenHashMap<>();
    private int loopCount = 0;
    private double progress = 0;
    private boolean cancelled = false;
    private boolean finished = false;

    public SimpleKeyframeAnimation(final KeyframeAnimationData data) {
        this.data = data;
    }

    @Override
    public void update(final Skeleton skeleton, final double timeSinceLast) {
        if (!cancelled && !finished) {
            if (progress == 0 && loopCount == 0) {
                for (final String bone : skeleton.getBones()) {
                    final ModelBoneInstance instance = skeleton.getBone(bone);
                    if (instance == null) {
                        throw new RuntimeException();
                    }
                    defaultRotation.put(bone, instance.getRotation());
                    defaultPosition.put(bone, instance.getOffset());
                    defaultScale.put(bone, instance.getScale());
                }
            }
            for (final String bone : skeleton.getBones()) {
                final ModelBoneInstance boneInstance = skeleton.getBone(bone);
                if (boneInstance == null) {
                    cancelled = true;
                    return;
                }
                updateBone(boneInstance);
            }
            if (progress >= data.getLength()*20 && !data.isLooped()) {
                finished = true;
            }
            progress += timeSinceLast;
            if (!data.isLooped()) {
                progress = Math.min(progress, data.getLength()*20);
            } else {
                loopCount = (int) Math.floor(progress / (data.getLength()*20));
            }
        }
    }

    private void updateBone(final ModelBoneInstance boneInstance) {
        final String name = boneInstance.getBone().getName();
        boneInstance.setRotation(data.getRotation(name, defaultRotation.get(name), progress/20.0));
        boneInstance.setOffset(data.getPosition(name, defaultPosition.get(name), progress/20.0));
        boneInstance.setScale(data.getScale(name, defaultScale.get(name), progress/20.0));
    }

    @Override
    public double getLength() {
        return data.isLooped() ? Double.MAX_VALUE : data.getLength()*20;
    }

    @Override
    public double getTimeRemaining() {
        return data.isLooped() ? Double.MAX_VALUE : data.getLength()*20 - progress;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
