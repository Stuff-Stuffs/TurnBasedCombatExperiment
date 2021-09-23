package io.github.stuff_stuffs.tbcexutil.client;

import io.github.stuff_stuffs.tbcexutil.common.Vec2d;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public final class RenderUtil {
    public static void renderRectangle(final MatrixStack matrices, final double x, final double y, final double width, final double height, final Sprite sprite, final int colour, final VertexConsumer consumer) {
        renderRectangle(matrices, x, y, width, height, sprite, colour, colour, colour, colour, consumer);
    }

    public static void renderRectangle(final MatrixStack matrices, final double x, final double y, final double width, final double height, final Sprite sprite, final int topLeftColour, final int bottomLeftColour, final int topRightColour, final int bottomRightColour, final VertexConsumer consumer) {
        colour(position(consumer, x + width, y, 0, matrices), topRightColour).texture(sprite.getMaxU(), sprite.getMinV()).next();
        colour(position(consumer, x, y, 0, matrices), topLeftColour).texture(sprite.getMinU(), sprite.getMinV()).next();
        colour(position(consumer, x, y + height, 0, matrices), bottomLeftColour).texture(sprite.getMinU(), sprite.getMaxV()).next();
        colour(position(consumer, x + width, y + height, 0, matrices), bottomRightColour).texture(sprite.getMaxU(), sprite.getMaxV()).next();
    }

    public static void renderRectangle(final MatrixStack matrices, final double x, final double y, final double width, final double height, final int colour, final VertexConsumer consumer) {
        renderRectangle(matrices, x, y, width, height, colour, colour, colour, colour, consumer);
    }

    public static void renderRectangle(final MatrixStack matrices, final double x, final double y, final double width, final double height, final int topLeftColour, final int bottomLeftColour, final int topRightColour, final int bottomRightColour, final VertexConsumer consumer) {
        colour(position(consumer, x + width, y, 0, matrices), topRightColour).next();
        colour(position(consumer, x, y, 0, matrices), topLeftColour).next();
        colour(position(consumer, x, y + height, 0, matrices), bottomLeftColour).next();
        colour(position(consumer, x + width, y + height, 0, matrices), bottomRightColour).next();
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

    public static VertexConsumer position(final VertexConsumer vertexConsumer, final double x, final double y, final double z, final MatrixStack matrices) {
        vertexConsumer.vertex(matrices.peek().getModel(), (float) x, (float) y, (float) z);
        return vertexConsumer;
    }

    public static VertexConsumer position(final VertexConsumer vertexConsumer, final Vec3d pos, final MatrixStack matrices) {
        return position(vertexConsumer, pos.x, pos.y, pos.z, matrices);
    }

    public static VertexConsumer uv(final VertexConsumer vertexConsumer, final Vec2d uv) {
        return vertexConsumer.texture((float) uv.x, (float) uv.y);
    }
}
