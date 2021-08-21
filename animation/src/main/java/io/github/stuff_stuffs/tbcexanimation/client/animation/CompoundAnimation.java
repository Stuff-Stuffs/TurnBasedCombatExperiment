package io.github.stuff_stuffs.tbcexanimation.client.animation;

import io.github.stuff_stuffs.tbcexanimation.client.model.Model;

import java.util.List;

public class CompoundAnimation implements Animation {
    private final List<Animation> animations;
    private final double length;
    private double currentTime;
    private int index;
    private boolean finished;

    public CompoundAnimation(final List<Animation> animations) {
        this.animations = animations;
        double l = 0;
        for (final Animation animation : animations) {
            l += animation.getLength();
        }
        length = l;
    }

    @Override
    public void update(final Model model, double timeSinceLast) {
        if (!finished) {
            if (index >= animations.size()) {
                finished = true;
                return;
            }
            final Animation animation = animations.get(index);
            if(animation.isFinished()||animation.isCancelled()) {
                index++;
                update(model, timeSinceLast);
                return;
            }
            final double currentTimeLeft = animation.getTimeRemaining();
            if (currentTimeLeft <= timeSinceLast) {
                timeSinceLast -= currentTimeLeft;
                animation.update(model, currentTimeLeft);
                index++;
                if (timeSinceLast > 0) {
                    update(model, timeSinceLast);
                }
            } else {
                animation.update(model, currentTimeLeft);
            }
            currentTime += timeSinceLast;
        }
    }

    @Override
    public double getLength() {
        return length;
    }

    @Override
    public double getTimeRemaining() {
        return length - currentTime;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }
}
