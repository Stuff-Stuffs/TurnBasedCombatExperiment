package io.github.stuff_stuffs.tbcexutil.common.colour;

public interface RgbColour extends Colour {
    IntRgbColour asInts();

    FloatRgbColour asFloats();

    default HsvColour asHsv() {
        final FloatRgbColour colour = asFloats();
        final float cMin = Math.min(colour.r, Math.min(colour.g, colour.b));
        final float cMax = Math.max(colour.r, Math.max(colour.g, colour.b));
        final float chroma = cMax - cMin;

        final float hue;
        if (chroma == 0) {
            hue = 0;
        } else if (cMax == colour.r) {
            hue = 60 * mod((colour.g - colour.b) / chroma, 6);
        } else if (cMax == colour.g) {
            hue = 60 * mod((colour.b - colour.r) / chroma + 2, 6);
        } else {
            hue = 60 * mod((colour.r - colour.g) / chroma + 2, 6);
        }

        final float saturation;
        if (cMax == 0) {
            saturation = 0;
        } else {
            saturation = chroma / cMax;
        }

        final float value = cMax;
        return new HsvColour(hue, saturation, value);
    }

    private static float mod(final float i, final float m) {
        final float e = i % m;
        if (e < 0) {
            return m + e;
        }
        return e;
    }
}
