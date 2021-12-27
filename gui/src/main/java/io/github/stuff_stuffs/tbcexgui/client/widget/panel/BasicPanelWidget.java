package io.github.stuff_stuffs.tbcexgui.client.widget.panel;

import io.github.stuff_stuffs.tbcexgui.client.render.GuiRenderLayers;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractParentWidget;
import io.github.stuff_stuffs.tbcexutil.client.RenderUtil;
import io.github.stuff_stuffs.tbcexutil.common.colour.Colour;
import io.github.stuff_stuffs.tbcexutil.common.colour.IntRgbColour;
import net.minecraft.client.util.math.MatrixStack;

public class BasicPanelWidget extends AbstractParentWidget {
    public static final Colour BACKGROUND_COLOUR = new IntRgbColour(127, 127, 127);
    private final WidgetPosition position;
    private final double width;
    private final double height;

    public BasicPanelWidget(WidgetPosition position, double width, double height) {
        this.position = position;
        this.width = width;
        this.height = height;
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return position;
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        render(vertexConsumers -> RenderUtil.renderRectangle(matrices, position.getX(), position.getY(), width, height, BACKGROUND_COLOUR, 192, vertexConsumers.getBuffer(GuiRenderLayers.POSITION_COLOUR_TRANSPARENT_LAYER)));
        super.render(matrices, mouseX, mouseY, delta);
    }
}
