package io.github.stuff_stuffs.tbcexutil.common.colour;

public final class IntRgbColour implements RgbColour {
    public static final IntRgbColour WHITE = new IntRgbColour(255, 255, 255);
    public static final IntRgbColour BLACK = new IntRgbColour(0, 0, 0);

    public final int r;
    public final int g;
    public final int b;
    private FloatRgbColour cache;

    public IntRgbColour(final int packed) {
        r = packed >> 16 & 0xFF;
        b = packed >> 8 & 0xFF;
        g = packed & 0xFF;
    }

    public IntRgbColour(final int r, final int g, final int b) {
        if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
            //TODO
            throw new RuntimeException();
        }
        this.r = r;
        this.g = g;
        this.b = b;
    }

    @Override
    public int pack(final int a) {
        if (a < 0 || a > 255) {
            //TODO
            throw new RuntimeException();
        }
        return a << 24 | r << 16 | g << 8 | b;
    }

    @Override
    public IntRgbColour asInts() {
        return this;
    }

    @Override
    public FloatRgbColour asFloats() {
        if (cache == null) {
            cache = new FloatRgbColour(pack(0));
        }
        return cache;
    }
}
