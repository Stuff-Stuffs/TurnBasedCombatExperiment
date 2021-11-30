package io.github.stuff_stuffs.tbcexcharacter.common.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.function.IntToDoubleFunction;

public class XpContainer {
    public static final Codec<XpContainer> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.INT.fieldOf("level").forGetter(container -> container.level), Codec.DOUBLE.fieldOf("currentXp").forGetter(container -> container.currentXp)).apply(instance, XpContainer::new));
    //TODO config
    private static final IntToDoubleFunction XP_REQUIRED_TO_LEVEL = i -> (i + 1) * 100;
    private int level;
    private double currentXp;
    private double cumulativeXp;

    public XpContainer(final int level, final double currentXp) {
        this.level = level;
        this.currentXp = currentXp;
        double acc = currentXp;
        for (int i = 0; i < level; i++) {
            acc += XP_REQUIRED_TO_LEVEL.applyAsDouble(i);
        }
        cumulativeXp = acc;
    }

    public boolean tryLevelUp() {
        final double required = XP_REQUIRED_TO_LEVEL.applyAsDouble(level);
        if (currentXp < required) {
            return false;
        }
        currentXp -= required;
        level++;
        return true;
    }

    public void addXp(final double xp) {
        currentXp += xp;
        cumulativeXp += xp;
    }

    public int getLevel() {
        return level;
    }

    public double getCumulativeXp() {
        return cumulativeXp;
    }

    public double getCurrentXp() {
        return currentXp;
    }

    public double getCurrentLevelProgress() {
        return currentXp / XP_REQUIRED_TO_LEVEL.applyAsDouble(level);
    }
}
