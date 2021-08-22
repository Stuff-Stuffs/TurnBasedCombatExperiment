package io.github.stuff_stuffs.tbcexanimation.client.animation;

import io.github.stuff_stuffs.tbcexanimation.client.model.Skeleton;

public interface Animation {
    void update(Skeleton skeleton, double timeSinceLast);

    double getLength();

    double getTimeRemaining();

    boolean isCancelled();

    default boolean isFinished() {
        return getTimeRemaining() <= 0;
    }
}
