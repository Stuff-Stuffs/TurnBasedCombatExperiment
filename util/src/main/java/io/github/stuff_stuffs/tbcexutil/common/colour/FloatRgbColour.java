package io.github.stuff_stuffs.tbcexutil.common.colour;

public final class FloatRgbColour implements RgbColour {
    public final float r;
    public final float g;
    public final float b;
    private IntRgbColour cache;

    public FloatRgbColour(final int packed) {
        r = (packed >> 16 & 255) / 255.0F;
        g = (packed >> 8 & 255) / 255.0F;
        b = (packed & 255) / 255.0F;
    }

    public FloatRgbColour(final float r, final float g, final float b) {
        if (r < 0 || r > 1 || g < 0 || g > 1 || b < 0 || b > 1) {
            throw new IllegalArgumentException("r, g, and b must be between 0 and 1 inclusive");
        }
        this.r = r;
        this.g = g;
        this.b = b;
    }

    @Override
    public int pack(final int a) {
        if (a < 0 || a > 255) {
            throw new IllegalArgumentException("a must be between 0 and 255 inclusive");
        }
        return a << 24 | Math.round(r * 255.0F) << 16 | Math.round(g * 255.0F) << 8 | Math.round(b * 255.0F);
    }

    @Override
    public IntRgbColour asInts() {
        if (cache == null) {
            cache = new IntRgbColour(pack(0));
        }
        return cache;
    }

    @Override
    public FloatRgbColour asFloats() {
        return this;
    }
}
