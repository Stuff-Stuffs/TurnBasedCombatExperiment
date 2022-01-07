package io.github.stuff_stuffs.tbcexgui.client.widget;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.api.text.TextDrawer;
import io.github.stuff_stuffs.tbcexgui.client.api.text.TextDrawers;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class TextWidget extends AbstractWidget {
    private final Supplier<Text> text;
    private final BooleanSupplier shadow;
    private final double width;
    private final double height;
    private final TextDrawer textDrawer;
    private final TextDrawer textDrawerShadowed;

    public TextWidget(final Supplier<Text> text, final BooleanSupplier shadow, final int colour, final double width, final double height) {
        this.text = text;
        this.shadow = shadow;
        this.width = width;
        this.height = height;
        textDrawer = TextDrawers.oneShot(TextDrawers.HorizontalJustification.CENTER, TextDrawers.VerticalJustification.CENTER, colour, 0, false);
        textDrawerShadowed = TextDrawers.oneShot(TextDrawers.HorizontalJustification.CENTER, TextDrawers.VerticalJustification.CENTER, colour, 0, true);
    }

    @Override
    public void render(final GuiContext context) {
        (shadow.getAsBoolean() ? textDrawerShadowed : textDrawer).draw(width, height, text.get().asOrderedText(), context);
    }
}
