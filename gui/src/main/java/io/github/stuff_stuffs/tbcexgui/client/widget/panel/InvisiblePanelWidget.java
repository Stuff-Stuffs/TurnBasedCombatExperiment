package io.github.stuff_stuffs.tbcexgui.client.widget.panel;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.widget.LayoutAlgorithm;
import io.github.stuff_stuffs.tbcexgui.client.widget.Widget;

import java.util.Collection;

public class InvisiblePanelWidget<C extends Collection<T>, T extends Widget> extends AbstractPanelWidget {
    private final C children;
    private final LayoutAlgorithm<C, ? super T> layout;

    public InvisiblePanelWidget(final LayoutAlgorithm<C, ? super T> layout, final C children) {
        super(0, 0, () -> 0);
        this.children = children;
        this.layout = layout;
    }

    public void addChild(final T child) {
        children.add(child);
    }

    @Override
    public void render(final GuiContext context) {
        context.enterSection(getDebugName());
        renderChildren(context);
        context.exitSection();
    }

    @Override
    protected void renderChildren(final GuiContext context) {
        layout.layout(children, context);
    }

    @Override
    protected void resizeChildren(final double width, final double height, final int pixelWidth, final int pixelHeight) {
        for (final T child : children) {
            child.resize(width, height, pixelWidth, pixelHeight);
        }
    }

    @Override
    public String getDebugName() {
        return "InvisiblePanelWidget";
    }
}
