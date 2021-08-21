package io.github.stuff_stuffs.tbcexutil.common;

public enum Easing {
    easeInBack {
        @Override
        public double apply(final double x) {
            final double c1 = 1.70158;
            final double c3 = c1 + 1;
            return c3 * x * x * x - c1 * x * x;
        }
    },
    easeOutBack {
        @Override
        public double apply(final double x) {
            final double c1 = 1.70158;
            final double c3 = c1 + 1;
            final double a = x - 1;
            return 1 + c3 * a * a * a + c1 * a * a;
        }
    },
    easeInOutBack {
        @Override
        public double apply(final double x) {
            final double c1 = 1.70158;
            final double c2 = c1 * 1.525;
            final double a = 2 * x;
            final double b = 2 * x - 2;
            return x < 0.5 ? (a * a * ((c2 + 1) * 2 * x - c2)) / 2 : (b * b * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;
        }
    },
    easeInQuad {
        @Override
        public double apply(final double x) {
            return x * x;
        }
    },
    easeOutQuad {
        @Override
        public double apply(final double x) {
            return 1 - (1 - x) * (1 - x);
        }
    },
    easeInOutQuad {
        @Override
        public double apply(final double x) {
            final double a = -2 * x + 2;
            return x < 0.5 ? 2 * x * x : 1 - a * a / 2;
        }
    },
    easeInCubic {
        @Override
        public double apply(final double x) {
            return x * x * x;
        }
    },
    easeOutCubic {
        @Override
        public double apply(final double x) {
            final double a = 1 - x;
            return 1 - a * a * a;
        }
    },
    easeInOutCubic {
        @Override
        public double apply(final double x) {
            final double a = -2 * x + 2;
            return x < 0.5 ? 4 * x * x * x : 1 - a * a * a / 2;
        }
    },
    easeInQuart {
        @Override
        public double apply(final double x) {
            return x * x * x * x;
        }
    },
    easeOutQuart {
        @Override
        public double apply(final double x) {
            final double a = 1 - x;
            return 1 - x * x * x * x;
        }
    },
    easeInOutQuart {
        @Override
        public double apply(final double x) {
            final double a = -2 * x + 2;
            return x < 0.5 ? 8 * x * x * x * x : 1 - a * a * a * a / 2;
        }
    };

    public abstract double apply(double x);
}
