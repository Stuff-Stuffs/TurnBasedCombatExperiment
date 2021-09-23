package io.github.stuff_stuffs.tbcexgui.client.widget.panel;

import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractParentWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import net.minecraft.client.util.math.MatrixStack;

public class RootPanelWidget extends AbstractParentWidget {
    private static final WidgetPosition ROOT = WidgetPosition.of(0, 0, 0);

    public RootPanelWidget() {
        setFocused(true);
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return ROOT;
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        super.render(matrices, mouseX, mouseY, delta);
    }
}
