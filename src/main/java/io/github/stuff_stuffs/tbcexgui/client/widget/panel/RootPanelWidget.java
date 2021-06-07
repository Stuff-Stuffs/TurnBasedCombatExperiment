package io.github.stuff_stuffs.tbcexgui.client.widget.panel;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractParentWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.SuppliedWidgetPosition;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

public class RootPanelWidget extends AbstractParentWidget {
    private static final WidgetPosition ROOT = new SuppliedWidgetPosition(0, 0, 0, 1);

    @Override
    public WidgetPosition getWidgetPosition() {
        return ROOT;
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        final Matrix4f model = matrices.peek().getModel();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        final BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        final float width = (float) getScreenWidth();
        final float height = (float) getScreenHeight();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(model, width, -height, 0).color(0, 0, 0, 127).next();
        bufferBuilder.vertex(model, -width, -height, 0).color(0, 0, 0, 127).next();
        bufferBuilder.vertex(model, -width, height, 0).color(0, 0, 0, 127).next();
        bufferBuilder.vertex(model, width, height, 0).color(0, 0, 0, 127).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
