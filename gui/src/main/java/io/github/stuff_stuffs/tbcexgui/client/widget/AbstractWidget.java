package io.github.stuff_stuffs.tbcexgui.client.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.stuff_stuffs.tbcexutil.client.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vector4f;

import java.util.List;

public abstract class AbstractWidget implements Widget {
    private double screenWidth, screenHeight;
    private int pixelWidth, pixelHeight;
    private double verticalPixel = 1 / 480d;
    private double horizontalPixel = 1 / 640d;

    @Override
    public void resize(final double width, final double height, final int pixelWidth, final int pixelHeight) {
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
        if (horizontalPixel < verticalPixel / 4d) {
            double inc = 1;
            while (inc * horizontalPixel < verticalPixel / 2) {
                inc++;
            }
            horizontalPixel = inc * horizontalPixel;
        } else if (verticalPixel < horizontalPixel / 4d) {
            double inc = 1;
            while (inc * verticalPixel < horizontalPixel / 2) {
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

    protected void renderTooltip(final MatrixStack matrices, final List<? extends TooltipComponent> components, double x, double y, final boolean move) {
        if (!components.isEmpty()) {
            final double borderThickness = 0.5;
            final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            final Tessellator tessellator = Tessellator.getInstance();
            final BufferBuilder bufferBuilder = tessellator.getBuffer();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            double maxWidth = 0.05 + 4 * horizontalPixel * borderThickness;
            double height = 4 * verticalPixel * borderThickness + 2;
            for (final TooltipComponent component : components) {
                final int componentHeight = component.getHeight();
                final TextInfo textInfo = getTextScaleClamped(component.getWidth(textRenderer), 100, 100);
                height += componentHeight;
                final double width = textInfo.textScale;
                if (width > maxWidth) {
                    maxWidth = width;
                }
            }
            height *= maxWidth;
            maxWidth += 6 * horizontalPixel;
            if (move) {
                if (maxWidth + x > getScreenWidth()) {
                    x -= maxWidth;
                }
                if (height + y > getScreenHeight()) {
                    y -= height;
                }
            }
            final int background = 0xf0100010;

            RenderUtil.renderRectangle(matrices, x, y + 1 * verticalPixel * borderThickness, maxWidth, height - 2 * verticalPixel * borderThickness, background, bufferBuilder);
            //background sides
            RenderUtil.renderRectangle(matrices, x + 1 * horizontalPixel * borderThickness, y, maxWidth - 2 * horizontalPixel * borderThickness, verticalPixel * borderThickness, background, bufferBuilder);
            RenderUtil.renderRectangle(matrices, x + 1 * horizontalPixel * borderThickness, y + height - verticalPixel * borderThickness, maxWidth - 2 * horizontalPixel * borderThickness, verticalPixel * borderThickness, background, bufferBuilder);
            //purple top and bottom
            final int topPurple = 0x505000ff;
            final int bottomPurple = 0x5028007f;
            RenderUtil.renderRectangle(matrices, x + 1 * horizontalPixel * borderThickness, y + verticalPixel * borderThickness, maxWidth - 2 * horizontalPixel * borderThickness, verticalPixel * borderThickness, topPurple, bufferBuilder);
            RenderUtil.renderRectangle(matrices, x + 1 * horizontalPixel * borderThickness, y + height - 2 * verticalPixel * borderThickness, maxWidth - 2 * horizontalPixel * borderThickness, verticalPixel * borderThickness, bottomPurple, bufferBuilder);
            //purple sides
            RenderUtil.renderRectangle(matrices, x + 1 * horizontalPixel * borderThickness, y + 2 * verticalPixel * borderThickness, horizontalPixel * borderThickness, height - 4 * verticalPixel * borderThickness, topPurple, bottomPurple, topPurple, bottomPurple, bufferBuilder);
            RenderUtil.renderRectangle(matrices, x + maxWidth - 2 * horizontalPixel * borderThickness, y + 2 * verticalPixel * borderThickness, horizontalPixel * borderThickness, height - 4 * verticalPixel * borderThickness, topPurple, bottomPurple, topPurple, bottomPurple, bufferBuilder);

            RenderSystem.enableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);

            RenderSystem.disableBlend();
            RenderSystem.enableTexture();

            final VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());

            matrices.push();
            matrices.translate(0.0D, 0.0D, 400.0D);
            double offset = 0;
            for (int v = 0; v < components.size(); ++v) {
                matrices.push();
                matrices.translate(0, offset, 0);
                matrices.translate(x + 3 * horizontalPixel, y + 3 * verticalPixel, 0);
                matrices.scale((float)maxWidth, (float) maxWidth, 1);
                final TooltipComponent component = components.get(v);
                component.drawText(textRenderer, 0, 0, matrices.peek().getModel(), immediate);
                offset += (component.getHeight() + (v == 0 ? 2 : 0)) * maxWidth;
                matrices.pop();
            }

            immediate.draw();
            for (int v = 0; v < components.size(); ++v) {
                matrices.push();
                matrices.translate(0, offset, 0);
                matrices.translate(x + 3 * horizontalPixel, y + 3 * verticalPixel, 0);
                matrices.scale((float)maxWidth, (float) maxWidth, 1);
                final TooltipComponent component = components.get(v);
                component.drawItems(textRenderer, 0, 0, matrices, MinecraftClient.getInstance().getItemRenderer(), 0, MinecraftClient.getInstance().getTextureManager());
                offset += (component.getHeight() + (v == 0 ? 2 : 0)) * maxWidth;
                matrices.pop();
            }
            matrices.pop();
        }
    }

    public void renderTooltipBackground(final double x, final double y, final double width, final double height, final MatrixStack matrices) {
        final double borderThickness = 0.5;
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        final int background = 0xF0100010;

        //center
        RenderUtil.renderRectangle(matrices, x, y + 1 * verticalPixel * borderThickness, width, height - 2 * verticalPixel * borderThickness, background, bufferBuilder);
        //background sides
        RenderUtil.renderRectangle(matrices, x + 1 * horizontalPixel * borderThickness, y, width - 2 * horizontalPixel * borderThickness, verticalPixel * borderThickness, background, bufferBuilder);
        RenderUtil.renderRectangle(matrices, x + 1 * horizontalPixel * borderThickness, y + height - verticalPixel * borderThickness, width - 2 * horizontalPixel * borderThickness, verticalPixel * borderThickness, background, bufferBuilder);
        //purple top and bottom
        final int topPurple = 0x505000FF;
        final int bottomPurple = 0x5028007F;
        RenderUtil.renderRectangle(matrices, x + 1 * horizontalPixel * borderThickness, y + verticalPixel * borderThickness, width - 2 * horizontalPixel * borderThickness, verticalPixel * borderThickness, topPurple, bufferBuilder);
        RenderUtil.renderRectangle(matrices, x + 1 * horizontalPixel * borderThickness, y + height - 2 * verticalPixel * borderThickness, width - 2 * horizontalPixel * borderThickness, verticalPixel * borderThickness, bottomPurple, bufferBuilder);
        //purple sides
        RenderUtil.renderRectangle(matrices, x + 1 * horizontalPixel * borderThickness, y + 2 * verticalPixel * borderThickness, horizontalPixel * borderThickness, height - 4 * verticalPixel * borderThickness, topPurple, bottomPurple, topPurple, bottomPurple, bufferBuilder);
        RenderUtil.renderRectangle(matrices, x + width - 2 * horizontalPixel * borderThickness, y + 2 * verticalPixel * borderThickness, horizontalPixel * borderThickness, height - 4 * verticalPixel * borderThickness, topPurple, bottomPurple, topPurple, bottomPurple, bufferBuilder);

        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public TextInfo getTextScale(final int textWidth, final double maxWidth, final double maxHeight) {
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        final double scaleFactor = MinecraftClient.getInstance().getWindow().getScaleFactor();
        final double hScale = (textWidth * 4 / (double) pixelWidth) * scaleFactor;
        final double hSize = (maxWidth / hScale) * horizontalPixel;
        final double vScale = (textRenderer.fontHeight * 2 / (double) pixelHeight) * scaleFactor;
        final double vSize = (maxHeight / vScale) * verticalPixel;

        return new TextInfo(Math.min(hSize, vSize), hScale, vScale);
    }

    public TextInfo getTextScaleClamped(final int textWidth, final double maxWidth, final double maxHeight) {
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        final double scaleFactor = MinecraftClient.getInstance().getWindow().getScaleFactor();
        final double hScale = (textWidth * 4 / (double) pixelWidth) * scaleFactor;
        final double hSize = Math.min(maxWidth / hScale, scaleFactor) * horizontalPixel;
        final double vScale = (textRenderer.fontHeight * 2 / (double) pixelHeight) * scaleFactor;
        final double vSize = Math.min(maxHeight / vScale, scaleFactor) * verticalPixel;

        return new TextInfo(Math.min(hSize, vSize), hScale, vScale);
    }

    public static class TextInfo {
        public final double textScale;
        public final double textWidth;
        public final double textHeight;

        public TextInfo(final double textScale, final double textWidth, final double textHeight) {
            this.textScale = textScale;
            this.textWidth = textWidth;
            this.textHeight = textHeight;
        }
    }

    public double widthToScaledWidth(final double width, final MatrixStack matrices) {
        final Vector4f vec = new Vector4f(0, 0, 0, 0);
        vec.transform(matrices.peek().getModel());
        final double min = vec.getX();
        vec.set((float) width, 0, 0, 0);
        vec.transform(matrices.peek().getModel());
        final double max = vec.getX();
        return max - min;
    }

    public void renderText(final MatrixStack matrices, final Text text, final boolean center, final boolean shadow, final int colour) {
        renderText(matrices, text.asOrderedText(), center, shadow, colour);
    }

    public void renderText(final MatrixStack matrices, final OrderedText text, final boolean center, final boolean shadow, final int colour) {
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        if (center) {
            final float middle = textRenderer.getWidth(text) / 2.0f;
            if (shadow) {
                textRenderer.draw(matrices, text, -middle, 0, colour);
            } else {
                textRenderer.drawWithShadow(matrices, text, -middle, 0, colour);
            }
        } else {
            if (shadow) {
                textRenderer.draw(matrices, text, 0, 0, colour);
            } else {
                textRenderer.drawWithShadow(matrices, text, 0, 0, colour);
            }
        }
    }

    public void renderFitText(final MatrixStack matrices, final Text text, final double x, final double y, final double maxWidth, final double maxHeight, final boolean shadow, final int colour) {
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        final double scale = getTextScale(textRenderer.getWidth(text), maxWidth, maxHeight).textScale;
        matrices.push();
        final double offset = (maxHeight - (scale * textRenderer.fontHeight)) / 2.0;
        final double centerX = x + maxWidth / 2.0;
        matrices.translate(centerX, y + offset, 0);
        matrices.scale((float) scale, (float) scale, (float) scale);
        renderText(matrices, text, true, shadow, colour);
        matrices.pop();
    }

    public void renderFitTextWrap(final MatrixStack matrices, final Text text, final double x, final double y, final double maxWidth, final double maxHeight, final boolean shadow, final int colour) {
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        final TextInfo textInfo = getTextScale(textRenderer.getWidth(text), maxWidth, maxHeight);
        final double width = textInfo.textWidth;
        final int maxLines = (int) Math.floor(maxWidth / (width * 1.5));
        if (maxLines > 1) {
            final List<OrderedText> lines = textRenderer.wrapLines(text, (int) Math.floor(widthToScaledWidth(maxWidth, matrices)));
            double minScale = Double.MAX_VALUE;
            for (final OrderedText line : lines) {
                minScale = Math.min(getTextScale(textRenderer.getWidth(line), maxWidth, maxHeight / lines.size()).textScale, minScale);
            }
            for (int j = 0; j < lines.size(); j++) {
                final OrderedText line = lines.get(j);
                matrices.push();
                final double offset = j * minScale * textRenderer.fontHeight;
                final double centerX = x + maxWidth / 2.0;
                matrices.translate(centerX, y + offset, 0);
                matrices.scale((float) minScale, (float) minScale, (float) minScale);
                renderText(matrices, line, true, shadow, colour);
                matrices.pop();
            }
        } else {
            renderFitText(matrices, text, x, y, maxWidth, maxHeight, shadow, colour);
        }
    }

    public void renderTextLines(final MatrixStack matrices, final List<? extends Text> texts, final double x, final double y, final double maxWidth, final double maxHeight, final boolean center, final boolean shadow, final int colour) {
        if (texts.size() == 0) {
            return;
        }
        final double maxHeightPerText = maxHeight / texts.size();
        double minScale = Double.MAX_VALUE;
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        for (final Text text : texts) {
            final int width = textRenderer.getWidth(text);
            minScale = Math.min(minScale, getTextScale(width, maxWidth, maxHeightPerText).textScale);
        }
        for (int i = 0; i < texts.size(); i++) {
            final Text text = texts.get(i);
            final double offset = i * minScale * textRenderer.fontHeight;
            final double centerX;
            if (center) {
                centerX = x + maxWidth / 2.0;
            } else {
                centerX = x;
            }
            matrices.push();
            matrices.translate(centerX, y + offset, 0);
            matrices.scale((float) minScale, (float) minScale, (float) minScale);
            renderText(matrices, text, center, shadow, colour);
            matrices.pop();
        }
    }
}
