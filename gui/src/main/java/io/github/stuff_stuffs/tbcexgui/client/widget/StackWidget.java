package io.github.stuff_stuffs.tbcexgui.client.widget;

import io.github.stuff_stuffs.tbcexgui.client.widget.panel.RootPanelWidget;
import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

public class StackWidget extends AbstractWidget {
    private Widget fallback = new RootPanelWidget();
    private final Stack<Widget> widgetStack = new ObjectArrayList<>();

    public void setFallback(final Widget fallback) {
        this.fallback = fallback;
    }

    public void push(final Widget widget) {
        widgetStack.push(widget);
    }

    public void pop() {
        if (!widgetStack.isEmpty()) {
            widgetStack.pop();
        }
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return getActiveWidget().getWidgetPosition();
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        return getActiveWidget().mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        return getActiveWidget().mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        return getActiveWidget().mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        return getActiveWidget().mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        getActiveWidget().render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && !widgetStack.isEmpty()) {
            pop();
            return true;
        }
        return getActiveWidget().keyPress(keyCode, scanCode, modifiers);
    }

    private Widget getActiveWidget() {
        if (widgetStack.isEmpty()) {
            return fallback;
        }
        return widgetStack.top();
    }
}
