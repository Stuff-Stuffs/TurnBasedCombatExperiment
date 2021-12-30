package io.github.stuff_stuffs.tbcexgui.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiInputContext;
import io.github.stuff_stuffs.tbcexgui.client.impl.GuiContextImpl;
import io.github.stuff_stuffs.tbcexgui.client.render.GuiRenderLayers;
import io.github.stuff_stuffs.tbcexgui.client.widget.Widget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public abstract class HandledTBCExScreen<T extends ScreenHandler> extends HandledScreen<T> implements RawCharTypeScreen {
    protected final Widget widget;
    private final List<GuiInputContext.InputEvent> inputEvents = new ArrayList<>(8);

    public HandledTBCExScreen(final T handler, final PlayerInventory inventory, final Text title, final Widget widget) {
        super(handler, inventory, title);
        this.widget = widget;
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        //TODO sensitivity
        inputEvents.add(new GuiInputContext.MouseScroll(transformMouseX(mouseX), transformMouseY(mouseY), amount / height));
        return true;
    }

    @Override
    public void mouseMoved(final double mouseX, final double mouseY) {
        //TODO
        inputEvents.add(new GuiInputContext.MouseMove(transformMouseX(mouseX), transformMouseY(mouseY)));
    }

    @Override
    protected void init() {
        super.init();
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
        final Mouse mouse = MinecraftClient.getInstance().mouse;
        final GuiContext context;
        if (mouse.isCursorLocked()) {
            context = new GuiContextImpl(matrices, GuiRenderLayers.getVertexConsumers(), 0.5, 0.5, inputEvents, delta);
        } else {
            context = new GuiContextImpl(matrices, GuiRenderLayers.getVertexConsumers(), transformMouseX(mouseX), transformMouseY(mouseY), inputEvents, delta);
        }
        widget.render(context);
        GuiRenderLayers.getVertexConsumers().draw();
        matrices.pop();
        RenderSystem.setProjectionMatrix(prevProjection);
        inputEvents.clear();
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        inputEvents.add(new GuiInputContext.MouseClick(transformMouseX(mouseX), transformMouseY(mouseY), button));
        return true;
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        //TODO sensitivity
        inputEvents.add(new GuiInputContext.MouseDrag(transformMouseX(mouseX), transformMouseY(mouseY), deltaX / width, deltaY / height, button));
        return true;
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        inputEvents.add(new GuiInputContext.MouseReleased(transformMouseX(mouseX), transformMouseY(mouseY), button));
        return true;
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && shouldCloseOnEsc()) {
            onClose();
            return true;
        }
        inputEvents.add(new GuiInputContext.KeyPress(keyCode, (modifiers & GLFW.GLFW_MOD_SHIFT) != 0, (modifiers & GLFW.GLFW_MOD_ALT) != 0, (modifiers & GLFW.GLFW_MOD_CONTROL) != 0, (modifiers & GLFW.GLFW_MOD_CAPS_LOCK) != 0, (modifiers & GLFW.GLFW_MOD_NUM_LOCK) != 0));
        return true;
    }

    @Override
    public void onCharTyped(final int codePoint, final int modifiers) {
        inputEvents.add(new GuiInputContext.KeyModsPress(codePoint, (modifiers & GLFW.GLFW_MOD_SHIFT) != 0, (modifiers & GLFW.GLFW_MOD_ALT) != 0, (modifiers & GLFW.GLFW_MOD_CONTROL) != 0, (modifiers & GLFW.GLFW_MOD_CAPS_LOCK) != 0, (modifiers & GLFW.GLFW_MOD_NUM_LOCK) != 0));
    }

    private static double transformMouseX(final double mouseX) {
        final Window window = MinecraftClient.getInstance().getWindow();
        final int width = window.getScaledWidth();
        final int height = window.getScaledHeight();
        if (width > height) {
            final double v = mouseX - (width / 2.0) + (height / 2.0);
            return v / height;
        }
        return mouseX / (double) width;
    }

    private static double transformMouseY(final double mouseY) {
        final Window window = MinecraftClient.getInstance().getWindow();
        final int width = window.getScaledWidth();
        final int height = window.getScaledHeight();
        if (width < height) {
            final double v = mouseY - (height / 2.0) + (width / 2.0);
            return v / width;
        }
        return mouseY / (double) height;
    }
}
