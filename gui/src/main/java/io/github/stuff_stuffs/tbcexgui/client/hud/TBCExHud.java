package io.github.stuff_stuffs.tbcexgui.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.stuff_stuffs.tbcexgui.client.impl.GuiContextImpl;
import io.github.stuff_stuffs.tbcexgui.client.render.GuiRenderLayers;
import io.github.stuff_stuffs.tbcexgui.client.widget.Widget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

import java.util.ArrayList;

public abstract class TBCExHud {
    protected final Widget root;
    private final GuiContextImpl context;
    private int width = -1;
    private int height = -1;

    protected TBCExHud(final Widget root, final String name) {
        this.root = root;
        context = new GuiContextImpl(GuiRenderLayers.getVertexConsumers(), name);
    }

    private void resize() {
        final MinecraftClient client = MinecraftClient.getInstance();
        final int width = client.getWindow().getFramebufferWidth();
        final int height = client.getWindow().getFramebufferHeight();
        if (width != this.width || height != this.height) {
            this.width = width;
            this.height = height;
            if (width > height) {
                root.resize(width / (double) height, 1, width, height);
            } else {
                root.resize(1, height / (double) width, width, height);
            }
        }
    }

    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float tickDelta) {
        resize();
        final Window window = MinecraftClient.getInstance().getWindow();
        final Matrix4f prevProjection = RenderSystem.getProjectionMatrix();
        final Matrix4f matrix4f = Matrix4f.projectionMatrix(0.0F, window.getFramebufferWidth(), 0.0F, window.getFramebufferHeight(), 1000.0F, 3000.0F);
        RenderSystem.setProjectionMatrix(matrix4f);
        matrices.push();
        matrices.scale(width, height, 1);
        if (width > height) {
            matrices.scale(height / (float) width, 1, 1);
            matrices.translate((width / (double) height - 1) / 2d, 0, 0);
        } else if (width < height) {
            matrices.scale(1, width / (float) height, 1);
            matrices.translate(0, (height / (double) width - 1) / 2d, 0);
        } else {
            matrices.translate(0.5, 0.5, 0);
        }
        context.setup(matrices, tickDelta, 0, 0, new ArrayList<>());
        root.render(context);
        context.draw();
        matrices.pop();
        RenderSystem.setProjectionMatrix(prevProjection);
    }

    public void tick() {

    }
}
