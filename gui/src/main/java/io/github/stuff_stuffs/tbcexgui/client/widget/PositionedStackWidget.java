package io.github.stuff_stuffs.tbcexgui.client.widget;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiInputContext;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class PositionedStackWidget extends AbstractWidget implements PositionedWidget {
    private final ReferenceArrayList<PositionedWidget> stack = new ReferenceArrayList<>();
    private PositionedWidget fallback;

    public void setFallback(final PositionedWidget fallback) {
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
        final PositionedWidget current;
        if (stack.isEmpty()) {
            current = fallback;
        } else {
            current = stack.top();
        }
        LayoutAlgorithm.BASIC.layout(List.of(current), context);
        context.exitSection();
    }

    @Override
    public String getDebugName() {
        return "PositionedStackWidget";
    }

    @Override
    public void resize(final double width, final double height, final int pixelWidth, final int pixelHeight) {
        super.resize(width, height, pixelWidth, pixelHeight);
        fallback.resize(width, height, pixelWidth, pixelHeight);
        stack.forEach(widget -> widget.resize(width, height, pixelWidth, pixelHeight));
    }

    public void push(final PositionedWidget widget) {
        stack.push(widget);
    }

    @Override
    public double getX() {
        return 0;
    }

    @Override
    public double getY() {
        return 0;
    }
}
