package io.github.stuff_stuffs.tbcexgui.client.widget;

public class BasicWidgetPosition implements WidgetPosition {
    private final double x;
    private final double y;
    private final double z;

    public BasicWidgetPosition(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getZ() {
        return z;
    }

    public BasicWidgetPosition withX(final double x) {
        return new BasicWidgetPosition(x, y, z);
    }

    public BasicWidgetPosition withY(final double y) {
        return new BasicWidgetPosition(x, y, z);
    }

    public BasicWidgetPosition withZ(final double z) {
        return new BasicWidgetPosition(x, y, z);
    }
}
