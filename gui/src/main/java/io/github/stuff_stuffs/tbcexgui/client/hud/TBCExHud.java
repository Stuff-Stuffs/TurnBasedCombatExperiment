package io.github.stuff_stuffs.tbcexgui.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.stuff_stuffs.tbcexgui.client.render.GuiRenderLayers;
import io.github.stuff_stuffs.tbcexgui.client.widget.ParentWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

public abstract class TBCExHud {
    protected final ParentWidget root;
    private int width=-1;
    private int height=-1;

    protected TBCExHud(ParentWidget root) {
        this.root = root;
    }

    private void resize() {
        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getFramebufferWidth();
        int height = client.getWindow().getFramebufferHeight();
        if(width!=this.width||height!=this.height) {
            this.width = width;
            this.height = height;
            if (width > height) {
                root.resize(width / (double) height, 1, width, height);
            } else {
                root.resize(1, height / (double) width, width, height);
            }
        }
    }

    private double transformMouseX(final double mouseX) {
        Window window = MinecraftClient.getInstance().getWindow();
        int width = window.getScaledWidth();
        int height = window.getScaledHeight();
        if (width > height) {
            final double v = mouseX - (width / 2.0) + (height / 2.0);
            return v / height;
        }
        return mouseX / (double) width;
    }

    private double transformMouseY(final double mouseY) {
        Window window = MinecraftClient.getInstance().getWindow();
        int width = window.getScaledWidth();
        int height = window.getScaledHeight();
        if (width < height) {
            final double v = mouseY - (height / 2.0) + (width / 2.0);
            return v / width;
        }
        return mouseY / (double) height;
    }

    public void render(MatrixStack matrices, double mouseX, double mouseY, float tickDelta) {
        resize();
        Window window = MinecraftClient.getInstance().getWindow();
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
        }
        root.render(matrices, transformMouseX(mouseX), transformMouseY(mouseY), tickDelta);
        matrices.pop();
        RenderSystem.setProjectionMatrix(prevProjection);
    }

    public void tick() {

    }
}
