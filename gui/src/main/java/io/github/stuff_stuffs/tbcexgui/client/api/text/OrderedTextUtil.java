package io.github.stuff_stuffs.tbcexgui.client.api.text;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntPredicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class OrderedTextUtil {
    private static final TextRenderer TEXT_RENDERER = MinecraftClient.getInstance().textRenderer;

    private OrderedTextUtil() {
    }

    public static OrderedText of(final int codepoint, final Style style) {
        return visitor -> visitor.accept(0, style, codepoint);
    }

    public static OrderedText of(final IntList codepoints, final Style style) {
        return of(codepoints, style, 0);
    }

    public static OrderedText of(final IntList codepoints, final Style style, final int startingIndex) {
        return visitor -> {
            final int len = codepoints.size();
            for (int i = 0; i < len; i++) {
                if (!visitor.accept(startingIndex + i, style, codepoints.getInt(i))) {
                    return false;
                }
            }
            return true;
        };
    }

    public static OrderedText of(final int[] codepoints, final Style style) {
        return of(codepoints, style, 0);
    }

    public static OrderedText of(final int[] codepoints, final Style style, final int startingIndex) {
        return visitor -> {
            for (int i = 0; i < codepoints.length; i++) {
                if (!visitor.accept(startingIndex + i, style, codepoints[i])) {
                    return false;
                }
            }
            return true;
        };
    }

    public static List<OrderedText> split(final OrderedText text, final IntPredicate codepointPredicate) {
        final List<OrderedText> result = new ArrayList<>();
        final OrderedTextAccumulator accumulator = new OrderedTextAccumulator();
        text.accept((index, style, codePoint) -> {
            if (codepointPredicate.test(codePoint)) {
                result.add(accumulator.accumulate());
                accumulator.reset();
            } else {
                accumulator.accept(style, codePoint);
            }
            return true;
        });
        return result;
    }

    public static List<OrderedText> lengthSplit(final OrderedText text, final LengthSplitHeuristic heuristic) {
        final List<OrderedText> result = new ArrayList<>();
        final OrderedTextAccumulator accumulator = new OrderedTextAccumulator();
        text.accept((index, style, codePoint) -> {
            heuristic.accept(codePoint, style);
            if (heuristic.shouldSplit()) {
                result.add(accumulator.accumulate());
                accumulator.reset();
            } else {
                accumulator.accept(style, codePoint);
            }
            return true;
        });
        return result;
    }

    public static LengthSplitHeuristic simpleLengthSplitHeuristic(final double maxLength, final double whitespaceModifier, final double separatorModifier) {
        return new SimpleLengthSplitHeuristic(maxLength, whitespaceModifier, separatorModifier);
    }

    public interface LengthSplitHeuristic {
        void accept(int codepoint, Style style);

        boolean shouldSplit();
    }

    private static final class SimpleLengthSplitHeuristic extends AbstractLengthSplitHeuristic {
        private final double whitespaceModifier;
        private final double separatorModifier;

        private SimpleLengthSplitHeuristic(final double threshold, final double whitespaceModifier, final double separatorModifier) {
            super(threshold);
            this.whitespaceModifier = whitespaceModifier;
            this.separatorModifier = separatorModifier;
        }

        @Override
        protected double scoreCodepoint(final int codepoint, final Style style) {
            return TEXT_RENDERER.getWidth(of(codepoint, style)) / 8.0;
        }

        @Override
        protected double scoreCurrentCodepoint(final int codepoint, final Style style) {
            double mod = 1;
            final char[] chars = Character.toChars(codepoint);
            boolean whitespace = false;
            boolean separator = false;
            for (final char c : chars) {
                if (Character.isWhitespace(c)) {
                    whitespace = true;
                } else if (c == '.' || c == '-' || c == '!' || c == ';' || c == ',' || c == '?') {
                    separator = true;
                }
            }
            if (whitespace) {
                mod *= whitespaceModifier;
            }
            if (separator) {
                mod *= separatorModifier;
            }
            return TEXT_RENDERER.getWidth(of(codepoint, style)) * mod / 8.0;
        }
    }

    public static abstract class AbstractLengthSplitHeuristic implements LengthSplitHeuristic {
        protected final double threshold;
        protected double score;
        protected double modifiedScore;

        protected AbstractLengthSplitHeuristic(final double threshold) {
            this.threshold = threshold;
        }

        @Override
        public void accept(final int codepoint, final Style style) {
            modifiedScore = score + scoreCurrentCodepoint(codepoint, style);
            score = score + scoreCodepoint(codepoint, style);
        }

        @Override
        public boolean shouldSplit() {
            if (modifiedScore >= threshold) {
                score = 0;
                modifiedScore = 0;
                return true;
            }
            return false;
        }

        protected abstract double scoreCodepoint(int codepoint, Style style);

        protected double scoreCurrentCodepoint(final int codepoint, final Style style) {
            return scoreCodepoint(codepoint, style);
        }
    }

    private static final class OrderedTextAccumulator {
        private final List<OrderedText> accumulator = new ArrayList<>();
        private final IntList currentList = new IntArrayList();
        private int lengthAccumulator;
        private Style currentStyle;

        public void reset() {
            accumulator.clear();
            currentList.clear();
            lengthAccumulator = 0;
            currentStyle = null;
        }

        public void accept(final Style style, final int codePoint) {
            if (Objects.equals(style, currentStyle)) {
                currentList.add(codePoint);
            } else {
                if (currentList.size() > 0) {
                    accumulator.add(of(currentList.toIntArray(), currentStyle, lengthAccumulator));
                }
                currentStyle = style;
                currentList.clear();
                currentList.add(codePoint);
            }
            lengthAccumulator++;
        }

        public OrderedText accumulate() {
            return OrderedText.concat(accumulator);
        }
    }
}
