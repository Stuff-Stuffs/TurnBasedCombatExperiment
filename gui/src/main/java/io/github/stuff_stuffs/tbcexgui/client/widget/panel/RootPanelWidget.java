package io.github.stuff_stuffs.tbcexgui.client.widget.panel;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractParentWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

public class RootPanelWidget extends AbstractParentWidget {
    private static final WidgetPosition ROOT = WidgetPosition.of(0, 0, 0);

    public RootPanelWidget () {
        this.setFocused(true);
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
