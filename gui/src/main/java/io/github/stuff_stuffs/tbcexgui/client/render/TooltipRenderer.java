package io.github.stuff_stuffs.tbcexgui.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

import java.util.List;

public final class TooltipRenderer {
    private static final List<TooltipData> TOOLTIP_DATAS = new ReferenceArrayList<>(4);

    public static void render(final List<TooltipComponent> components, final double x, final double y, final double horizontalPixel, final double verticalPixel, final int pixelWidth, final int pixelHeight, final Matrix4f matrix) {
        if (!components.isEmpty()) {
            TOOLTIP_DATAS.add(new TooltipData(components, x, y, horizontalPixel, verticalPixel, pixelWidth, pixelHeight, matrix.copy()));
        }
    }

    public static void renderAll() {
        final Matrix4f backup = RenderSystem.getProjectionMatrix();
        final Window window = MinecraftClient.getInstance().getWindow();
        final Matrix4f proj = Matrix4f.projectionMatrix(0, window.getFramebufferWidth(), 0, window.getFramebufferHeight(), 1000, 3000);
        RenderSystem.setProjectionMatrix(proj);
        final VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(AbstractWidget.GUI_BUFFERS, AbstractWidget.FALLBACK_BUFFER);
        for (final TooltipData data : TOOLTIP_DATAS) {
            render(data, immediate);
        }
        immediate.draw();
        RenderSystem.setProjectionMatrix(backup);
        TOOLTIP_DATAS.clear();
    }

    public static void clear() {
        TOOLTIP_DATAS.clear();
    }

    private static void render(final TooltipData data, final VertexConsumerProvider.Immediate vertexConsumers) {
        final MatrixStack matrices = new MatrixStack();
        matrices.method_34425(data.matrix);
        final double borderThickness = 0.5;
        final MinecraftClient client = MinecraftClient.getInstance();
        final TextRenderer textRenderer = client.textRenderer;
        double textScale = Double.MAX_VALUE;
        double width = 0;
        double height = 0;
        for (final TooltipComponent component : data.components) {
            final int componentHeight = component.getHeight();
            final int componentWidth = component.getWidth(textRenderer);
            height += componentHeight;
            final double scale = AbstractWidget.getTextScale(componentWidth, 100, 1 / 48.0, data.pixelWidth, data.pixelHeight, data.horizontalPixel, data.verticalPixel);
            if (scale < textScale) {
                textScale = scale;
            }
            if (componentWidth * textScale > width) {
                width = componentWidth * textScale;
            }
        }
        width += 4 * data.verticalPixel * borderThickness;
        height *= textScale;
        height += 4 * data.verticalPixel * borderThickness;
        AbstractWidget.renderTooltipBackground(data.x, data.y, width, height, matrices, data.horizontalPixel, data.verticalPixel, vertexConsumers.getBuffer(GuiRenderLayers.POSITION_COLOUR_LAYER));

        double offset = 0;
        for (final TooltipComponent tooltipComponent : data.components) {
            matrices.push();
            matrices.translate(0, offset, 0);
            matrices.translate(data.x + 2 * data.horizontalPixel * borderThickness, data.y + 2 * data.verticalPixel * borderThickness, 0);
            matrices.scale((float) textScale, (float) textScale, 1);
            tooltipComponent.drawText(textRenderer, 0, 0, matrices.peek().getModel(), vertexConsumers);
            offset += tooltipComponent.getHeight() * textScale;
            matrices.pop();
        }

        offset = 0;
        for (final TooltipComponent tooltipComponent : data.components) {
            matrices.push();
            matrices.translate(0, offset, 0);
            matrices.translate(data.x + 2 * data.horizontalPixel * borderThickness, data.y + 2 * data.verticalPixel * borderThickness, 0);
            matrices.scale((float) textScale, (float) textScale, 1);
            tooltipComponent.drawItems(textRenderer, 0, 0, matrices, client.getItemRenderer(), 0, client.getTextureManager());
            offset += tooltipComponent.getHeight() * textScale;
            matrices.pop();
        }
    }

    private TooltipRenderer() {
    }

    private static class TooltipData {
        private final List<TooltipComponent> components;
        private final double x;
        private final double y;
        private final double horizontalPixel;
        private final double verticalPixel;
        private final int pixelHeight;
        private final int pixelWidth;
        private final Matrix4f matrix;

        private TooltipData(final List<TooltipComponent> components, final double x, final double y, final double horizontalPixel, final double verticalPixel, final int pixelWidth, final int pixelHeight, final Matrix4f matrix) {
            this.components = components;
            this.x = x;
            this.y = y;
            this.horizontalPixel = horizontalPixel;
            this.verticalPixel = verticalPixel;
            this.pixelHeight = pixelHeight;
            this.pixelWidth = pixelWidth;
            this.matrix = matrix;
        }
    }
}
