package io.github.stuff_stuffs.tbcexutil.common.colour;

public final class HsvColour implements Colour {
    public final float h;
    public final float s;
    public final float v;
    private RgbColour cache;

    public HsvColour(final float h, final float s, final float v) {
        if (h < 0 || h > 360 || s < 0 || s > 1 || v < 0 || v > 255) {
            //TODO
            throw new RuntimeException();
        }
        this.h = h;
        this.s = s;
        this.v = v;
    }

    @Override
    public int pack(final int a) {
        return asRgb().pack(a);
    }

    public RgbColour asRgb() {
        if (cache == null) {
            final float c = v * s;
            final float x = c * (1 - Math.abs((h / 60) % 2 - 1));
            final float m = v - c;
            final float r;
            final float g;
            final float b;
            if (h < 60) {
                r = c;
                g = x;
                b = 0;
            } else if (h < 120) {
                r = x;
                g = c;
                b = 0;
            } else if (h < 180) {
                r = 0;
                g = c;
                b = x;
            } else if (h < 240) {
                r = 0;
                g = x;
                b = c;
            } else if (h < 300) {
                r = x;
                g = 0;
                b = c;
            } else {
                r = c;
                g = 0;
                b = x;
            }
            cache = new FloatRgbColour(r + m, g + m, b + m);
        }
        return cache;
    }
}
