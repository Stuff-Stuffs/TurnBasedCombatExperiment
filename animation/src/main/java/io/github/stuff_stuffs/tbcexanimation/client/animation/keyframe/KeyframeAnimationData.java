package io.github.stuff_stuffs.tbcexanimation.client.animation.keyframe;

import io.github.stuff_stuffs.tbcexutil.common.DoubleQuaternion;
import io.github.stuff_stuffs.tbcexutil.common.Easing;
import it.unimi.dsi.fastutil.doubles.Double2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectSortedMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public final class KeyframeAnimationData {
    private final Map<String, Double2ObjectSortedMap<RotationKeyframe>> rotationKeyframes;
    private final Map<String, Double2ObjectSortedMap<VecKeyframe>> positionKeyframes;
    private final Map<String, Double2ObjectSortedMap<VecKeyframe>> scaleKeyframes;
    private final double length;

    private KeyframeAnimationData(final Map<String, Double2ObjectSortedMap<RotationKeyframe>> rotationKeyframes, final Map<String, Double2ObjectSortedMap<VecKeyframe>> positionKeyframes, final Map<String, Double2ObjectSortedMap<VecKeyframe>> scaleKeyframes, final double length) {
        this.rotationKeyframes = rotationKeyframes;
        this.positionKeyframes = positionKeyframes;
        this.scaleKeyframes = scaleKeyframes;
        this.length = length;
    }

    public double getLength() {
        return length;
    }


    public DoubleQuaternion getRotation(final String bone, final DoubleQuaternion start, final double t) {
        if (!rotationKeyframes.containsKey(bone)) {
            return start;
        }
        final Double2ObjectSortedMap<RotationKeyframe> keyframes = rotationKeyframes.get(bone);
        return interp(new RotationKeyframe(start, Easing.easeOutQuad, 0), keyframes, t, ((start1, end, t1) -> DoubleQuaternion.slerp(t1, start1, end)));
    }

    public Vec3d getPosition(final String bone, final Vec3d start, final double t) {
        if (!positionKeyframes.containsKey(bone)) {
            return start;
        }
        final Double2ObjectSortedMap<VecKeyframe> keyframes = positionKeyframes.get(bone);
        return interp(new VecKeyframe(start, Easing.easeOutQuad, 0), keyframes, t, ((start1, end, t1) -> start1.multiply(1 - t1).add(end.multiply(t1))));
    }

    public Vec3d getScale(final String bone, final Vec3d start, final double t) {
        if (!scaleKeyframes.containsKey(bone)) {
            return start;
        }
        final Double2ObjectSortedMap<VecKeyframe> keyframes = scaleKeyframes.get(bone);
        return interp(new VecKeyframe(start, Easing.easeOutQuad, 0), keyframes, t, ((start1, end, t1) -> start1.multiply(1 - t1).add(end.multiply(t1))));
    }

    private <T> T interp(final Keyframe<T> start, final Double2ObjectSortedMap<? extends Keyframe<T>> keyframes, final double t, final Interpolator<T> interpolator) {
        final Keyframe<T> infimum = Objects.requireNonNullElse(getInfimum(keyframes, t), start);
        final Keyframe<T> supremum = getSupremum(keyframes, t);
        if (supremum == null) {
            return infimum.getValue();
        }
        final double normalized = infimum.getStartTime()== supremum.getStartTime()?0:(t - infimum.getStartTime()) / (supremum.getStartTime()) - infimum.getStartTime();
        if (normalized <= 0) {
            return infimum.getValue();
        }
        if (normalized >= 1) {
            return supremum.getValue();
        }
        final double progress = supremum.getEasing().apply(normalized);
        return interpolator.interp(infimum.getValue(), supremum.getValue(), progress);
    }

    private static <T> @Nullable Keyframe<T> getInfimum(final Double2ObjectSortedMap<? extends Keyframe<T>> keyframes, final double t) {
        if (keyframes == null) {
            return null;
        }
        final Double2ObjectSortedMap<? extends Keyframe<T>> headMap = keyframes.headMap(t);
        if (headMap.isEmpty()) {
            return null;
        } else {
            return headMap.get(headMap.lastDoubleKey());
        }
    }

    private static <T> @Nullable Keyframe<T> getSupremum(final Double2ObjectSortedMap<? extends Keyframe<T>> keyframes, final double t) {
        if (keyframes == null) {
            return null;
        }
        final Double2ObjectSortedMap<? extends Keyframe<T>> tailMap = keyframes.tailMap(t);
        if (tailMap.isEmpty()) {
            return null;
        } else {
            return tailMap.get(tailMap.firstDoubleKey());
        }
    }

    public interface Keyframe<T> {
        T getValue();

        double getStartTime();

        Easing getEasing();
    }

    public static final class RotationKeyframe implements Keyframe<DoubleQuaternion> {
        private final DoubleQuaternion rotation;
        private final Easing easing;
        private final double startTime;

        public RotationKeyframe(final DoubleQuaternion rotation, final Easing easing, final double startTime) {
            this.rotation = rotation;
            this.easing = easing;
            this.startTime = startTime;
        }

        @Override
        public DoubleQuaternion getValue() {
            return rotation;
        }

        @Override
        public double getStartTime() {
            return startTime;
        }

        @Override
        public Easing getEasing() {
            return easing;
        }
    }

    public static final class VecKeyframe implements Keyframe<Vec3d> {
        private final Vec3d vec;
        private final Easing easing;
        private final double startTime;

        public VecKeyframe(final Vec3d vec, final Easing easing, final double startTime) {
            this.vec = vec;
            this.easing = easing;
            this.startTime = startTime;
        }

        @Override
        public Vec3d getValue() {
            return vec;
        }

        @Override
        public double getStartTime() {
            return startTime;
        }

        @Override
        public Easing getEasing() {
            return easing;
        }
    }

    private interface Interpolator<T> {
        T interp(T start, T end, double t);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Map<String, Double2ObjectSortedMap<RotationKeyframe>> rotationKeyframes = new Object2ReferenceOpenHashMap<>();
        private final Map<String, Double2ObjectSortedMap<VecKeyframe>> positionKeyframes = new Object2ReferenceOpenHashMap<>();
        private final Map<String, Double2ObjectSortedMap<VecKeyframe>> scaleKeyframes = new Object2ReferenceOpenHashMap<>();
        private boolean built = false;

        private Builder() {
        }

        public Builder addRotationKeyframe(final String bone, final DoubleQuaternion rotation, final Easing easing, final double time) {
            if (built) {
                throw new RuntimeException();
            }
            if (!rotationKeyframes.computeIfAbsent(bone, b -> new Double2ObjectAVLTreeMap<>()).tailMap(time).isEmpty()) {
                throw new RuntimeException();
            }
            rotationKeyframes.get(bone).put(time, new RotationKeyframe(rotation, easing, time));
            return this;
        }

        public Builder addPositionKeyframe(final String bone, final Vec3d pos, final Easing easing, final double time) {
            if (built) {
                throw new RuntimeException();
            }
            if (!positionKeyframes.computeIfAbsent(bone, b -> new Double2ObjectAVLTreeMap<>()).tailMap(time).isEmpty()) {
                throw new RuntimeException();
            }
            positionKeyframes.get(bone).put(time, new VecKeyframe(pos, easing, time));
            return this;
        }

        public Builder addScaleKeyframe(final String bone, final Vec3d scale, final Easing easing, final double time) {
            if (built) {
                throw new RuntimeException();
            }
            if (!scaleKeyframes.computeIfAbsent(bone, b -> new Double2ObjectAVLTreeMap<>()).tailMap(time).isEmpty()) {
                throw new RuntimeException();
            }
            scaleKeyframes.get(bone).put(time, new VecKeyframe(scale, easing, time));
            return this;
        }

        public KeyframeAnimationData build(final double length, final boolean loop) {
            if (built) {
                throw new RuntimeException();
            }
            built = true;
            for (final Double2ObjectSortedMap<RotationKeyframe> map : rotationKeyframes.values()) {
                final Double2ObjectSortedMap<RotationKeyframe> tailMap = map.tailMap(length + 0.0000001);
                if (!tailMap.isEmpty()) {
                    throw new RuntimeException();
                }
            }
            for (final Double2ObjectSortedMap<VecKeyframe> map : positionKeyframes.values()) {
                final Double2ObjectSortedMap<VecKeyframe> tailMap = map.tailMap(length + 0.0000001);
                if (!tailMap.isEmpty()) {
                    throw new RuntimeException();
                }
            }
            for (final Double2ObjectSortedMap<VecKeyframe> map : scaleKeyframes.values()) {
                final Double2ObjectSortedMap<VecKeyframe> tailMap = map.tailMap(length + 0.0000001);
                if (!tailMap.isEmpty()) {
                    throw new RuntimeException();
                }
            }
            return new KeyframeAnimationData(rotationKeyframes, positionKeyframes, scaleKeyframes, length);
        }
    }
}
