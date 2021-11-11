package io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.util.BattleShapeCache;
import io.github.stuff_stuffs.tbcexutil.common.MathUtil;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public final class ProjectileUtil {
    public static Stream<LaunchInfo> getLaunchAngles(final Vec3d startPos, final double velocity, final Box boxTarget) {
        final double g = 0.01;
        final Vec3d[] targets = MathUtil.getPoints(boxTarget);
        final List<LaunchInfo> list = new ArrayList<>(targets.length * 2);
        final double v2 = velocity * velocity;
        final double v4 = v2 * v2;
        for (final Vec3d target : targets) {
            final Vec3d translatedTarget = target.subtract(startPos);
            final double distSq = translatedTarget.x * translatedTarget.x + translatedTarget.z * translatedTarget.z;
            double m = v4 - g * (g * distSq + 2 * translatedTarget.y * v2);
            if (m > 0) {
                m = Math.sqrt(m);
                final double inverseSqrt = MathHelper.fastInverseSqrt(distSq);
                final Vec3d normalized = translatedTarget.multiply(inverseSqrt);
                final double dist = distSq * inverseSqrt;
                final double p = (v2 + m) / (g * dist);
                final double n = (v2 - m) / (g * dist);
                final double yaw = MathHelper.atan2(translatedTarget.z, translatedTarget.x);
                final double pTheta = Math.atan(p);
                final double nTheta = Math.atan(n);
                list.add(new LaunchInfo(yaw, pTheta, velocity, translatedTarget, normalized));
                list.add(new LaunchInfo(yaw, nTheta, velocity, translatedTarget, normalized));
            } else if (m == 0) {
                final double inverseSqrt = MathHelper.fastInverseSqrt(distSq);
                final Vec3d normalized = translatedTarget.multiply(inverseSqrt);
                final double dist = distSq * inverseSqrt;
                final double p = v2 / (g * dist);
                final double yaw = MathHelper.atan2(translatedTarget.z, translatedTarget.x);
                final double pTheta = Math.atan(p);
                list.add(new LaunchInfo(yaw, pTheta, velocity, translatedTarget, normalized));
            }
        }
        return list.stream();
    }

    public static Function<LaunchInfo, List<Vec3d>> createArcFunction(final Vec3d startPos) {
        return launchInfo -> {
            final double cosYaw = MathHelper.cos((float) launchInfo.yaw);
            final double sinYaw = MathHelper.sin((float) launchInfo.yaw);
            final double cosAngle = MathHelper.cos((float) launchInfo.pitch);
            final double sinAngle = MathHelper.sin((float) launchInfo.pitch);
            final double length = launchInfo.unNormalizedDiff.horizontalLength();
            final double maxT = length / (launchInfo.velocity * cosAngle);
            final List<Vec3d> arc = new ArrayList<>((int) Math.ceil(Math.abs(maxT)));
            double i = 0;
            while (i < Math.abs(maxT)) {
                final Vec3d point = new Vec3d(startPos.x + i * launchInfo.velocity * cosAngle * cosYaw, startPos.y + i * launchInfo.velocity * sinAngle, startPos.z + i * launchInfo.velocity * cosAngle * sinYaw);
                arc.add(point);
                i += Math.min(1, maxT - i);
            }
            return arc;
        };
    }

    public static boolean raycastArc(final List<Vec3d> arc, final BattleShapeCache cache, final BattleParticipantHandle... exclusions) {
        for (int i = 0; i < arc.size() - 1; i++) {
            final Vec3d start = arc.get(i);
            final Vec3d end = arc.get(i + 1);
            if (!cache.rayCast(start, end, exclusions)) {
                return false;
            }
        }
        return true;
    }

    public record LaunchInfo(double yaw, double pitch, double velocity, Vec3d unNormalizedDiff, Vec3d normalizedDiff) {
    }

    private ProjectileUtil() {
    }
}
