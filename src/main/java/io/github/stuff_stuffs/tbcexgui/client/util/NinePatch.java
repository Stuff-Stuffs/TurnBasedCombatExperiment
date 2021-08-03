package io.github.stuff_stuffs.tbcexgui.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.Locale;
import java.util.Map;

import static io.github.stuff_stuffs.tbcexgui.client.util.RenderUtil.renderRectangle;

public final class NinePatch {
    public static void render(final Map<Part, Sprite> spriteMap, final double x, final double y, final double width, final double height, final double pixelWidth, final double pixelHeight, final double borderWidth, final MatrixStack matrices) {
        render(spriteMap, x, y, width, height, pixelWidth, pixelHeight, borderWidth, 4, 0xffffffff, matrices);
    }

    public static void render(final Map<Part, Sprite> spriteMap, final double x, final double y, final double width, final double height, final double pixelWidth, final double pixelHeight, final double borderWidth, final int spriteSize, final int colour, final MatrixStack matrices) {
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 10f);
        final BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        //top left
        renderRectangle(
                matrices,
                x,
                y,
                pixelWidth * spriteSize * borderWidth,
                pixelHeight * spriteSize * borderWidth,
                spriteMap.get(Part.TOP_LEFT),
                colour,
                bufferBuilder
        );
        //top middle
        renderRectangle(
                matrices,
                x + pixelWidth * spriteSize * borderWidth,
                y,
                width - pixelWidth * 2 * spriteSize * borderWidth,
                pixelHeight * spriteSize * borderWidth,
                spriteMap.get(Part.TOP_MIDDLE),
                colour,
                bufferBuilder
        );
        //top right
        renderRectangle(
                matrices,
                x + width - spriteSize * pixelWidth * borderWidth,
                y,
                pixelWidth * spriteSize * borderWidth,
                pixelHeight * spriteSize * borderWidth,
                spriteMap.get(Part.TOP_RIGHT),
                colour,
                bufferBuilder
        );
        //left
        renderRectangle(
                matrices,
                x,
                y + pixelHeight * spriteSize * borderWidth,
                pixelWidth * spriteSize * borderWidth,
                height - pixelHeight * 2 * spriteSize * borderWidth,
                spriteMap.get(Part.MIDDLE_LEFT),
                colour,
                bufferBuilder
        );
        //middle
        renderRectangle(
                matrices,
                x + pixelWidth * spriteSize * borderWidth,
                y + pixelHeight * spriteSize * borderWidth,
                width - pixelWidth * 2 * spriteSize * borderWidth,
                height - pixelHeight * 2 * spriteSize * borderWidth,
                spriteMap.get(Part.MIDDLE_MIDDLE),
                colour,
                bufferBuilder
        );
        //right
        renderRectangle(
                matrices,
                x + width - pixelWidth * spriteSize * borderWidth,
                y + pixelHeight * spriteSize * borderWidth,
                pixelWidth * spriteSize * borderWidth,
                height - pixelHeight * 2 * spriteSize * borderWidth,
                spriteMap.get(Part.MIDDLE_RIGHT),
                colour,
                bufferBuilder
        );

        //bottom left
        renderRectangle(
                matrices,
                x,
                y + height - pixelHeight * spriteSize * borderWidth,
                pixelWidth * spriteSize * borderWidth,
                pixelHeight * spriteSize * borderWidth,
                spriteMap.get(Part.BOTTOM_LEFT),
                colour,
                bufferBuilder
        );
        //bottom middle
        renderRectangle(
                matrices,
                x + pixelWidth * spriteSize * borderWidth,
                y + height - pixelHeight * spriteSize * borderWidth,
                width - pixelWidth * 2 * spriteSize * borderWidth,
                pixelHeight * spriteSize * borderWidth,
                spriteMap.get(Part.BOTTOM_MIDDLE),
                colour,
                bufferBuilder
        );
        //bottom right
        renderRectangle(
                matrices,
                x + width - spriteSize * pixelWidth * borderWidth,
                y + height - pixelHeight * spriteSize * borderWidth,
                pixelWidth * spriteSize * borderWidth,
                pixelHeight * spriteSize * borderWidth,
                spriteMap.get(Part.BOTTOM_RIGHT),
                colour,
                bufferBuilder
        );
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
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
