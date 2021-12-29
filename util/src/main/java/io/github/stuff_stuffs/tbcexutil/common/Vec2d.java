package io.github.stuff_stuffs.tbcexutil.common;

public class Vec2d {
    public static final Vec2d ZERO = new Vec2d(0, 0);
    public final double x;
    public final double y;

    public Vec2d(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    public Vec2d add(final Vec2d o) {
        return new Vec2d(x + o.x, y + o.y);
    }

    public Vec2d multiply(final Vec2d o) {
        return new Vec2d(x * o.x, y * o.y);
    }

    public Vec2d scale(final double factor) {
        return new Vec2d(x * factor, y * factor);
    }

    public double dot(final Vec2d o) {
        return x * o.x + y * o.y;
    }

    public Vec2d subtract(Vec2d o) {
        return new Vec2d(x - o.x, y - o.y);
    }

    @Override
    public String toString() {
        return "Vec2d{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
