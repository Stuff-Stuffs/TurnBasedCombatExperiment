package io.github.stuff_stuffs.tbcexgui.client.widget;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

public abstract class AbstractWidget implements Widget {
    private double screenWidth, screenHeight;
    private int pixelWidth, pixelHeight;
    private double verticalPixel = 1 / 480d;
    private double horizontalPixel = 1 / 640d;

    @Override
    public void resize(final double width, final double height, int pixelWidth, int pixelHeight) {
        screenWidth = width;
        screenHeight = height;
        this.pixelWidth = pixelWidth;
        this.pixelHeight = pixelHeight;
        horizontalPixel = 1 / (double) pixelWidth;
        verticalPixel = 1 / (double) pixelHeight;
        while (horizontalPixel < 0.005) {
            horizontalPixel = horizontalPixel * 2;
        }
        while (verticalPixel < 0.005) {
            verticalPixel = verticalPixel * 2;
        }
        if (horizontalPixel < verticalPixel / 2d) {
            double inc = 1;
            while (inc * horizontalPixel < verticalPixel) {
                inc++;
            }
            horizontalPixel = inc * horizontalPixel;
        } else if (verticalPixel < horizontalPixel / 2d) {
            double inc = 1;
            while (inc * verticalPixel < horizontalPixel) {
                inc++;
            }
            verticalPixel = inc * verticalPixel;
        }
    }

    public double getVerticalPixel() {
        return verticalPixel;
    }

    public double getHorizontalPixel() {
        return horizontalPixel;
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

    protected static void renderRectangle(final MatrixStack matrices, final double x, final double y, final double width, final double height, final Sprite sprite, final int colour, final VertexConsumer consumer) {
        final Matrix4f model = matrices.peek().getModel();
        final int alpha = (colour >> 24) & 0xff;
        final int red = (colour >> 16) & 0xff;
        final int green = (colour >> 8) & 0xff;
        final int blue = (colour) & 0xff;
        consumer.vertex(model, (float) (x + width), (float) y, 0).color(red, green, blue, alpha).texture(sprite.getMaxU(), sprite.getMinV()).next();
        consumer.vertex(model, (float) x, (float) y, 0).color(red, green, blue, alpha).texture(sprite.getMinU(), sprite.getMinV()).next();
        consumer.vertex(model, (float) x, (float) (y + height), 0).color(red, green, blue, alpha).texture(sprite.getMinU(), sprite.getMaxV()).next();
        consumer.vertex(model, (float) (x + width), (float) (y + height), 0).color(red, green, blue, alpha).texture(sprite.getMaxU(), sprite.getMaxV()).next();
    }
}
