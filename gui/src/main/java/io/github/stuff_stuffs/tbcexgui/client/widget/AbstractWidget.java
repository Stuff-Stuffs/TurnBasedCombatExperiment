package io.github.stuff_stuffs.tbcexgui.client.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.stuff_stuffs.tbcexgui.client.render.GuiRenderLayers;
import io.github.stuff_stuffs.tbcexgui.client.render.TooltipRenderer;
import io.github.stuff_stuffs.tbcexutil.client.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Consumer;

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

    public void render(final Consumer<VertexConsumerProvider> renderer) {
        final VertexConsumerProvider.Immediate immediate = GuiRenderLayers.getVertexConsumer();
        renderer.accept(immediate);
        immediate.draw();
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

    protected void renderTooltip(final MatrixStack matrices, final List<TooltipComponent> components, final double x, final double y) {
        TooltipRenderer.render(components, x, y, horizontalPixel, verticalPixel, pixelWidth, pixelHeight, matrices.peek().getModel());
    }

    public void renderTooltipBackground(final double x, final double y, final double width, final double height, final MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        renderTooltipBackground(x, y, width, height, matrices, horizontalPixel, verticalPixel, vertexConsumers.getBuffer(GuiRenderLayers.POSITION_COLOUR_LAYER));
    }

    public static void renderTooltipBackground(final double x, final double y, final double width, final double height, final MatrixStack matrices, final double horizontalPixel, final double verticalPixel, final VertexConsumer vertexConsumer) {
        final double borderThickness = 0.5;
        final int background = 0xF0100010;

        //center
        RenderUtil.renderRectangle(matrices, x, y + 1 * verticalPixel * borderThickness, width, height - 2 * verticalPixel * borderThickness, background, vertexConsumer);
        //background sides
        RenderUtil.renderRectangle(matrices, x + 1 * horizontalPixel * borderThickness, y, width - 2 * horizontalPixel * borderThickness, verticalPixel * borderThickness, background, vertexConsumer);
        RenderUtil.renderRectangle(matrices, x + 1 * horizontalPixel * borderThickness, y + height - verticalPixel * borderThickness, width - 2 * horizontalPixel * borderThickness, verticalPixel * borderThickness, background, vertexConsumer);
        //purple top and bottom
        final int topPurple = 0x505000FF;
        final int bottomPurple = 0x5028007F;
        RenderUtil.renderRectangle(matrices, x + 1 * horizontalPixel * borderThickness, y + verticalPixel * borderThickness, width - 2 * horizontalPixel * borderThickness, verticalPixel * borderThickness, topPurple, vertexConsumer);
        RenderUtil.renderRectangle(matrices, x + 1 * horizontalPixel * borderThickness, y + height - 2 * verticalPixel * borderThickness, width - 2 * horizontalPixel * borderThickness, verticalPixel * borderThickness, bottomPurple, vertexConsumer);
        //purple sides
        RenderUtil.renderRectangle(matrices, x + 1 * horizontalPixel * borderThickness, y + 2 * verticalPixel * borderThickness, horizontalPixel * borderThickness, height - 4 * verticalPixel * borderThickness, topPurple, bottomPurple, topPurple, bottomPurple, vertexConsumer);
        RenderUtil.renderRectangle(matrices, x + width - 2 * horizontalPixel * borderThickness, y + 2 * verticalPixel * borderThickness, horizontalPixel * borderThickness, height - 4 * verticalPixel * borderThickness, topPurple, bottomPurple, topPurple, bottomPurple, vertexConsumer);
    }

    public static double getTextScale(final int textWidth, final double maxWidth, final double maxHeight, final double pixelWidth, final double pixelHeight, final double horizontalPixel, final double verticalPixel) {
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        final double scaleFactor = MinecraftClient.getInstance().getWindow().getScaleFactor();
        final double hScale = (textWidth * 4 / pixelWidth) * scaleFactor;
        final double hSize = (maxWidth / hScale) * horizontalPixel;
        final double vScale = (textRenderer.fontHeight * 2 / pixelHeight) * scaleFactor;
        final double vSize = (maxHeight / vScale) * verticalPixel;

        return Math.min(hSize, vSize);
    }

    public double getTextScale(final int textWidth, final double maxWidth, final double maxHeight) {
        return getTextScale(textWidth, maxWidth, maxHeight, pixelWidth, pixelHeight, horizontalPixel, verticalPixel);
    }

    public void renderText(final MatrixStack matrices, final Text text, final boolean center, final boolean shadow, final int colour, final VertexConsumerProvider vertexConsumers) {
        renderText(matrices, text.asOrderedText(), center, shadow, colour, vertexConsumers);
    }

    public void renderText(final MatrixStack matrices, final OrderedText text, final boolean center, final boolean shadow, final int colour, final VertexConsumerProvider vertexConsumers) {
        renderText(matrices, text, center, shadow, colour, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE, vertexConsumers);
    }

    public void renderText(final MatrixStack matrices, final OrderedText text, final boolean center, final boolean shadow, final int colour, final int backgroundColour, final int light, final VertexConsumerProvider vertexConsumer) {
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        if (center) {
            final float middle = textRenderer.getWidth(text) / 2.0f;
            textRenderer.draw(text, -middle, 0, colour, shadow, matrices.peek().getModel(), vertexConsumer, false, backgroundColour, light);
        } else {
            textRenderer.draw(text, 0, 0, colour, shadow, matrices.peek().getModel(), vertexConsumer, false, backgroundColour, light);
        }
    }

    public void renderFitText(final MatrixStack matrices, final Text text, final double x, final double y, final double maxWidth, final double maxHeight, final boolean shadow, final int colour, final VertexConsumerProvider vertexConsumers) {
        renderFitText(matrices, text, x, y, maxWidth, maxHeight, shadow, colour, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE, vertexConsumers);
    }

    public void renderFitText(final MatrixStack matrices, final Text text, final double x, final double y, final double maxWidth, final double maxHeight, final boolean shadow, final int colour, final int backgroundColour, final int light, final VertexConsumerProvider vertexConsumers) {
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        final double scale = getTextScale(textRenderer.getWidth(text), maxWidth, maxHeight);
        matrices.push();
        final double offset = (maxHeight - (scale * textRenderer.fontHeight)) / 2.0;
        final double centerX = x + maxWidth / 2.0;
        matrices.translate(centerX, y + offset, 0);
        matrices.scale((float) scale, (float) scale, (float) scale);
        renderText(matrices, text.asOrderedText(), true, shadow, colour, backgroundColour, light, vertexConsumers);
        matrices.pop();
    }

    public void renderFitTextWrap(final MatrixStack matrices, final Text text, final double x, final double y, final double maxWidth, final double maxHeight, final boolean shadow, final int colour, final VertexConsumerProvider vertexConsumers) {
        renderFitTextWrap(matrices, text, x, y, maxWidth, maxHeight, shadow, colour, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE, vertexConsumers);
    }

    public void renderFitTextWrap(final MatrixStack matrices, final Text text, final double x, final double y, final double maxWidth, final double maxHeight, final boolean shadow, final int colour, final int backgroundColour, final int light, final VertexConsumerProvider vertexConsumers) {
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        final int textWidth = textRenderer.getWidth(text);
        final double textScale = getTextScale(textWidth, 100, maxHeight);
        final double width = textWidth * textScale;
        //TODO better heuristic
        final int maxLines = (int) Math.floor(width / (maxWidth * 3));
        if (maxLines > 1) {
            final List<OrderedText> lines = textRenderer.wrapLines(text, (int) Math.floor(3 * maxWidth / textScale));
            double minScale = Double.MAX_VALUE;
            for (final OrderedText line : lines) {
                minScale = Math.min(getTextScale(textRenderer.getWidth(line), maxWidth, maxHeight / lines.size()), minScale);
            }
            final double centerOffset = maxHeight / 2 - (lines.size() * minScale * textRenderer.fontHeight) / 2;
            for (int j = 0; j < lines.size(); j++) {
                final OrderedText line = lines.get(j);
                matrices.push();
                final double offset = j * minScale * textRenderer.fontHeight;
                final double centerX = x + maxWidth / 2.0;
                matrices.translate(centerX, y + centerOffset + offset, 0);
                matrices.scale((float) minScale, (float) minScale, (float) minScale);
                renderText(matrices, line, true, shadow, colour, backgroundColour, light, vertexConsumers);
                matrices.pop();
            }
        } else {
            renderFitText(matrices, text, x, y, maxWidth, maxHeight, shadow, colour, backgroundColour, light, vertexConsumers);
        }
    }

    public void renderTextLines(final MatrixStack matrices, final List<? extends Text> texts, final double x, final double y, final double maxWidth, final double maxHeight, final boolean center, final boolean shadow, final int colour, final VertexConsumerProvider vertexConsumers) {
        renderTextLines(matrices, texts, x, y, maxWidth, maxHeight, center, shadow, colour, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE, vertexConsumers);
    }

    public void renderTextLines(final MatrixStack matrices, final List<? extends Text> texts, final double x, final double y, final double maxWidth, final double maxHeight, final boolean center, final boolean shadow, final int colour, final int backgroundColour, final int light, final VertexConsumerProvider vertexConsumers) {
        if (texts.size() == 0) {
            return;
        }
        final double maxHeightPerText = maxHeight / texts.size();
        double minScale = Double.MAX_VALUE;
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        for (final Text text : texts) {
            final int width = textRenderer.getWidth(text);
            minScale = Math.min(minScale, getTextScale(width, maxWidth, maxHeightPerText));
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
            renderText(matrices, text.asOrderedText(), center, shadow, colour, backgroundColour, light, vertexConsumers);
            matrices.pop();
        }
    }
}
