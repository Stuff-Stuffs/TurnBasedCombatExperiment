package io.github.stuff_stuffs.tbcexgui.client.widget;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiInputContext;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import org.lwjgl.glfw.GLFW;

public class StackWidget extends AbstractWidget {
    private final ReferenceArrayList<Widget> stack = new ReferenceArrayList<>();
    private Widget fallback;

    public void setFallback(final Widget fallback) {
        this.fallback = fallback;
    }

    @Override
    public void render(final GuiContext context) {
        context.enterSection(getDebugName());
        processEvents(context, event -> {
            if (event instanceof GuiInputContext.KeyPress keyPress) {
                if (!stack.isEmpty() && keyPress.keyCode == GLFW.GLFW_KEY_ESCAPE) {
                    stack.pop();
                    return true;
                }
            }
            return false;
        });
        if (stack.isEmpty()) {
            fallback.render(context);
        } else {
            stack.top().render(context);
        }
        context.exitSection();
    }

    @Override
    public String getDebugName() {
        return "StackWidget";
    }

    @Override
    public void resize(final double width, final double height, final int pixelWidth, final int pixelHeight) {
        super.resize(width, height, pixelWidth, pixelHeight);
        fallback.resize(width, height, pixelWidth, pixelHeight);
        stack.forEach(widget -> widget.resize(width, height, pixelWidth, pixelHeight));
    }

    public void push(final Widget widget) {
        stack.push(widget);
    }
}
