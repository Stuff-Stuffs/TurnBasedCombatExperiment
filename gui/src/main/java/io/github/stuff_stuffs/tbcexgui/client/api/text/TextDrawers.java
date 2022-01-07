package io.github.stuff_stuffs.tbcexgui.client.api.text;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiTextRenderer;
import net.minecraft.text.OrderedText;

import java.util.List;

public final class TextDrawers {
    private TextDrawers() {
    }

    public static TextDrawer lineBreaking(final HorizontalJustification perLineJustification, final int colour, final int backgroundColour, final boolean shadowed) {
        return new TextDrawer() {
            //TODO optimize
            private List<OrderedText> findBest(final OrderedText text, final double width, final double height, final GuiTextRenderer renderer) {
                final int SPLIT_CONST = 4;
                final int fullWidth = Math.max(renderer.getWidth(text) >> SPLIT_CONST, 2);
                int bestSplitSize = fullWidth;
                double bestScale = 0;
                for (int i = 0; i < fullWidth; i++) {
                    final List<OrderedText> texts = OrderedTextUtil.lengthSplit(text, OrderedTextUtil.simpleLengthSplitHeuristic(i << SPLIT_CONST, 4, 8));
                    final double maxHeight = height / texts.size();
                    double scale = Double.MAX_VALUE;
                    for (final OrderedText orderedText : texts) {
                        scale = Math.min(scale, getTextScale(orderedText, width, maxHeight, renderer));
                    }
                    if (scale > bestScale) {
                        bestSplitSize = i;
                        bestScale = scale;
                    }
                }
                return OrderedTextUtil.lengthSplit(text, OrderedTextUtil.simpleLengthSplitHeuristic(bestSplitSize << SPLIT_CONST, 4, 8));
            }

            @Override
            public void draw(final double width, final double height, final OrderedText text, final GuiContext context) {
                final GuiTextRenderer renderer = context.getTextRenderer();
                final List<OrderedText> texts = findBest(text, width, height, renderer);
                final int size = texts.size();
                final double maxHeight = height / size;
                double scaleFactor = 1;
                for (final OrderedText orderedText : texts) {
                    scaleFactor = Math.min(scaleFactor, getTextScale(orderedText, width, maxHeight, renderer));
                }
                for (int i = 0; i < size; i++) {
                    context.pushTranslate(0, maxHeight * i, 0);

                    final double textWidth = renderer.getWidth(text);
                    final double scaledTextWidth = textWidth * scaleFactor;
                    final double scaledTextHeight = renderer.getHeight() * scaleFactor;
                    final double offsetX = (width - scaledTextWidth);
                    final double offsetY = (maxHeight - scaledTextHeight);
                    final double transX = switch (perLineJustification) {
                        case LEFT -> 0;
                        case CENTER -> -scaledTextWidth / 2.0;
                        case RIGHT -> offsetX;
                    };
                    final double transY = -scaledTextHeight / 2.0;
                    if (transX != 0 || transY != 0) {
                        context.pushTranslate(transX, transY, 0);
                    }
                    context.pushScale(scaleFactor, scaleFactor, scaleFactor);
                    renderer.render(text, colour, shadowed, backgroundColour);
                    context.popGuiTransform();
                    if (transX != 0 || transY != 0) {
                        context.popGuiTransform();
                    }

                    context.popGuiTransform();
                }
            }
        };
    }

    public static TextDrawer oneShot(final HorizontalJustification horizontalJustification, final VerticalJustification verticalJustification, final int colour, final int backgroundColour, final boolean shadowed) {
        return (width, height, text, context) -> {
            final GuiTextRenderer renderer = context.getTextRenderer();
            final double scaleFactor = getTextScale(text, width, height, renderer);
            final double textWidth = renderer.getWidth(text);
            final double scaledTextWidth = textWidth * scaleFactor;
            final double scaledTextHeight = renderer.getHeight() * scaleFactor;
            final double offsetX = (width - scaledTextWidth);
            final double offsetY = (height - scaledTextHeight);
            final double transX = switch (horizontalJustification) {
                case LEFT -> 0;
                case CENTER -> -scaledTextWidth / 2.0;
                case RIGHT -> offsetX;
            };
            final double transY = switch (verticalJustification) {
                case TOP -> 0;
                case CENTER -> -scaledTextHeight / 2.0;
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

    public static double getTextScale(final OrderedText text, final double width, final double height, final GuiTextRenderer renderer) {
        final double textWidth = renderer.getWidth(text);
        final double horizontalScaleFactor = width / Math.max(textWidth, 0.001);
        final double verticalScaleFactor = height / Math.max(renderer.getHeight(), 0.001);
        return Math.min(horizontalScaleFactor, verticalScaleFactor);
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
