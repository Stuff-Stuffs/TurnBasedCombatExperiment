package io.github.stuff_stuffs.tbcexgui.client.widget.panel;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiQuadEmitter;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiRenderMaterial;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.LayoutAlgorithm;
import io.github.stuff_stuffs.tbcexgui.client.widget.PositionedWidget;

import java.util.ArrayList;
import java.util.List;

public class RootPanelWidget extends AbstractWidget {
    private static final GuiRenderMaterial MATERIAL = GuiRenderMaterial.POS_COLOUR_TRANSLUCENT;
    private final List<PositionedWidget> widgets = new ArrayList<>(1);

    public void addChild(final PositionedWidget widget) {
        widgets.add(widget);
    }

    @Override
    public void resize(final double width, final double height, final int pixelWidth, final int pixelHeight) {
        super.resize(width, height, pixelWidth, pixelHeight);
        for (final PositionedWidget widget : widgets) {
            widget.resize(width, height, pixelWidth, pixelHeight);
        }
    }

    @Override
    public void render(final GuiContext context) {
        final float offsetX = ((float) getWidth() - 1) / 2.0f;
        final float offsetY = ((float) getHeight() - 1) / 2.0f;
        final GuiQuadEmitter emitter = context.getEmitter();
        emitter.renderMaterial(MATERIAL);
        emitter.pos(0, -offsetX, -offsetY);
        emitter.pos(1, -offsetX, 1 + offsetY);
        emitter.pos(2, 1 + offsetX, 1 + offsetY);
        emitter.pos(3, 1 + offsetX, -offsetY);
        final int colour = 0x77_00_00_00;
        emitter.colour(colour, colour, colour, colour);
        emitter.emit();
        LayoutAlgorithm.BASIC.layout(widgets, context);
    }
}
