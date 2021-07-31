package io.github.stuff_stuffs.tbcexgui.client.widget;

import java.util.function.DoubleSupplier;

public final class SuppliedWidgetPosition implements WidgetPosition {
    private final DoubleSupplier x;
    private final DoubleSupplier y;
    private final DoubleSupplier z;

    public SuppliedWidgetPosition(final double x, final double y, final double z) {
        this(() -> x, () -> y, () -> z);
    }

    public SuppliedWidgetPosition(final DoubleSupplier x, final DoubleSupplier y, final DoubleSupplier z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public double getX() {
        return x.getAsDouble();
    }

    @Override
    public double getY() {
        return y.getAsDouble();
    }

    @Override
    public double getZ() {
        return z.getAsDouble();
    }
}
