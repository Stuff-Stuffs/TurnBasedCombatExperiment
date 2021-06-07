package io.github.stuff_stuffs.tbcexgui.client.util;

public final class Rect2d {
    public final double minX, minY, maxX, maxY;

    public Rect2d(final double minX, final double minY, final double maxX, final double maxY) {
        if (minX > maxX || minY > maxY) {
            throw new IllegalArgumentException();
        }
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public boolean isIn(final double x, final double y) {
        return minX <= x && minY <= y && x <= maxX && y <= maxY;
    }
}
