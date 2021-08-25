package io.github.stuff_stuffs.tbcexanimation.client.animation;

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
    private final boolean loop;

    private KeyframeAnimationData(final Map<String, Double2ObjectSortedMap<RotationKeyframe>> rotationKeyframes, final Map<String, Double2ObjectSortedMap<VecKeyframe>> positionKeyframes, final Map<String, Double2ObjectSortedMap<VecKeyframe>> scaleKeyframes, final double length, final boolean loop) {
        this.rotationKeyframes = rotationKeyframes;
        this.positionKeyframes = positionKeyframes;
        this.scaleKeyframes = scaleKeyframes;
        this.length = length;
        this.loop = loop;
    }

    public double getLength() {
        return length;
    }

    public boolean isLooped() {
        return loop;
    }

    public DoubleQuaternion getRotation(final String bone, final DoubleQuaternion start, final double t) {
        if (!rotationKeyframes.containsKey(bone)) {
            return start;
        }
        final Double2ObjectSortedMap<RotationKeyframe> keyframes = rotationKeyframes.get(bone);
        return interp(new RotationKeyframe(start, Easing.easeOutQuad, 0), keyframes, t, ((start1, end, t1) -> DoubleQuaternion.slerp(t1, start1, end)));
    }

    public Vec3d getPosition(final String bone, final Vec3d start, final double t) {
        if (!rotationKeyframes.containsKey(bone)) {
            return start;
        }
        final Double2ObjectSortedMap<VecKeyframe> keyframes = positionKeyframes.get(bone);
        return interp(new VecKeyframe(start, Easing.easeOutQuad, 0), keyframes, t, ((start1, end, t1) -> start1.add(end).multiply(0.5)));
    }

    public Vec3d getScale(final String bone, final Vec3d start, final double t) {
        if (!rotationKeyframes.containsKey(bone)) {
            return start;
        }
        final Double2ObjectSortedMap<VecKeyframe> keyframes = scaleKeyframes.get(bone);
        return interp(new VecKeyframe(start, Easing.easeOutQuad, 0), keyframes, t, ((start1, end, t1) -> start1.add(end).multiply(0.5)));
    }

    private <T> T interp(final Keyframe<T> start, final Double2ObjectSortedMap<? extends Keyframe<T>> keyframes, final double t, final Interpolator<T> interpolator) {
        if (loop && t > length) {
            final Keyframe<T> infimum = Objects.requireNonNullElseGet(getInfimum(keyframes, t), () -> getInfimum(keyframes, Double.MAX_VALUE));
            final Keyframe<T> supremum = Objects.requireNonNullElseGet(getSupremum(keyframes, t), () -> getSupremum(keyframes, -Double.MAX_VALUE));
            final double offset = infimum.getStartTime();
            final double duration;
            if (supremum.getStartTime() < infimum.getStartTime()) {
                duration = supremum.getStartTime() + infimum.getStartTime() - length;
            } else {
                duration = supremum.getStartTime() - infimum.getStartTime();
            }
            double o = t % length;
            if (o < infimum.getStartTime()) {
                o += length;
            }
            final double normalized = (o - offset) / duration;
            final double progress = supremum.getEasing().apply(normalized);
            return interpolator.interp(infimum.getValue(), supremum.getValue(), progress);
        } else if (loop) {
            final Keyframe<T> infimum = Objects.requireNonNullElse(getInfimum(keyframes, t), start);
            final Keyframe<T> supremum = Objects.requireNonNullElseGet(getSupremum(keyframes, t), () -> getSupremum(keyframes, -Double.MAX_VALUE));
            final double offset = infimum.getStartTime();
            final double duration;
            if (supremum.getStartTime() < infimum.getStartTime()) {
                duration = supremum.getStartTime() + infimum.getStartTime() - length;
            } else {
                duration = supremum.getStartTime() - infimum.getStartTime();
            }
            double o = t % length;
            if (o < infimum.getStartTime()) {
                o += length;
            }
            final double normalized = (o - offset) / duration;
            final double progress = supremum.getEasing().apply(normalized);
            return interpolator.interp(infimum.getValue(), supremum.getValue(), progress);
        } else {
            final Keyframe<T> infimum = Objects.requireNonNullElse(getInfimum(keyframes, t), start);
            final Keyframe<T> supremum = getSupremum(keyframes, t);
            if (supremum == null) {
                return infimum.getValue();
            }
            final double normalized = (t - infimum.getStartTime()) / (supremum.getStartTime()) - infimum.getStartTime();
            if (normalized <= 0) {
                return infimum.getValue();
            }
            if (normalized >= 1) {
                return supremum.getValue();
            }
            final double progress = supremum.getEasing().apply(normalized);
            return interpolator.interp(infimum.getValue(), supremum.getValue(), progress);
        }
    }

    private static <T> @Nullable Keyframe<T> getInfimum(final Double2ObjectSortedMap<? extends Keyframe<T>> keyframes, final double t) {
        final Double2ObjectSortedMap<? extends Keyframe<T>> headMap = keyframes.headMap(t);
        if (headMap.isEmpty()) {
            return null;
        } else {
            return headMap.get(headMap.lastDoubleKey());
        }
    }

    private static <T> @Nullable Keyframe<T> getSupremum(final Double2ObjectSortedMap<? extends Keyframe<T>> keyframes, final double t) {
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

    public static final class Builder {
        private final Map<String, Double2ObjectSortedMap<RotationKeyframe>> rotationKeyframes = new Object2ReferenceOpenHashMap<>();
        private final Map<String, Double2ObjectSortedMap<VecKeyframe>> positionKeyframes = new Object2ReferenceOpenHashMap<>();
        private final Map<String, Double2ObjectSortedMap<VecKeyframe>> scaleKeyframes = new Object2ReferenceOpenHashMap<>();
        private boolean built = false;

        private Builder() {
        }

        public Builder addRotationKeyframe(String bone, DoubleQuaternion rotation, Easing easing, double time) {
            if(built) {
                throw new RuntimeException();
            }
            if(!rotationKeyframes.computeIfAbsent(bone, b -> new Double2ObjectAVLTreeMap<>()).tailMap(time).isEmpty()) {
                throw new RuntimeException();
            }
            rotationKeyframes.get(bone).put(time, new RotationKeyframe(rotation, easing, time));
            return this;
        }

        public Builder addPositionKeyframe(String bone, Vec3d pos, Easing easing, double time) {
            if(built) {
                throw new RuntimeException();
            }
            if(!positionKeyframes.computeIfAbsent(bone, b -> new Double2ObjectAVLTreeMap<>()).tailMap(time).isEmpty()) {
                throw new RuntimeException();
            }
            positionKeyframes.get(bone).put(time, new VecKeyframe(pos, easing, time));
            return this;
        }

        public Builder addScaleKeyframe(String bone, Vec3d scale, Easing easing, double time) {
            if(built) {
                throw new RuntimeException();
            }
            if(!scaleKeyframes.computeIfAbsent(bone, b -> new Double2ObjectAVLTreeMap<>()).tailMap(time).isEmpty()) {
                throw new RuntimeException();
            }
            scaleKeyframes.get(bone).put(time, new VecKeyframe(scale, easing, time));
            return this;
        }

        public KeyframeAnimationData build(double length, boolean loop) {
            if(built) {
                throw new RuntimeException();
            }
            built = true;
            for (Double2ObjectSortedMap<RotationKeyframe> map : rotationKeyframes.values()) {
                final Double2ObjectSortedMap<RotationKeyframe> tailMap = map.tailMap(length+0.0000001);
                if(!tailMap.isEmpty()) {
                    throw new RuntimeException();
                }
            }
            for (Double2ObjectSortedMap<VecKeyframe> map : positionKeyframes.values()) {
                final Double2ObjectSortedMap<VecKeyframe> tailMap = map.tailMap(length+0.0000001);
                if(!tailMap.isEmpty()) {
                    throw new RuntimeException();
                }
            }
            for (Double2ObjectSortedMap<VecKeyframe> map : scaleKeyframes.values()) {
                final Double2ObjectSortedMap<VecKeyframe> tailMap = map.tailMap(length+0.0000001);
                if(!tailMap.isEmpty()) {
                    throw new RuntimeException();
                }
            }
            return new KeyframeAnimationData(rotationKeyframes, positionKeyframes, scaleKeyframes, length, loop);
        }
    }
}
