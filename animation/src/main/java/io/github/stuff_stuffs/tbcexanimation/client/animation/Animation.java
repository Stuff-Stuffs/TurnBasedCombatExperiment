package io.github.stuff_stuffs.tbcexanimation.client.animation;

import io.github.stuff_stuffs.tbcexanimation.client.model.Model;

public interface Animation {
    void update(Model model, double timeSinceLast);

    double getLength();

    double getTimeRemaining();

    boolean isCancelled();

    default boolean isFinished() {
        return getTimeRemaining() <= 0;
    }
}
