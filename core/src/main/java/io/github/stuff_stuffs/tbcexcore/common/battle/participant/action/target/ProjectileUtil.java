package io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.util.BattleShapeCache;
import io.github.stuff_stuffs.tbcexutil.common.MathUtil;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
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

    public static boolean raycastArc(final Vec3d startPos, final LaunchInfo launchInfo, final BattleShapeCache cache, final BattleParticipantHandle... exclusions) {
        final double cosYaw = MathHelper.cos((float) launchInfo.yaw);
        final double sinYaw = MathHelper.sin((float) launchInfo.yaw);
        final double cosAngle = MathHelper.cos((float) launchInfo.pitch);
        final double sinAngle = MathHelper.sin((float) launchInfo.pitch);
        final double length = launchInfo.unNormalizedDiff.horizontalLength();
        final double maxT = length / (launchInfo.velocity * cosAngle);
        double i = 0;
        while ((i + 1) < Math.abs(maxT)) {
            final double t1 = i;
            final double t2 = i + Math.signum(maxT) * 1;
            final Vec3d start = new Vec3d(startPos.x + t1 * launchInfo.velocity * cosAngle * cosYaw, startPos.y + t1 * launchInfo.velocity * sinAngle, startPos.z + t1 * launchInfo.velocity * cosAngle * sinYaw);
            final Vec3d end = new Vec3d(startPos.x + t2 * launchInfo.velocity * cosAngle * cosYaw, startPos.y + t2 * launchInfo.velocity * sinAngle, startPos.z + t2 * launchInfo.velocity * cosAngle * sinYaw);
            final boolean rayCast = cache.rayCast(start, end, exclusions);
            if (!rayCast) {
                return false;
            }
            i += 1;
        }
        return true;
    }

    public record LaunchInfo(double yaw, double pitch, double velocity, Vec3d unNormalizedDiff, Vec3d normalizedDiff) {
    }

    private ProjectileUtil() {
    }
}
