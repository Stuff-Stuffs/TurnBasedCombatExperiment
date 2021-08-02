package io.github.stuff_stuffs.tbcexgui.client.widget.panel;

import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractParentWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import net.minecraft.client.util.math.MatrixStack;

public class HidingPanel extends AbstractParentWidget {
    private static final WidgetPosition POSITION = WidgetPosition.of(0, 0, 0);
    private boolean hidden = true;

    public void setHidden(final boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (hidden) {
            return false;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        if (hidden) {
            return false;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        if (hidden) {
            return false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        if (hidden) {
            return false;
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        if (hidden) {
            return false;
        }
        return super.keyPress(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        if (!hidden) {
            super.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return POSITION;
    }
}
