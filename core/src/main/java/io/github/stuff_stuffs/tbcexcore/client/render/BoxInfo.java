package io.github.stuff_stuffs.tbcexcore.client.render;

public final class BoxInfo {
    public final double x0, y0, z0;
    public final double x1, y1, z1;
    public final double r, g, b, a;

    public BoxInfo(final double x0, final double y0, final double z0, final double x1, final double y1, final double z1, final double r, final double g, final double b, final double a) {
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
}
