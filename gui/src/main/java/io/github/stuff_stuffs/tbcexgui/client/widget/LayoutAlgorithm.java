package io.github.stuff_stuffs.tbcexgui.client.widget;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;

import java.util.List;

public interface LayoutAlgorithm<T extends Widget> {
    LayoutAlgorithm<PositionedWidget> BASIC = (widgets, context) -> {
        for (int i = 0; i < widgets.size(); i++) {
            PositionedWidget widget = widgets.get(i);
            context.pushTranslate(widget.getX(), widget.getY(), (i + 1) / (double) (widgets.size()));
            final GuiContext childContext = context.createChild();
            widget.render(childContext);
            context.popQuadTransform();
        }
    };

    void layout(List<? extends T> widgets, GuiContext context);
}
