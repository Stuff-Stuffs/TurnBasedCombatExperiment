package io.github.stuff_stuffs.tbcexutil.common.colour;

public interface Colour {
    int pack(int a);

    default int pack() {
        return pack(255);
    }
}
