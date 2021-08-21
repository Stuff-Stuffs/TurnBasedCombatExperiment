package io.github.stuff_stuffs.tbcexutil.common;

public final class IdSupplier {
    private int counter = 0;

    public int nextId() {
        return counter++;
    }
}
