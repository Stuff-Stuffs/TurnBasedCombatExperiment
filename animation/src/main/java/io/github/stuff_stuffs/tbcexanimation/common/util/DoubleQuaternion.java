package io.github.stuff_stuffs.tbcexanimation.common.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public final class DoubleQuaternion {
    public final double x;
    public final double y;
    public final double z;
    public final double w;

    public DoubleQuaternion(final double x, final double y, final double z, final double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public DoubleQuaternion(final Vec3d axis, double rotationAngle, final boolean degrees) {
        if (degrees) {
            rotationAngle = Math.toRadians(rotationAngle);
        }

        final double halfSin = Math.sin(rotationAngle / 2.0);
        x = axis.x * halfSin;
        y = axis.y * halfSin;
        z = axis.z * halfSin;
        w = Math.cos(rotationAngle / 2.0);
    }

    public DoubleQuaternion(double roll, double pitch, double yaw, boolean degrees) {
        if (degrees) {
            roll = Math.toRadians(roll);
            pitch = Math.toRadians(pitch);
            yaw = Math.toRadians(yaw);
        }

        double sr = Math.sin(0.5 * roll);
        double cr = Math.cos(0.5 * roll);
        double sp = Math.sin(0.5 * pitch);
        double cp = Math.cos(0.5 * pitch);
        double sy = Math.sin(0.5 * yaw);
        double cy = Math.cos(0.5 * yaw);

        this.w = cr * cp * cy + sr * sp * sy;
        this.x = sr * cp * cy - cr * sp * sy;
        this.y = cr * sp * cy + sr * cp * sy;
        this.z = cr * cp * sy - sr * sp * cy;
    }

    public DoubleQuaternion normalize() {
        final double length2 = x * x + y * y + z * z + w * w;
        if (Math.abs(length2 - 1) < 1.0E-3) {
            return this;
        }
        if (length2 > 1.0E-9) {
            final double invLength = MathHelper.fastInverseSqrt(length2);
            return new DoubleQuaternion(x * invLength, y * invLength, z * invLength, w * invLength);
        } else {
            return new DoubleQuaternion(0, 0, 0, 0);
        }
    }

    public Quaternion toFloatQuat() {
        return new Quaternion((float) x, (float) y, (float) z, (float) w);
    }

    public DoubleQuaternion multiply(final DoubleQuaternion other) {
        final double x = w * other.x + this.x * other.w + y * other.z - z * other.y;
        final double y = w * other.y - this.x * other.z + this.y * other.w + z * other.x;
        final double z = w * other.z + this.x * other.y - this.y * other.x + this.z * other.w;
        final double w = this.w * other.w - this.x * other.x - this.y * other.y - this.z * other.z;
        return new DoubleQuaternion(x, y, z, w);
    }

    public static double angularDistance(final DoubleQuaternion a, final DoubleQuaternion b) {
        final double t = dot(a, b);
        return Math.acos(2 * t * t - 1);
    }

    public static double dot(final DoubleQuaternion a, final DoubleQuaternion b) {
        return a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w;
    }

    public static DoubleQuaternion slerp(final double t, final DoubleQuaternion a, final DoubleQuaternion b) {
        // quaternion to return
        // Calculate angle between them.
        final double cosHalfTheta = dot(a,b);
        // if qa=qb or qa=-qb then theta = 0 and we can return qa
        if (Math.abs(cosHalfTheta) >= 1.0) {
            return a;
        }
        // Calculate temporary values.
        final double halfTheta = Math.acos(cosHalfTheta);
        final double sinHalfTheta = Math.sqrt(1.0 - cosHalfTheta * cosHalfTheta);
        // if theta = 180 degrees then result is not fully defined
        // we could rotate around any axis normal to qa or qb
        if (Math.abs(sinHalfTheta) < 0.00001) {
            return new DoubleQuaternion((a.x * 0.5 + b.x * 0.5), (a.y * 0.5 + b.y * 0.5), (a.z * 0.5 + b.z * 0.5), (a.w * 0.5 + b.w * 0.5)).normalize();
        }
        final double ratioA = Math.sin((1 - t) * halfTheta) / sinHalfTheta;
        final double ratioB = Math.sin(t * halfTheta) / sinHalfTheta;
        return new DoubleQuaternion((a.x * ratioA + b.x * ratioB), (a.y * ratioA + b.y * ratioB), (a.z * ratioA + b.z * ratioB), (a.w * ratioA + b.w * ratioB)).normalize();
    }
}
