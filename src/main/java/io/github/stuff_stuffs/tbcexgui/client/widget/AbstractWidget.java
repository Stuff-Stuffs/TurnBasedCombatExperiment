package io.github.stuff_stuffs.tbcexgui.client.widget;

public abstract class AbstractWidget implements Widget {
    private double screenWidth, screenHeight;
    private int pixelWidth, pixelHeight;

    @Override
    public void resize(final double width, final double height, int pixelWidth, int pixelHeight) {
        screenWidth = width;
        screenHeight = height;
        this.pixelWidth = pixelWidth;
        this.pixelHeight = pixelHeight;
    }

    public double getScreenWidth() {
        return screenWidth;
    }

    public double getScreenHeight() {
        return screenHeight;
    }

    public int getPixelWidth() {
        return pixelWidth;
    }

    public int getPixelHeight() {
        return pixelHeight;
    }
}
