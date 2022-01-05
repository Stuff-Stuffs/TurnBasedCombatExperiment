package io.github.stuff_stuffs.tbcexgui.client.api.text;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiTextRenderer;
import net.minecraft.text.OrderedText;

import java.util.List;

public final class TextDrawers {
    private TextDrawers() {
    }

    public static TextDrawer lineBreaking(final HorizontalJustification perLineJustification, final int colour, final int backgroundColour, final boolean shadowed) {
        final TextDrawer delegate = oneShot(perLineJustification, VerticalJustification.BOTTOM, colour, backgroundColour, shadowed);
        return (width, height, text, context) -> {
            final List<OrderedText> texts = OrderedTextUtil.lengthSplit(text, OrderedTextUtil.simpleLengthSplitHeuristic(width, 4, 8));
            final int size = texts.size();
            if (size == 0) {
                return;
            }
            final double maxHeight = height / size;
            for (int i = 0; i < size; i++) {
                context.pushTranslate(0, maxHeight * i, 0);
                delegate.draw(width, maxHeight, texts.get(i), context);
                context.popGuiTransform();
            }
        };
    }

    public static TextDrawer oneShot(final HorizontalJustification horizontalJustification, final VerticalJustification verticalJustification, final int colour, final int backgroundColour, final boolean shadowed) {
        return (width, height, text, context) -> {
            final GuiTextRenderer renderer = context.getTextRenderer();
            final double textWidth = renderer.getWidth(text);
            final double horizontalScaleFactor = width / Math.max(textWidth, 0.001);
            final double verticalScaleFactor = height / Math.max(renderer.getHeight(), 0.001);
            final double scaleFactor = Math.min(horizontalScaleFactor, verticalScaleFactor);
            final double scaledTextWidth = textWidth * scaleFactor;
            final double scaledTextHeight = renderer.getHeight() * scaleFactor;
            final double offsetX = (width - scaledTextWidth);
            final double offsetY = (height - scaledTextHeight);
            final double transX = switch (horizontalJustification) {
                case LEFT -> 0;
                case CENTER -> -offsetX / 2.0;
                case RIGHT -> offsetX;
            };
            final double transY = switch (verticalJustification) {
                case TOP -> 0;
                case CENTER -> -offsetY / 2.0;
                case BOTTOM -> -offsetY;
            };
            if (transX != 0 || transY != 0) {
                context.pushTranslate(transX, transY, 0);
            }
            context.pushScale(scaleFactor, scaleFactor, scaleFactor);
            renderer.render(text, colour, shadowed, backgroundColour);
            context.popGuiTransform();
            if (transX != 0 || transY != 0) {
                context.popGuiTransform();
            }
        };
    }

    public enum HorizontalJustification {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum VerticalJustification {
        TOP,
        CENTER,
        BOTTOM
    }
}
