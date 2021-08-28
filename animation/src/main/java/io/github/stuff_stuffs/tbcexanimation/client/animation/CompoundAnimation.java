package io.github.stuff_stuffs.tbcexanimation.client.animation;

import io.github.stuff_stuffs.tbcexanimation.client.TBCExAnimationClient;
import io.github.stuff_stuffs.tbcexanimation.client.model.Skeleton;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class CompoundAnimation implements Animation {
    private final List<Animation> animations;
    private int index;
    private boolean finished;

    public CompoundAnimation(final CompoundAnimationData data) {
        animations = new ArrayList<>(data.getAnimations().size());
        for (final Identifier animationId : data.getAnimations()) {
            final Animation animation = TBCExAnimationClient.MODEL_MANAGER.getAnimation(animationId);
            if (animation == null) {
                throw new RuntimeException("Error, missing animation: " + animationId);
            }
            animations.add(animation);
        }
    }

    @Override
    public void update(final Skeleton skeleton, double timeSinceLast) {
        if (!finished) {
            if (index >= animations.size()) {
                finished = true;
                return;
            }
            final Animation animation = animations.get(index);
            if (animation.isFinished() || animation.isCancelled()) {
                index++;
                update(skeleton, timeSinceLast);
                return;
            }
            final double currentTimeLeft = animation.getTimeRemaining();
            if (currentTimeLeft <= timeSinceLast) {
                timeSinceLast -= currentTimeLeft;
                animation.update(skeleton, currentTimeLeft);
                index++;
                if (timeSinceLast > 0) {
                    update(skeleton, timeSinceLast);
                }
            } else {
                animation.update(skeleton, timeSinceLast);
            }
        }
    }

    @Override
    public double getLength() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double getTimeRemaining() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }
}
