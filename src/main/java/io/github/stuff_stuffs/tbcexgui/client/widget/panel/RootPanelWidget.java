package io.github.stuff_stuffs.tbcexgui.client.widget.panel;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractParentWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

public class RootPanelWidget extends AbstractParentWidget {
    private static final WidgetPosition ROOT = WidgetPosition.of(0, 0, 0);

    public RootPanelWidget () {
        this.setFocused(true);
    }
    @Override
    public WidgetPosition getWidgetPosition() {
        return ROOT;
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        final Matrix4f model = matrices.peek().getModel();
        final BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(model, 1, 0, 0).color(0, 0, 0, 127).next();
        buffer.vertex(model, 0, 0, 0).color(0, 0, 0, 127).next();
        buffer.vertex(model, 0, 1, 0).color(0, 0, 0, 127).next();
        buffer.vertex(model, 1, 1, 0).color(0, 0, 0, 127).next();
        buffer.end();
        BufferRenderer.draw(buffer);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
