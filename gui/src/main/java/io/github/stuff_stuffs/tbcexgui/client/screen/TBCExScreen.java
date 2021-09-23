package io.github.stuff_stuffs.tbcexgui.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.stuff_stuffs.tbcexgui.client.widget.Widget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.glfw.GLFW;

public abstract class TBCExScreen extends Screen {
    protected final Widget widget;

    protected TBCExScreen(final Text title, final Widget widget) {
        super(title);
        this.widget = widget;
    }

    @Override
    protected void init() {
        final Window window = MinecraftClient.getInstance().getWindow();
        width = window.getFramebufferWidth();
        height = window.getFramebufferHeight();
        if (width > height) {
            widget.resize(width / (double) height, 1, width, height);
        } else {
            widget.resize(1, height / (double) width, width, height);
        }
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        return widget.mouseClicked(transformMouseX(mouseX), transformMouseY(mouseY), button);
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        return widget.mouseDragged(transformMouseX(mouseX), transformMouseY(mouseY), button, deltaX / width, deltaY / height);
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        return widget.mouseReleased(transformMouseX(mouseX), transformMouseY(mouseY), button);
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        return widget.mouseScrolled(transformMouseX(mouseX), transformMouseY(mouseY), transformMouseY(amount));
    }

    @Override
    public void mouseMoved(final double mouseX, final double mouseY) {
        //TODO
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && shouldCloseOnEsc()) {
            onClose();
            return true;
        }
        return widget.keyPress(keyCode, scanCode, modifiers);
    }

    private double transformMouseX(final double mouseX) {
        final Window window = MinecraftClient.getInstance().getWindow();
        final int width = window.getScaledWidth();
        final int height = window.getScaledHeight();
        if (width > height) {
            final double v = mouseX - (width / 2.0) + (height / 2.0);
            return v / height;
        }
        return mouseX / (double) width;
    }

    private double transformMouseY(final double mouseY) {
        final Window window = MinecraftClient.getInstance().getWindow();
        final int width = window.getScaledWidth();
        final int height = window.getScaledHeight();
        if (width < height) {
            final double v = mouseY - (height / 2.0) + (width / 2.0);
            return v / width;
        }
        return mouseY / (double) height;
    }

    @Override
    public void render(final MatrixStack matrices, final int mouseX, final int mouseY, final float delta) {
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
        }
        widget.render(matrices, transformMouseX(mouseX), transformMouseY(mouseY), delta);
        matrices.pop();
        RenderSystem.setProjectionMatrix(prevProjection);
    }
}
