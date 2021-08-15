package io.github.stuff_stuffs.tbcexgui.client.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.stuff_stuffs.tbcexgui.client.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

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

    protected void renderTooltip(final MatrixStack matrices, final List<? extends TooltipComponent> components, double x, double y) {
        if (!components.isEmpty()) {
            final double borderThickness = 0.5;
            final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            final Tessellator tessellator = Tessellator.getInstance();
            final BufferBuilder bufferBuilder = tessellator.getBuffer();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            double maxWidth = 0.05 + 4 * horizontalPixel * borderThickness;
            double height = 4 * verticalPixel * borderThickness + 2 / (double) pixelHeight;
            for (final TooltipComponent component : components) {
                height += component.getHeight() / (double) pixelHeight;
                final double width = component.getWidth(textRenderer) / (double) pixelWidth;
                if (width > maxWidth) {
                    maxWidth = width;
                }
            }
            maxWidth += 6 * horizontalPixel;
            if (maxWidth + x > 1) {
                x -= maxWidth;
            }
            if (height + y > 1) {
                y -= height;
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
                matrices.scale(1 / (float) pixelWidth, 1 / (float) pixelWidth, 1);
                final TooltipComponent component = components.get(v);
                component.drawText(textRenderer, 0, 0, matrices.peek().getModel(), immediate);
                offset += (component.getHeight() + (v == 0 ? 2 : 0)) / (double) pixelHeight;
                matrices.pop();
            }

            immediate.draw();
            for (int v = 0; v < components.size(); ++v) {
                matrices.push();
                matrices.translate(0, offset, 0);
                matrices.translate(x + 3 * horizontalPixel, y + 3 * verticalPixel, 0);
                matrices.scale(1 / (float) pixelWidth, 1 / (float) pixelWidth, 1);
                final TooltipComponent component = components.get(v);
                component.drawItems(textRenderer, 0, 0, matrices, MinecraftClient.getInstance().getItemRenderer(), 0, MinecraftClient.getInstance().getTextureManager());
                offset += (component.getHeight() + (v == 0 ? 2 : 0)) / (double) pixelHeight;
                matrices.pop();
            }
            matrices.pop();
        }
    }

    //TODO wtf
    public void renderFitText(final MatrixStack matrices, final Text text, final double x, final double y, final double maxWidth, final double maxHeight, final boolean shadow, final int colour) {
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        final float textWidth = (textRenderer.getWidth(text) * MinecraftClient.getInstance().getWindow().getScaledWidth() / (float) getPixelWidth());
        //todo even more wtf
        double scaleFactor = maxWidth<=0.125?1:1.75;
        double scale;
        if (textWidth > maxWidth) {
            scale = maxHeight * (maxWidth / textWidth) * textRenderer.fontHeight * scaleFactor;
        } else {
            scale = maxHeight;
        }
        if (scale * textRenderer.fontHeight > maxHeight) {
            scale = maxHeight / textRenderer.fontHeight;
        }
        matrices.push();
        double offset = (maxHeight-(scale*textRenderer.fontHeight))/2.0;
        double centerX = x+maxWidth/2.0;
        matrices.translate(centerX,y + offset,0);
        matrices.scale((float) scale, (float) scale, (float) scale);
        if (shadow) {
            textRenderer.drawWithShadow(matrices, text, -textRenderer.getWidth(text)/2f, 0, colour);
        } else {
            textRenderer.draw(matrices, text, -textRenderer.getWidth(text)/2f, 0, colour);
        }
        matrices.pop();
    }

    public void renderFitTextWrap(final MatrixStack matrices, final Text text, final double x, final double y, final double maxWidth, final double maxHeight, final boolean shadow, final int colour) {
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        float textWidth = (textRenderer.getWidth(text) * MinecraftClient.getInstance().getWindow().getScaledWidth() / (float) getPixelWidth());
        double scaleFactor = maxWidth<=0.125?1:1.75;
        double scale;
        if (textWidth > maxWidth) {
            scale = maxHeight * (maxWidth / textWidth) * textRenderer.fontHeight * scaleFactor;
        } else {
            scale = maxHeight;
        }
        if (scale * textRenderer.fontHeight > maxHeight) {
            scale = maxHeight / textRenderer.fontHeight;
        }
        int maxLines = (int) Math.floor(maxHeight/(scale*textRenderer.fontHeight*2*scaleFactor));
        if(maxLines>1) {
            List<OrderedText> lines = textRenderer.wrapLines(text, (int) Math.floor(textRenderer.getWidth(text)/(float)maxLines));
            double minScale = Double.MAX_VALUE;
            for (OrderedText line : lines) {
                textWidth = textRenderer.getWidth(line)/(float)getPixelWidth();
                if (textWidth > maxWidth) {
                    scale = maxHeight * (maxWidth / textWidth) * textRenderer.fontHeight * scaleFactor;
                } else {
                    scale = maxHeight;
                }
                if (scale * textRenderer.fontHeight > maxHeight) {
                    scale = maxHeight / textRenderer.fontHeight;
                }
                if(scale<minScale) {
                    minScale = scale;
                }
            }
            minScale /= (float)lines.size();
            for (int j = 0; j < lines.size(); j++) {
                OrderedText line = lines.get(j);
                matrices.push();
                double offset = (maxHeight/(double) lines.size())*j;
                double centerX = x + maxWidth / 2.0;
                matrices.translate(centerX, y + offset, 0);
                matrices.scale((float) minScale, (float) minScale, (float) minScale);
                if (shadow) {
                    textRenderer.drawWithShadow(matrices, line, -textRenderer.getWidth(line) / 2f, 0, colour);
                } else {
                    textRenderer.draw(matrices, line, -textRenderer.getWidth(line) / 2f, 0, colour);
                }
                matrices.pop();
            }
        } else {
            renderFitText(matrices, text, x, y, maxWidth, maxHeight, shadow, colour);
        }
    }
}
