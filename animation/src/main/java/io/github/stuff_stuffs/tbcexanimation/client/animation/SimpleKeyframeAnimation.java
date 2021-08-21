package io.github.stuff_stuffs.tbcexanimation.client.animation;

import io.github.stuff_stuffs.tbcexanimation.client.model.Model;
import io.github.stuff_stuffs.tbcexanimation.client.model.ModelBoneInstance;
import io.github.stuff_stuffs.tbcexutil.common.Easing;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;

import java.util.Map;

public class SimpleKeyframeAnimation implements Animation {
    private final KeyframeAnimationData data;
    private final Map<String, KeyframeAnimationData.KeyframeData> defaultData = new Object2ReferenceOpenHashMap<>();
    private int loopCount = 0;
    private double progress = 0;
    private boolean cancelled = false;
    private boolean finished = false;

    public SimpleKeyframeAnimation(final KeyframeAnimationData data) {
        this.data = data;
    }

    @Override
    public void update(final Model model, final double timeSinceLast) {
        if (!cancelled && !finished) {
            if (progress == 0 && loopCount == 0) {
                for (final String bone : model.getBones()) {
                    final ModelBoneInstance instance = model.getBone(bone);
                    if (instance == null) {
                        throw new RuntimeException();
                    }
                    defaultData.put(bone, new KeyframeAnimationData.KeyframeData(0, instance.getRotation(), Easing.easeInQuad, instance.getOffset(), Easing.easeInQuad));
                }
            }
            for (final String bone : model.getBones()) {
                final ModelBoneInstance boneInstance = model.getBone(bone);
                if (boneInstance == null) {
                    cancelled = true;
                    return;
                }
                KeyframeAnimationData.KeyframeData infimum = data.getInfimum(bone, progress - loopCount * data.getLength());
                if (loopCount == 0 && infimum == null) {
                    infimum = defaultData.get(bone);
                } else if (loopCount > 0) {
                    data.getInfimum(bone, Double.MAX_VALUE);
                }
                KeyframeAnimationData.KeyframeData supremum = data.getInfimum(bone, progress - loopCount * data.getLength());
                if (supremum == null && loopCount > 0) {
                    supremum = data.getSupremum(bone, -Double.MAX_VALUE);
                }
                if (supremum == null) {
                    boneInstance.setRotation(infimum.rotation);
                    boneInstance.setOffset(infimum.offset);
                } else {
                    updateBone(boneInstance, infimum, supremum);
                }
            }
            if (progress == data.getLength() && !data.isLooped()) {
                finished = true;
                return;
            }
            progress += timeSinceLast;
            if (!data.isLooped()) {
                progress = Math.min(progress, data.getLength());
            } else {
                loopCount = (int) Math.floor(progress / data.getLength());
            }
        }
    }

    private void updateBone(final ModelBoneInstance boneInstance, final KeyframeAnimationData.KeyframeData start, final KeyframeAnimationData.KeyframeData end) {
        final KeyframeAnimationData.KeyframeData interpolated = KeyframeAnimationData.interpolate(progress - loopCount * data.getLength(), start, end);
        boneInstance.setRotation(interpolated.rotation);
        boneInstance.setOffset(interpolated.offset);
    }

    @Override
    public double getLength() {
        return data.isLooped() ? Double.MAX_VALUE : data.getLength();
    }

    @Override
    public double getTimeRemaining() {
        return data.isLooped() ? Double.MAX_VALUE : data.getLength() - progress;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
