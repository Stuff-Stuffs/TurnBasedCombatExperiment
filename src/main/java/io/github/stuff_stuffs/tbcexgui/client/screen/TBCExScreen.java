package io.github.stuff_stuffs.tbcexgui.client.screen;

import io.github.stuff_stuffs.tbcexgui.client.render.ScissorStack;
import io.github.stuff_stuffs.tbcexgui.client.widget.Widget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public abstract class TBCExScreen extends Screen {
    protected final Widget widget;

    protected TBCExScreen(final Text title, final Widget widget) {
        super(title);
        this.widget = widget;
    }

    @Override
    protected void init() {
        final Framebuffer fb = MinecraftClient.getInstance().getFramebuffer();
        if (width > height) {
            widget.resize(width / (double) height, 1, fb.viewportWidth, fb.viewportHeight);
        } else {
            widget.resize(1, height / (double) width, fb.viewportWidth, fb.viewportHeight);
        }
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        return widget.mouseClicked(transformMouseX(mouseX), transformMouseY(mouseY), button);
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        return widget.mouseDragged(transformMouseX(mouseX), transformMouseY(mouseY), button, deltaX / (Math.min(width, height)), deltaY / (Math.min(width, height)));
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        //TODO
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        return widget.mouseScrolled(transformMouseX(mouseX), transformMouseY(mouseY), amount);
    }

    @Override
    public void mouseMoved(final double mouseX, final double mouseY) {
        //TODO
    }

    private double transformMouseX(final double mouseX) {
        return (mouseX - width / 2d) / (Math.min(width, height) / 2d);
    }

    private double transformMouseY(final double mouseY) {
        return (mouseY - height / 2d) / (Math.min(width, height) / 2d);
    }

    @Override
    public void render(final MatrixStack matrices, final int mouseX, final int mouseY, final float delta) {
        final Framebuffer fb = MinecraftClient.getInstance().getFramebuffer();
        matrices.translate(fb.viewportWidth / 2d, fb.viewportHeight / 2d, 0);
        final float scale = Math.min(fb.viewportWidth, fb.viewportHeight);
        matrices.scale(scale / 2f, scale / 2f, 1);
        ScissorStack.push(matrices, -1, -1, 1, 1);
        widget.render(matrices, transformMouseX(mouseX), transformMouseY(mouseY), delta);
        ScissorStack.pop();
    }
}
