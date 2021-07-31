package io.github.stuff_stuffs.tbcexgui.client.widget;

import java.util.function.Supplier;

public interface WidgetPosition {
    double getX();

    double getY();

    double getZ();

    static WidgetPosition of(final double x, final double y, final double z) {
        return new WidgetPosition() {
            @Override
            public double getX() {
                return x;
            }

            @Override
            public double getY() {
                return y;
            }

            @Override
            public double getZ() {
                return z;
            }
        };
    }

    static WidgetPosition combine(final WidgetPosition first, final WidgetPosition second) {
        return combine(() -> first, second);
    }

    static WidgetPosition combine(final Supplier<WidgetPosition> first, final WidgetPosition second) {
        return new WidgetPosition() {
            @Override
            public double getX() {
                return first.get().getX() + second.getX();
            }

            @Override
            public double getY() {
                return first.get().getY() + second.getY();
            }

            @Override
            public double getZ() {
                return first.get().getZ() + second.getZ();
            }
        };
    }
}
