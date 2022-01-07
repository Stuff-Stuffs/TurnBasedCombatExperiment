package io.github.stuff_stuffs.tbcexgui.client.widget.panel;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiQuadEmitter;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiRenderMaterial;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.LayoutAlgorithm;
import io.github.stuff_stuffs.tbcexgui.client.widget.PositionedWidget;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

import java.util.Map;

public class RootPanelWidget extends AbstractWidget {
    private static final GuiRenderMaterial MATERIAL = GuiRenderMaterial.POS_COLOUR_TRANSLUCENT;
    private final Map<Handle, PositionedWidget> widgets = new Reference2ObjectOpenHashMap<>();
    private final boolean shaded;

    public RootPanelWidget(final boolean shaded) {
        this.shaded = shaded;
    }

    public Handle addChild(final PositionedWidget widget) {
        final Handle handle = new Handle();
        widgets.put(handle, widget);
        return handle;
    }

    public void removeChild(final Handle handle) {
        widgets.remove(handle);
    }

    @Override
    public void resize(final double width, final double height, final int pixelWidth, final int pixelHeight) {
        super.resize(width, height, pixelWidth, pixelHeight);
        for (final PositionedWidget widget : widgets.values()) {
            widget.resize(width, height, pixelWidth, pixelHeight);
        }
    }

    @Override
    public void render(final GuiContext context) {
        if (shaded) {
            final float offsetX = ((float) getScreenWidth() - 1) / 2.0f;
            final float offsetY = ((float) getScreenHeight() - 1) / 2.0f;
            final GuiQuadEmitter emitter = context.getEmitter();
            emitter.renderMaterial(MATERIAL);
            emitter.pos(0, -offsetX, -offsetY);
            emitter.pos(1, -offsetX, 1 + offsetY);
            emitter.pos(2, 1 + offsetX, 1 + offsetY);
            emitter.pos(3, 1 + offsetX, -offsetY);
            final int colour = 0x77_00_00_00;
            emitter.colour(colour, colour, colour, colour);
            emitter.depth(1);
            emitter.emit();
        }
        LayoutAlgorithm.BASIC.layout(widgets.values(), context);
    }

    public static final class Handle {
    }
}
