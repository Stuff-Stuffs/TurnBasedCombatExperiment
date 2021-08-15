package io.github.stuff_stuffs.tbcexcore.client.gui.widget;

import io.github.stuff_stuffs.tbcexcore.client.util.ItemStackInfo;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class BattleInventoryPreviewWidget extends AbstractWidget {
    private final WidgetPosition position;
    private final DoubleSupplier width;
    private final DoubleSupplier height;
    private final Supplier<@Nullable ItemStackInfo> stackSupplier;

    public BattleInventoryPreviewWidget(WidgetPosition position, DoubleSupplier width, DoubleSupplier height, Supplier<@Nullable ItemStackInfo> stackSupplier) {
        this.position = position;
        this.width = width;
        this.height = height;
        this.stackSupplier = stackSupplier;
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return position;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return false;
    }

    @Override
    public void render(MatrixStack matrices, double mouseX, double mouseY, float delta) {

    }

    @Override
    public boolean keyPress(int keyCode, int scanCode, int modifiers) {
        return false;
    }
}
