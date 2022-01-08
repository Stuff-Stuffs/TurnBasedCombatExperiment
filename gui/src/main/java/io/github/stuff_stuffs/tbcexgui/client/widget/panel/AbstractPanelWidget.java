package io.github.stuff_stuffs.tbcexgui.client.widget.panel;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiQuadEmitter;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiRenderMaterial;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;

import java.util.function.IntSupplier;

public abstract class AbstractPanelWidget extends AbstractWidget {
    private static final GuiRenderMaterial MATERIAL = GuiRenderMaterial.POS_COLOUR_TRANSLUCENT;
    protected final float width;
    protected final float height;
    protected final IntSupplier colour;

    public AbstractPanelWidget(final double width, final double height, final IntSupplier colour) {
        this.width = (float) width;
        this.height = (float) height;
        this.colour = colour;
    }

    @Override
    public void resize(final double width, final double height, final int pixelWidth, final int pixelHeight) {
        super.resize(width, height, pixelWidth, pixelHeight);
        resizeChildren(width, height, pixelWidth, pixelHeight);
    }

    @Override
    public void render(final GuiContext context) {
        final GuiQuadEmitter emitter = context.getEmitter();
        emitter.renderMaterial(MATERIAL);
        emitter.pos(0, 0, 0);
        emitter.pos(1, 0, height);
        emitter.pos(2, width, height);
        emitter.pos(3, width, 0);
        final int colour = this.colour.getAsInt();
        emitter.colour(colour, colour, colour, colour);
        emitter.emit();
        renderChildren(context);
    }

    protected abstract void renderChildren(GuiContext context);

    protected abstract void resizeChildren(double width, double height, int pixelWidth, int pixelHeight);
}
