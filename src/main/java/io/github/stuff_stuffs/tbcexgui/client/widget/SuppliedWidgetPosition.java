package io.github.stuff_stuffs.tbcexgui.client.widget;

import java.util.function.DoubleSupplier;

public final class SuppliedWidgetPosition implements WidgetPosition {
    private final DoubleSupplier x;
    private final DoubleSupplier y;
    private final DoubleSupplier z;
    private final DoubleSupplier scale;

    public SuppliedWidgetPosition(final double x, final double y, final double z) {
        this(() -> x, () -> y, () -> z);
    }

    public SuppliedWidgetPosition(final double x, final double y, final double z, final double scale) {
        this(() -> x, () -> y, () -> z, () -> scale);
    }

    public SuppliedWidgetPosition(final DoubleSupplier x, final DoubleSupplier y, final DoubleSupplier z) {
        this(x, y, z, () -> 1);
    }

    public SuppliedWidgetPosition(final DoubleSupplier x, final DoubleSupplier y, final DoubleSupplier z, final DoubleSupplier scale) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.scale = scale;
    }

    @Override
    public double getX() {
        return x.getAsDouble() * getScale();
    }

    @Override
    public double getRawX() {
        return x.getAsDouble();
    }

    @Override
    public double getY() {
        return y.getAsDouble() * getScale();
    }

    @Override
    public double getRawY() {
        return y.getAsDouble();
    }

    @Override
    public double getZ() {
        return z.getAsDouble() * getScale();
    }

    @Override
    public double getRawZ() {
        return z.getAsDouble();
    }

    @Override
    public double getScale() {
        return scale.getAsDouble();
    }
}
