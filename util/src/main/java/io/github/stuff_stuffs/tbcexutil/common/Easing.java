package io.github.stuff_stuffs.tbcexutil.common;

public enum Easing {
    easeInSine {
        @Override
        public double apply(final double x) {
            return 1 - Math.cos((x * Math.PI) / 2);
        }
    },
    easeOutSine {
        @Override
        public double apply(final double x) {
            return Math.sin((x * Math.PI) / 2);
        }
    },
    easeInOutSine {
        @Override
        public double apply(final double x) {
            return -(Math.cos(Math.PI * x) - 1) / 2;
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
            return 1 - a * a * a * a;
        }
    },
    easeInOutQuart {
        @Override
        public double apply(final double x) {
            final double a = -2 * x + 2;
            return x < 0.5 ? 8 * x * x * x * x : 1 - a * a * a * a / 2;
        }
    },
    easeInQuint {
        @Override
        public double apply(final double x) {
            return x * x * x * x * x;
        }
    },
    easeOutQuint {
        @Override
        public double apply(final double x) {
            final double a = 1 - x;
            return 1 - a * a * a * a * a;
        }
    },
    easeInOutQuint {
        @Override
        public double apply(final double x) {
            final double a = -2 * x + 2;
            return x < 0.5 ? 16 * x * x * x * x * x : 1 - a * a * a * a * a / 2;
        }
    },
    easeInExpo {
        @Override
        public double apply(final double x) {
            return x <= 0 ? 0 : Math.pow(2, 10 * x - 10);
        }
    },
    easeOutExpo {
        @Override
        public double apply(final double x) {
            return x >= 1 ? 1 : 1 - Math.pow(2, -10 * x);
        }
    },
    easeInOutExpo {
        @Override
        public double apply(final double x) {
            return x <= 0 ? 0 : x >= 1 ? 1 : x < 0.5 ? Math.pow(2, 20 * x - 10) / 2 : (2 - Math.pow(2, -20 * x + 10)) / 2;
        }
    },
    easeInCirc {
        @Override
        public double apply(final double x) {
            return 1 - Math.sqrt(1 - x * x);
        }
    },
    easeOutCirc {
        @Override
        public double apply(final double x) {
            return Math.sqrt(1 - (x - 1) * (x - 1));
        }
    },
    easeInOutCirc {
        @Override
        public double apply(final double x) {
            final double a = -2 * x + 2;
            return x < 0.5 ? (1 - Math.sqrt(1 - (2 * x) * (2 * x))) / 2 : (Math.sqrt(1 - a * a) + 1) / 2;
        }
    },
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
    easeInElastic {
        @Override
        public double apply(final double x) {
            final double c4 = (2 * Math.PI) / 3;

            return x <= 0 ? 0 : x >= 1 ? 1 : -Math.pow(2, 10 * x - 10) * Math.sin((x * 10 - 10.75) * c4);
        }
    },
    easeOutElastic {
        @Override
        public double apply(final double x) {
            final double c4 = (2 * Math.PI) / 3;

            return x <= 0 ? 0 : x >= 1 ? 1 : Math.pow(2, -10 * x) * Math.sin((x * 10 - 0.75) * c4) + 1;
        }
    },
    easeInOutElastic {
        @Override
        public double apply(final double x) {
            final double c5 = (2 * Math.PI) / 4.5;

            final double sin = Math.sin((20 * x - 11.125) * c5);
            return x <= 0 ? 0 : x >= 1 ? 1 : x < 0.5 ? -(Math.pow(2, 20 * x - 10) * sin) / 2 : (Math.pow(2, -20 * x + 10) * sin) / 2 + 1;
        }
    },
    easeInBounce {
        @Override
        public double apply(double x) {
            final double n1 = 7.5625;
            final double d1 = 2.75;

            if (x < 1 / d1) {
                return 1 - (n1 * x * x);
            } else if (x < 2 / d1) {
                return 1 - (n1 * (x -= 1.5 / d1) * x + 0.75);
            } else if (x < 2.5 / d1) {
                return 1 - (n1 * (x -= 2.25 / d1) * x + 0.9375);
            } else {
                return 1 - (n1 * (x -= 2.625 / d1) * x + 0.984375);
            }
        }
    },
    easeOutBounce {
        @Override
        public double apply(double x) {
            final double n1 = 7.5625;
            final double d1 = 2.75;

            if (x < 1 / d1) {
                return n1 * x * x;
            } else if (x < 2 / d1) {
                return n1 * (x -= 1.5 / d1) * x + 0.75;
            } else if (x < 2.5 / d1) {
                return n1 * (x -= 2.25 / d1) * x + 0.9375;
            } else {
                return n1 * (x -= 2.625 / d1) * x + 0.984375;
            }
        }
    },
    easeInOutBounce {
        @Override
        public double apply(final double x) {
            return x < 0.5 ? (1 - easeOutBounce.apply(1 - 2 * x)) / 2 : (1 + easeOutBounce.apply(2 * x - 1)) / 2;
        }
    };

    public abstract double apply(double x);
}
