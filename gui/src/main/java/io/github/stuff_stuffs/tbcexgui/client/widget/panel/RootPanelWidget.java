package io.github.stuff_stuffs.tbcexgui.client.widget.panel;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiQuadEmitter;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiRenderMaterial;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.LayoutAlgorithm;
import io.github.stuff_stuffs.tbcexgui.client.widget.PositionedWidget;
import io.github.stuff_stuffs.tbcexutil.common.Vec2d;

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
        emitter.pos(3, 1 + offsetX, -offsetY);
        emitter.pos(2, 1 + offsetX, 1 + offsetY);
        emitter.pos(1, -offsetX, 1 + offsetY);
        final int colour = 0x77_00_00_00;
        emitter.colour(colour, colour, colour, colour);
        emitter.emit();
        //context.renderText(new LiteralText("sdasadfgsdfgdfgasd").asOrderedText(), GuiContext.TextOutline.NONE, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF);
        //context.pushScissor(0.25f, 0.25f, 0.5f, 0.5f);

        final float rad = 0.05f;
        final Vec2d mouse = context.transformMouseCursor(new Vec2d(context.getInputContext().getMouseCursorX(), context.getInputContext().getMouseCursorY()));
        final float cX = (float) mouse.x;
        final float cY = (float) mouse.y;
        emitter.renderMaterial(MATERIAL);
        emitter.pos(0, cX - rad, cY - rad);
        emitter.pos(3, cX + rad, cY - rad);
        emitter.pos(2, cX + rad, cY + rad);
        emitter.pos(1, cX - 2 * rad, cY + rad);
        emitter.depth(1);
        emitter.colour(0x77_FF_00_00, 0x77_00_FF_00, 0x77_00_00_FF, 0x77_00_00_00);
        emitter.emit();

        //context.popQuadTransform();

        LayoutAlgorithm.BASIC.layout(widgets, context);
    }
}
