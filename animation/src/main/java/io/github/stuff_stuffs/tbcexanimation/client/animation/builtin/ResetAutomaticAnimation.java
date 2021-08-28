package io.github.stuff_stuffs.tbcexanimation.client.animation.builtin;

import io.github.stuff_stuffs.tbcexanimation.client.animation.Animation;
import io.github.stuff_stuffs.tbcexanimation.client.model.ModelBoneInstance;
import io.github.stuff_stuffs.tbcexanimation.client.model.Skeleton;
import io.github.stuff_stuffs.tbcexutil.common.Easing;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;

import java.util.Iterator;
import java.util.Map;

public class ResetAutomaticAnimation implements Animation {
    private final Map<String, Animation> boneResets = new Object2ReferenceOpenHashMap<>();
    private final double scale;
    private final Easing easing;
    private double progress;
    private double maxLength;

    public ResetAutomaticAnimation(final double scale, final Easing easing) {
        this.scale = scale;
        this.easing = easing;
    }

    @Override
    public void update(final Skeleton skeleton, final double timeSinceLast) {
        if (progress == 0) {
            maxLength = 0;
            for (final String bone : skeleton.getBones()) {
                final ModelBoneInstance boneInstance = skeleton.getBone(bone);
                if (boneInstance == null) {
                    throw new RuntimeException();
                }
                final Animation animation = boneInstance.resetAnimationAutomatic(scale, easing);
                animation.update(skeleton, 0);
                boneResets.put(bone, animation);
                maxLength = Math.max(maxLength, animation.getLength());
            }
        }
        if (progress < maxLength) {
            final Iterator<Map.Entry<String, Animation>> iterator = boneResets.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry<String, Animation> animationEntry = iterator.next();
                final ModelBoneInstance boneInstance = skeleton.getBone(animationEntry.getKey());
                if (boneInstance == null) {
                    iterator.remove();
                } else {
                    if (!animationEntry.getValue().isFinished()) {
                        animationEntry.getValue().update(skeleton, timeSinceLast);
                    }
                }
            }
            progress += timeSinceLast;
        }
    }

    @Override
    public double getLength() {
        return progress == 0 ? Double.POSITIVE_INFINITY : maxLength;
    }

    @Override
    public double getTimeRemaining() {
        return progress==0?Double.MAX_VALUE:maxLength - progress;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }
}
