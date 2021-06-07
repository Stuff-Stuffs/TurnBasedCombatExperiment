package io.github.stuff_stuffs.tbcexgui.client.widget;

public class MutableWidgetPosition implements WidgetPosition {
    private final double x,y,z,scale;

    public MutableWidgetPosition(double x, double y, double z, double scale) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.scale = scale;
    }

    @Override
    public double getX() {
        return x * scale;
    }

    @Override
    public double getRawX() {
        return x;
    }

    @Override
    public double getY() {
        return y*scale;
    }

    @Override
    public double getRawY() {
        return y;
    }

    @Override
    public double getZ() {
        return z*scale;
    }

    @Override
    public double getRawZ() {
        return z;
    }

    @Override
    public double getScale() {
        return scale;
    }

    public MutableWidgetPosition withX(double x) {
        return new MutableWidgetPosition(x,y,z,scale);
    }

    public MutableWidgetPosition withY(double y) {
        return new MutableWidgetPosition(x,y,z,scale);
    }

    public MutableWidgetPosition withZ(double z) {
        return new MutableWidgetPosition(x,y,z,scale);
    }

    public MutableWidgetPosition withScale(double scale) {
        return new MutableWidgetPosition(x,y,z,scale);
    }
}
