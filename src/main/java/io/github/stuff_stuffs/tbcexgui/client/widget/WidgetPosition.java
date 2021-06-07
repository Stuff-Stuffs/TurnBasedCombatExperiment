package io.github.stuff_stuffs.tbcexgui.client.widget;

import java.util.function.Supplier;

public interface WidgetPosition {
    double getX();

    double getRawX();

    double getY();

    double getRawY();

    double getZ();

    double getRawZ();

    double getScale();

    static WidgetPosition of(double x, double y, double z, double scale) {
        return new WidgetPosition() {
            @Override
            public double getX() {
                return x*scale;
            }

            @Override
            public double getRawX() {
                return x;
            }

            @Override
            public double getY() {
                return y*scale;
            }

            @Override
            public double getRawY() {
                return y;
            }

            @Override
            public double getZ() {
                return z*scale;
            }

            @Override
            public double getRawZ() {
                return z;
            }

            @Override
            public double getScale() {
                return scale;
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
            public double getRawX() {
                return first.get().getRawX() + second.getRawX();
            }

            @Override
            public double getY() {
                return first.get().getY() + second.getY();
            }

            @Override
            public double getRawY() {
                return first.get().getRawY() + second.getRawY();
            }

            @Override
            public double getZ() {
                return first.get().getZ() + second.getZ();
            }

            @Override
            public double getRawZ() {
                return first.get().getRawZ() + second.getRawZ();
            }

            @Override
            public double getScale() {
                return first.get().getScale() * second.getScale();
            }
        };
    }
}
