package io.github.stuff_stuffs.tbcexutil.client;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

public final class RenderUtil {
    public static void renderRectangle(final MatrixStack matrices, final double x, final double y, final double width, final double height, final Sprite sprite, final int colour, final VertexConsumer consumer) {
        renderRectangle(matrices, x, y, width, height, sprite, colour, colour, colour, colour, consumer);
    }

    public static void renderRectangle(final MatrixStack matrices, final double x, final double y, final double width, final double height, final Sprite sprite, final int topLeftColour, final int bottomLeftColour, final int topRightColour, final int bottomRightColour, final VertexConsumer consumer) {
        final Matrix4f model = matrices.peek().getModel();
        colour(consumer.vertex(model, (float) (x + width), (float) y, 0), topRightColour).texture(sprite.getMaxU(), sprite.getMinV()).next();
        colour(consumer.vertex(model, (float) x, (float) y, 0), topLeftColour).texture(sprite.getMinU(), sprite.getMinV()).next();
        colour(consumer.vertex(model, (float) x, (float) (y + height), 0), bottomLeftColour).texture(sprite.getMinU(), sprite.getMaxV()).next();
        colour(consumer.vertex(model, (float) (x + width), (float) (y + height), 0), bottomRightColour).texture(sprite.getMaxU(), sprite.getMaxV()).next();
    }

    public static void renderRectangle(final MatrixStack matrices, final double x, final double y, final double width, final double height, final int colour, final VertexConsumer consumer) {
        renderRectangle(matrices, x, y, width, height, colour, colour, colour, colour, consumer);
    }

    public static void renderRectangle(final MatrixStack matrices, final double x, final double y, final double width, final double height, final int topLeftColour, final int bottomLeftColour, final int topRightColour, final int bottomRightColour, final VertexConsumer consumer) {
        final Matrix4f model = matrices.peek().getModel();
        colour(consumer.vertex(model, (float) (x + width), (float) y, 0), topRightColour).next();
        colour(consumer.vertex(model, (float) x, (float) y, 0), topLeftColour).next();
        colour(consumer.vertex(model, (float) x, (float) (y + height), 0), bottomLeftColour).next();
        colour(consumer.vertex(model, (float) (x + width), (float) (y + height), 0), bottomRightColour).next();
    }

    public static VertexConsumer colour(final VertexConsumer vertexConsumer, final int colour) {
        final int alpha = (colour >> 24) & 0xff;
        final int red = (colour >> 16) & 0xff;
        final int green = (colour >> 8) & 0xff;
        final int blue = (colour) & 0xff;
        vertexConsumer.color(red, green, blue, alpha);
        return vertexConsumer;
    }

    private RenderUtil() {
    }
}
