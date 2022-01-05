package io.github.stuff_stuffs.tbcexgui.client.render;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiQuadEmitter;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiRenderMaterial;
import io.github.stuff_stuffs.tbcexutil.common.colour.Colour;
import io.github.stuff_stuffs.tbcexutil.common.colour.IntRgbColour;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

import java.util.Locale;
import java.util.Map;

public final class NinePatch {
    private NinePatch() {
    }

    public static void render(final Map<Part, Sprite> spriteMap, final double x, final double y, final double width, final double height, final double pixelWidth, final double pixelHeight, final double borderWidth, final GuiContext context, final GuiRenderMaterial material) {
        render(spriteMap, x, y, width, height, pixelWidth, pixelHeight, borderWidth, IntRgbColour.WHITE, 255, context, material);
    }

    public static void render(final Map<Part, Sprite> spriteMap, final double x, final double y, final double width, final double height, final double pixelWidth, final double pixelHeight, final double borderWidth, final Colour colour, final int alpha, final GuiContext context, final GuiRenderMaterial material) {
        //top left
        renderRectangle(
                context,
                material,
                x,
                y,
                pixelWidth * borderWidth + 0.001,
                pixelHeight * borderWidth + 0.001,
                spriteMap.get(Part.TOP_LEFT),
                colour,
                alpha
        );
        //top middle
        renderRectangle(
                context,
                material,
                x + pixelWidth * borderWidth,
                y,
                width - pixelWidth * 2 * borderWidth + 0.001,
                pixelHeight * borderWidth + 0.001,
                spriteMap.get(Part.TOP_MIDDLE),
                colour,
                alpha
        );
        //top right
        renderRectangle(
                context,
                material,
                x + width - pixelWidth * borderWidth,
                y,
                pixelWidth * borderWidth + 0.001,
                pixelHeight * borderWidth + 0.001,
                spriteMap.get(Part.TOP_RIGHT),
                colour,
                alpha
        );
        //left
        renderRectangle(
                context,
                material,
                x,
                y + pixelHeight * borderWidth,
                pixelWidth * borderWidth + 0.001,
                height - pixelHeight * 2 * borderWidth + 0.001,
                spriteMap.get(Part.MIDDLE_LEFT),
                colour,
                alpha
        );
        //middle
        renderRectangle(
                context,
                material,
                x + pixelWidth * borderWidth,
                y + pixelHeight * borderWidth,
                width - pixelWidth * 2 * borderWidth + 0.001,
                height - pixelHeight * 2 * borderWidth + 0.001,
                spriteMap.get(Part.MIDDLE_MIDDLE),
                colour,
                alpha
        );
        //right
        renderRectangle(
                context,
                material,
                x + width - pixelWidth * borderWidth,
                y + pixelHeight * borderWidth,
                pixelWidth * borderWidth + 0.001,
                height - pixelHeight * 2 * borderWidth + 0.001,
                spriteMap.get(Part.MIDDLE_RIGHT),
                colour,
                alpha
        );

        //bottom left
        renderRectangle(
                context,
                material,
                x,
                y + height - pixelHeight * borderWidth,
                pixelWidth * borderWidth + 0.001,
                pixelHeight * borderWidth + 0.001,
                spriteMap.get(Part.BOTTOM_LEFT),
                colour,
                alpha
        );
        //bottom middle
        renderRectangle(
                context,
                material,
                x + pixelWidth * borderWidth,
                y + height - pixelHeight * borderWidth,
                width - pixelWidth * 2 * borderWidth + 0.001,
                pixelHeight * borderWidth + 0.001,
                spriteMap.get(Part.BOTTOM_MIDDLE),
                colour,
                alpha
        );
        //bottom right
        renderRectangle(
                context,
                material,
                x + width - pixelWidth * borderWidth,
                y + height - pixelHeight * borderWidth,
                pixelWidth * borderWidth + 0.001,
                pixelHeight * borderWidth + 0.001,
                spriteMap.get(Part.BOTTOM_RIGHT),
                colour,
                alpha
        );
    }

    private static void renderRectangle(final GuiContext context, final GuiRenderMaterial material, final double x, final double y, final double width, final double height, final Sprite sprite, final Colour colour, final int alpha) {
        final GuiQuadEmitter emitter = context.getEmitter();
        emitter.renderMaterial(material);
        final int c = colour.pack(alpha);
        emitter.colour(c, c, c, c);
        emitter.pos(0, (float) (x + width), (float) y);
        emitter.sprite(0, sprite.getMaxU(), sprite.getMinV());
        emitter.pos(1, (float) x, (float) y);
        emitter.sprite(1, sprite.getMinU(), sprite.getMinV());
        emitter.pos(2, (float) x, (float) (y + height));
        emitter.sprite(2, sprite.getMinU(), sprite.getMaxV());
        emitter.pos(3, (float) (x + width), (float) (y + height));
        emitter.sprite(3, sprite.getMaxU(), sprite.getMaxV());
        emitter.emit();
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
}
