package io.github.stuff_stuffs.tbcexutil.client;

import io.github.stuff_stuffs.tbcexutil.common.Vec2d;
import io.github.stuff_stuffs.tbcexutil.common.colour.Colour;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class RenderUtil {
    public static void renderRectangle(final MatrixStack matrices, final double x, final double y, final double width, final double height, final Sprite sprite, final Colour colour, final int alpha, final VertexConsumer consumer) {
        renderRectangle(matrices, x, y, width, height, sprite, colour, alpha, colour, alpha, colour, alpha, colour, alpha, consumer);
    }

    public static void renderRectangle(final MatrixStack matrices, final double x, final double y, final double width, final double height, final Sprite sprite, final Colour topLeftColour, final int topLeftAlpha, final Colour bottomLeftColour, final int bottomLeftAlpha, final Colour topRightColour, final int topRightAlpha, final Colour bottomRightColour, final int bottomRightAlpha, final VertexConsumer consumer) {
        colour(position(consumer, x + width, y, 0, matrices), topRightColour, topRightAlpha).texture(sprite.getMaxU(), sprite.getMinV()).next();
        colour(position(consumer, x, y, 0, matrices), topLeftColour, topLeftAlpha).texture(sprite.getMinU(), sprite.getMinV()).next();
        colour(position(consumer, x, y + height, 0, matrices), bottomLeftColour, bottomLeftAlpha).texture(sprite.getMinU(), sprite.getMaxV()).next();
        colour(position(consumer, x + width, y + height, 0, matrices), bottomRightColour, bottomRightAlpha).texture(sprite.getMaxU(), sprite.getMaxV()).next();
    }

    public static void renderRectangle(final MatrixStack matrices, final double x, final double y, final double width, final double height, final Colour colour, final int alpha, final VertexConsumer consumer) {
        renderRectangle(matrices, x, y, width, height, colour, alpha, colour, alpha, colour, alpha, colour, alpha, consumer);
    }

    public static void renderRectangle(final MatrixStack matrices, final double x, final double y, final double width, final double height, final Colour topLeftColour, final int topLeftAlpha, final Colour bottomLeftColour, final int bottomLeftAlpha, final Colour topRightColour, final int topRightAlpha, final Colour bottomRightColour, final int bottomRightAlpha, final VertexConsumer consumer) {
        colour(position(consumer, x + width, y, 0, matrices), topRightColour, topRightAlpha).next();
        colour(position(consumer, x, y, 0, matrices), topLeftColour, topLeftAlpha).next();
        colour(position(consumer, x, y + height, 0, matrices), bottomLeftColour, bottomLeftAlpha).next();
        colour(position(consumer, x + width, y + height, 0, matrices), bottomRightColour, bottomRightAlpha).next();
    }

    public static VertexConsumer colour(final VertexConsumer vertexConsumer, final Colour colour, final int a) {
        return colour(vertexConsumer, colour.pack(a));
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
        return uv(vertexConsumer, uv.x, uv.y);
    }

    public static VertexConsumer uv(final VertexConsumer vertexConsumer, final double u, final double v) {
        return vertexConsumer.texture((float) u, (float) v);
    }

    public static VertexConsumer lineNormal(final VertexConsumer vertexConsumer, final Vec3d first, final Vec3d second, final MatrixStack matrices) {
        Vec3d norm = second.subtract(first);
        norm = norm.multiply(MathHelper.fastInverseSqrt(norm.lengthSquared()));
        vertexConsumer.normal(matrices.peek().getNormal(), (float) norm.x, (float) norm.y, (float) norm.z);
        return vertexConsumer;
    }

    public static void drawBox(final MatrixStack matrices, final VertexConsumer vertexConsumer, final Box box, final Colour colour) {
        drawBox(matrices, vertexConsumer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, colour);
    }

    public static void drawBox(final MatrixStack matrices, final VertexConsumer vertexConsumer, final double x1, final double y1, final double z1, final double x2, final double y2, final double z2, final Colour colour) {
        final int c = colour.pack(255);
        final float red = ((c >> 16) & 0xFF) / 255.0F;
        final float green = ((c >> 8) & 0xFF) / 255.0F;
        final float blue = (c & 0xFF) / 255.0F;
        final float alpha = ((c >> 24) & 0xFF) / 255.0F;
        WorldRenderer.drawBox(matrices, vertexConsumer, x1, y1, z1, x2, y2, z2, red, green, blue, alpha, red, green, blue);
    }
}
