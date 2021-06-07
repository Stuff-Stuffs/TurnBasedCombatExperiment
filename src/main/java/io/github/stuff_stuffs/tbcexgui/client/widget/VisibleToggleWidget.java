package io.github.stuff_stuffs.tbcexgui.client.widget;

import net.minecraft.client.util.math.MatrixStack;

import java.util.function.BooleanSupplier;

public class VisibleToggleWidget extends AbstractParentWidget {
    private final WidgetPosition position;
    private final BooleanSupplier state;

    public VisibleToggleWidget(final WidgetPosition position, final BooleanSupplier state) {
        this.position = position;
        this.state = state;
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return position;
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        if (state.getAsBoolean()) {
            super.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        if (state.getAsBoolean()) {
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        if (state.getAsBoolean()) {
            return super.mouseScrolled(mouseX, mouseY, amount);
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (state.getAsBoolean()) {
            return super.mouseClicked(mouseX, mouseY, button);
        } else {
            return false;
        }
    }
}
