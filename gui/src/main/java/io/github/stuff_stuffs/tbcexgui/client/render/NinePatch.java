package io.github.stuff_stuffs.tbcexgui.client.render;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.Locale;
import java.util.Map;

import static io.github.stuff_stuffs.tbcexutil.client.RenderUtil.renderRectangle;

public final class NinePatch {
    public static void render(final Map<Part, Sprite> spriteMap, final double x, final double y, final double width, final double height, final double pixelWidth, final double pixelHeight, final double borderWidth, final MatrixStack matrices, final VertexConsumer consumer) {
        render(spriteMap, x, y, width, height, pixelWidth, pixelHeight, borderWidth, 0xffffffff, matrices, consumer);
    }

    public static void render(final Map<Part, Sprite> spriteMap, final double x, final double y, final double width, final double height, final double pixelWidth, final double pixelHeight, final double borderWidth, final int colour, final MatrixStack matrices, final VertexConsumer consumer) {
        //top left
        renderRectangle(
                matrices,
                x,
                y,
                pixelWidth * borderWidth + 0.001,
                pixelHeight * borderWidth + 0.001,
                spriteMap.get(Part.TOP_LEFT),
                colour,
                consumer
        );
        //top middle
        renderRectangle(
                matrices,
                x + pixelWidth * borderWidth,
                y,
                width - pixelWidth * 2 * borderWidth + 0.001,
                pixelHeight * borderWidth + 0.001,
                spriteMap.get(Part.TOP_MIDDLE),
                colour,
                consumer
        );
        //top right
        renderRectangle(
                matrices,
                x + width - pixelWidth * borderWidth,
                y,
                pixelWidth * borderWidth + 0.001,
                pixelHeight * borderWidth + 0.001,
                spriteMap.get(Part.TOP_RIGHT),
                colour,
                consumer
        );
        //left
        renderRectangle(
                matrices,
                x,
                y + pixelHeight * borderWidth,
                pixelWidth * borderWidth + 0.001,
                height - pixelHeight * 2 * borderWidth + 0.001,
                spriteMap.get(Part.MIDDLE_LEFT),
                colour,
                consumer
        );
        //middle
        renderRectangle(
                matrices,
                x + pixelWidth * borderWidth,
                y + pixelHeight * borderWidth,
                width - pixelWidth * 2 * borderWidth + 0.001,
                height - pixelHeight * 2 * borderWidth + 0.001,
                spriteMap.get(Part.MIDDLE_MIDDLE),
                colour,
                consumer
        );
        //right
        renderRectangle(
                matrices,
                x + width - pixelWidth * borderWidth,
                y + pixelHeight * borderWidth,
                pixelWidth * borderWidth + 0.001,
                height - pixelHeight * 2 * borderWidth + 0.001,
                spriteMap.get(Part.MIDDLE_RIGHT),
                colour,
                consumer
        );

        //bottom left
        renderRectangle(
                matrices,
                x,
                y + height - pixelHeight * borderWidth,
                pixelWidth * borderWidth + 0.001,
                pixelHeight * borderWidth + 0.001,
                spriteMap.get(Part.BOTTOM_LEFT),
                colour,
                consumer
        );
        //bottom middle
        renderRectangle(
                matrices,
                x + pixelWidth * borderWidth,
                y + height - pixelHeight * borderWidth,
                width - pixelWidth * 2 * borderWidth + 0.001,
                pixelHeight * borderWidth + 0.001,
                spriteMap.get(Part.BOTTOM_MIDDLE),
                colour,
                consumer
        );
        //bottom right
        renderRectangle(
                matrices,
                x + width - pixelWidth * borderWidth,
                y + height - pixelHeight * borderWidth,
                pixelWidth * borderWidth + 0.001,
                pixelHeight * borderWidth + 0.001,
                spriteMap.get(Part.BOTTOM_RIGHT),
                colour,
                consumer
        );
    }

    public enum Part {
        TOP_LEFT,
        TOP_MIDDLE,
        TOP_RIGHT,
        MIDDLE_LEFT,
        MIDDLE_MIDDLE,
        MIDDLE_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_MIDDLE,
        BOTTOM_RIGHT;
        private final String path;

        Part() {
            path = "/" + name().toLowerCase(Locale.ROOT);
        }

        public Identifier append(final Identifier identifier) {
            return new Identifier(identifier.getNamespace(), identifier.getPath() + path);
        }
    }

    private NinePatch() {
    }
}
