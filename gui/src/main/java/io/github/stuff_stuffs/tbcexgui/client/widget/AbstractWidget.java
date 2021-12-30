package io.github.stuff_stuffs.tbcexgui.client.widget;

public abstract class AbstractWidget implements Widget {
    private double width, height;
    private int pixelWidth, pixelHeight;

    @Override
    public void resize(final double width, final double height, final int pixelWidth, final int pixelHeight) {
        this.width = width;
        this.height = height;
        this.pixelWidth = pixelWidth;
        this.pixelHeight = pixelHeight;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public int getPixelWidth() {
        return pixelWidth;
    }

    public int getPixelHeight() {
        return pixelHeight;
    }
}
