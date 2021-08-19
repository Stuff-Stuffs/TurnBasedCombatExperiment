package io.github.stuff_stuffs.tbcexanimation.client.animation;

import io.github.stuff_stuffs.tbcexanimation.common.util.DoubleQuaternion;
import io.github.stuff_stuffs.tbcexanimation.common.util.Easing;
import it.unimi.dsi.fastutil.doubles.Double2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectSortedMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public final class KeyframeAnimationData {
    private final Map<String, Double2ObjectSortedMap<KeyframeData>> keyframes;
    private final Set<String> requiredBones;
    private final double length;
    private final boolean loop;

    private KeyframeAnimationData(final Map<String, Double2ObjectSortedMap<KeyframeData>> keyframes, final boolean loop) {
        this.keyframes = keyframes;
        double max = 0;
        for (final Double2ObjectSortedMap<KeyframeData> value : keyframes.values()) {
            max = Math.max(max, value.lastDoubleKey());
        }
        requiredBones = new ObjectOpenHashSet<>();
        requiredBones.addAll(keyframes.keySet());
        length = max;
        this.loop = loop;
    }

    public boolean isLooped() {
        return loop;
    }

    public double getLength() {
        return length;
    }

    public Set<String> getRequiredBones() {
        return requiredBones;
    }

    public @Nullable KeyframeAnimationData.KeyframeData getInfimum(final String bone, final double time) {
        final Double2ObjectSortedMap<KeyframeData> keyFrameData = keyframes.get(bone);
        if (keyFrameData == null) {
            return null;
        }
        final Double2ObjectSortedMap<KeyframeData> headMap = keyFrameData.headMap(time);
        if (headMap.isEmpty()) {
            return null;
        }
        return headMap.get(headMap.lastDoubleKey());
    }

    public @Nullable KeyframeAnimationData.KeyframeData getSupremum(final String bone, final double time) {
        final Double2ObjectSortedMap<KeyframeData> keyFrameData = keyframes.get(bone);
        if (keyFrameData == null) {
            return null;
        }
        final Double2ObjectSortedMap<KeyframeData> tailMap = keyFrameData.tailMap(time);
        if (tailMap.isEmpty()) {
            return null;
        }
        return tailMap.get(tailMap.firstDoubleKey());
    }

    public static final class KeyframeData {
        public final double startTime;
        public final DoubleQuaternion rotation;
        public final Easing rotationEasing;
        public final Vec3d offset;
        public final Easing positionEasing;

        public KeyframeData(final double startTime, final DoubleQuaternion rotation, final Easing rotationEasing, final Vec3d offset, final Easing positionEasing) {
            this.startTime = startTime;
            this.rotation = rotation;
            this.rotationEasing = rotationEasing;
            this.offset = offset;
            this.positionEasing = positionEasing;
        }
    }

    public static KeyframeData interpolate(final double time, final KeyframeData start, final KeyframeData end) {
        final double normalized = (time - start.startTime) / (end.startTime - start.startTime);
        if (normalized <= 0) {
            return start;
        }
        if (normalized >= 1) {
            return end;
        }
        final double rotationProgress = end.rotationEasing.apply(normalized);
        final double positionProgress = end.rotationEasing.apply(normalized);
        return new KeyframeData(time, DoubleQuaternion.slerp(rotationProgress, start.rotation, end.rotation), end.rotationEasing, start.offset.multiply(positionProgress).add(end.offset.multiply(1 - positionProgress)), end.positionEasing);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Map<String, Double2ObjectSortedMap<KeyframeData>> keyframes = new Object2ReferenceOpenHashMap<>();

        private Builder() {
        }

        public final class KeyFrameBuilder {
            private final Double2ObjectSortedMap<KeyframeData> keyFrames = new Double2ObjectAVLTreeMap<>();
            private final String bone;
            private double lastTime = -1;

            private KeyFrameBuilder(final String bone) {
                this.bone = bone;
            }

            public KeyFrameBuilder addFrame(final KeyframeData data) {
                final double startTime = data.startTime;
                if (startTime <= lastTime && lastTime != -1) {
                    throw new RuntimeException();
                }
                keyFrames.put(startTime, data);
                lastTime = startTime;
                return this;
            }

            public Builder build() {
                keyframes.put(bone, keyFrames);
                return Builder.this;
            }
        }

        public KeyFrameBuilder addBone(final String bone) {
            return new KeyFrameBuilder(bone);
        }

        public KeyframeAnimationData build(final boolean loop) {
            return new KeyframeAnimationData(new Object2ReferenceOpenHashMap<>(keyframes), loop);
        }
    }
}
