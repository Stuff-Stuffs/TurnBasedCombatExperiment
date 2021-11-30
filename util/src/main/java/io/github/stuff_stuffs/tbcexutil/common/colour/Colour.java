package io.github.stuff_stuffs.tbcexutil.common.colour;

public interface Colour {
    Colour WHITE = new IntRgbColour(255, 255, 255);

    int pack(int a);

    default int pack() {
        return pack(255);
    }
}
