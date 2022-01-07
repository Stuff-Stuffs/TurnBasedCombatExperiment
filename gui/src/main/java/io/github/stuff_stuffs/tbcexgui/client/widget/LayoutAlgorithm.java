package io.github.stuff_stuffs.tbcexgui.client.widget;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;

import java.util.Collection;

public interface LayoutAlgorithm<C extends Collection<T>, T extends Widget> {
    LayoutAlgorithm<Collection<PositionedWidget>, PositionedWidget> BASIC = (widgets, context) -> {
        int i = 0;
        for (PositionedWidget widget : widgets) {
            context.pushTranslate(widget.getX(), widget.getY(), (i++ + 1) / (double) (widgets.size()));
            widget.render(context);
            context.popGuiTransform();
        }
    };

    void layout(C widgets, GuiContext context);
}
