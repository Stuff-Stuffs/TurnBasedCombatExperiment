package io.github.stuff_stuffs.tbcexgui.client.widget;

import io.github.stuff_stuffs.tbcexutil.common.colour.Colour;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class TextWidget extends AbstractWidget {
    private final WidgetPosition position;
    private final Supplier<Text> text;
    private final BooleanSupplier shadow;
    private final Colour colour;
    private final IntSupplier alpha;
    private final double width;
    private final double height;

    public TextWidget(final WidgetPosition position, final Supplier<Text> text, final BooleanSupplier shadow, final Colour colour, final IntSupplier alpha, final double width, final double height) {
        this.position = position;
        this.text = text;
        this.shadow = shadow;
        this.colour = colour;
        this.alpha = alpha;
        this.width = width;
        this.height = height;
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return position;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        return false;
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        return false;
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        return false;
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        return false;
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        render(vertexConsumers -> renderFitTextWrap(matrices, text.get(), position.getX(), position.getY(), width, height, shadow.getAsBoolean(), colour, alpha.getAsInt(), vertexConsumers));
    }

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        return false;
    }
}
