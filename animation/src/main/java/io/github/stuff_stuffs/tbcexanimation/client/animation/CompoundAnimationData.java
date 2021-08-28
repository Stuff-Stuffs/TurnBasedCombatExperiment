package io.github.stuff_stuffs.tbcexanimation.client.animation;

import net.minecraft.util.Identifier;

import java.util.List;

public class CompoundAnimationData {
    private final List<Identifier> animations;

    public CompoundAnimationData(List<Identifier> animations) {
        this.animations = animations;
    }

    public List<Identifier> getAnimations() {
        return animations;
    }
}
