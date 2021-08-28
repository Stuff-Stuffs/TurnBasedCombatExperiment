package io.github.stuff_stuffs.tbcexanimation.client.animation.builtin;

import io.github.stuff_stuffs.tbcexanimation.client.animation.Animation;
import io.github.stuff_stuffs.tbcexanimation.client.model.ModelBoneInstance;
import io.github.stuff_stuffs.tbcexanimation.client.model.Skeleton;
import io.github.stuff_stuffs.tbcexutil.common.Easing;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;

import java.util.Iterator;
import java.util.Map;

public final class ResetAnimation implements Animation {
    private final Map<String, Animation> boneResets = new Object2ReferenceOpenHashMap<>();
    private final double time;
    private final Easing easing;
    private double progress;

    public ResetAnimation(double time, Easing easing) {
        this.time = time;
        this.easing = easing;
    }

    @Override
    public void update(Skeleton skeleton, double timeSinceLast) {
        if (progress == 0) {
            for (String bone : skeleton.getBones()) {
                final ModelBoneInstance boneInstance = skeleton.getBone(bone);
                if (boneInstance == null) {
                    throw new RuntimeException();
                }
                boneResets.put(bone, boneInstance.resetAnimation(time, easing));
            }
        }
        if (progress < time) {
            final Iterator<Map.Entry<String, Animation>> iterator = boneResets.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Animation> animationEntry = iterator.next();
                ModelBoneInstance boneInstance = skeleton.getBone(animationEntry.getKey());
                if (boneInstance == null) {
                    iterator.remove();
                } else {
                    animationEntry.getValue().update(skeleton, timeSinceLast);
                }
            }
            progress += timeSinceLast;
        }
    }

    @Override
    public double getLength() {
        return time;
    }

    @Override
    public double getTimeRemaining() {
        return time-progress;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }
}
