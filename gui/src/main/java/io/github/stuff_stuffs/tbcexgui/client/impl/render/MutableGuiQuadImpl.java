package io.github.stuff_stuffs.tbcexgui.client.impl.render;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiQuad;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiRenderMaterial;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiRenderMaterialFinder;
import io.github.stuff_stuffs.tbcexgui.client.api.MutableGuiQuad;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;

public class MutableGuiQuadImpl implements MutableGuiQuad {
    private static final GuiRenderMaterial DEFAULT_RENDER_MATERIAL = GuiRenderMaterialFinder.finder().find();
    private static final VertexModifier[] ROTATIONS = new VertexModifier[]{null, (q, i) -> q.sprite(i, q.spriteV(i), q.spriteU(i)), //90
            (q, i) -> q.sprite(i, 1 - q.spriteU(i), 1 - q.spriteV(i)), //180
            (q, i) -> q.sprite(i, 1 - q.spriteV(i), q.spriteU(i)) // 270
    };
    private final float[] xs = new float[4];
    private final float[] ys = new float[4];
    private final float[] us = new float[4];
    private final float[] vs = new float[4];
    private final int[] colours = new int[4];
    private final int[] lights = new int[4];
    private GuiRenderMaterial renderMaterial = DEFAULT_RENDER_MATERIAL;
    private int tag;
    private float depth;

    @Override
    public int tag() {
        return tag;
    }

    @Override
    public float posByIndex(final int vertexIndex, final int coordinateIndex) {
        return (switch (coordinateIndex) {
            case 0 -> xs;
            case 1 -> ys;
            default -> throw new IllegalArgumentException();
        })[vertexIndex];
    }

    @Override
    public float x(final int vertexIndex) {
        return xs[vertexIndex];
    }

    @Override
    public float y(final int vertexIndex) {
        return ys[vertexIndex];
    }

    @Override
    public float depth() {
        return depth;
    }

    @Override
    public int colour(final int vertexIndex) {
        return colours[vertexIndex];
    }

    @Override
    public float spriteU(final int vertexIndex) {
        return us[vertexIndex];
    }

    @Override
    public float spriteV(final int vertexIndex) {
        return vs[vertexIndex];
    }

    @Override
    public int light(final int vertexIndex) {
        return lights[vertexIndex];
    }

    @Override
    public GuiRenderMaterial renderMaterial() {
        return renderMaterial;
    }

    @Override
    public MutableGuiQuad light(final int vertexIndex, final int light) {
        lights[vertexIndex] = light;
        return this;
    }

    @Override
    public MutableGuiQuad tag(final int tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public MutableGuiQuad depth(final float depth) {
        this.depth = depth;
        return this;
    }

    @Override
    public MutableGuiQuad pos(final int vertexIndex, final float x, final float y) {
        xs[vertexIndex] = x;
        ys[vertexIndex] = y;
        return this;
    }

    @Override
    public MutableGuiQuad colour(final int vertexIndex, final int color) {
        colours[vertexIndex] = color;
        return this;
    }

    @Override
    public MutableGuiQuad sprite(final int vertexIndex, final float u, final float v) {
        us[vertexIndex] = u;
        vs[vertexIndex] = v;
        return this;
    }

    @Override
    public MutableGuiQuad spriteBake(final Sprite sprite, final int bakeFlags) {
        if ((bakeFlags & BAKE_ROTATE_90) != 0) {
            applyModifier(this, ROTATIONS[1]);
        }
        if ((bakeFlags & BAKE_ROTATE_180) != 0) {
            applyModifier(this, ROTATIONS[2]);
        }
        if ((bakeFlags & BAKE_ROTATE_270) != 0) {
            applyModifier(this, ROTATIONS[3]);
        }
        if ((BAKE_FLIP_U & bakeFlags) != 0) {
            applyModifier(this, (q, i) -> q.sprite(i, 1 - q.spriteU(i), q.spriteV(i)));
        }
        if ((BAKE_FLIP_V & bakeFlags) != 0) {
            applyModifier(this, (q, i) -> q.sprite(i, q.spriteU(i), 1 - q.spriteV(i)));
        }
        interpolate(this, sprite);
        return this;
    }

    @Override
    public MutableGuiQuad renderMaterial(final GuiRenderMaterial renderMaterial) {
        this.renderMaterial = renderMaterial;
        return this;
    }

    @Override
    public MutableGuiQuad interpolate(final int vertexIndex, final GuiQuad other, final double w0, final double w1, final double w2, final double w3) {
        sprite(vertexIndex, (float) (other.spriteU(0) * w0 + other.spriteU(1) * w1 + other.spriteU(2) * w2 + other.spriteU(3) * w3), (float) (other.spriteV(0) * w0 + other.spriteV(1) * w1 + other.spriteV(2) * w2 + other.spriteV(3) * w3));
        colour(vertexIndex, interpolateColour(other.colour(0), other.colour(1), other.colour(2), other.colour(3), w0, w1, w2, w3));
        final int blockLight = MathHelper.clamp((int) Math.round(
                LightmapTextureManager.getBlockLightCoordinates(other.light(0)) * w0 +
                        LightmapTextureManager.getBlockLightCoordinates(other.light(1)) * w1 +
                        LightmapTextureManager.getBlockLightCoordinates(other.light(2)) * w2 +
                        LightmapTextureManager.getBlockLightCoordinates(other.light(3)) * w3
        ), 0, 15);
        final int skyLight = MathHelper.clamp((int) Math.round(
                LightmapTextureManager.getSkyLightCoordinates(other.light(0)) * w0 +
                        LightmapTextureManager.getSkyLightCoordinates(other.light(1)) * w1 +
                        LightmapTextureManager.getSkyLightCoordinates(other.light(2)) * w2 +
                        LightmapTextureManager.getSkyLightCoordinates(other.light(3)) * w3
        ), 0, 15);
        lights[vertexIndex] = LightmapTextureManager.pack(blockLight, skyLight);
        return this;
    }

    public void reset() {
        Arrays.fill(xs, 0);
        Arrays.fill(ys, 0);
        Arrays.fill(us, 0);
        Arrays.fill(vs, 0);
        Arrays.fill(colours, 0);
        renderMaterial = DEFAULT_RENDER_MATERIAL;
        tag = 0;
        depth = 0;
    }

    private static int interpolateColour(final int c0, final int c1, final int c2, final int c3, final double w0, final double w1, final double w2, final double w3) {
        return interpolateColourComponent(c0, c1, c2, c3, w0, w1, w2, w3, 0) | interpolateColourComponent(c0, c1, c2, c3, w0, w1, w2, w3, 1) | interpolateColourComponent(c0, c1, c2, c3, w0, w1, w2, w3, 2) | interpolateColourComponent(c0, c1, c2, c3, w0, w1, w2, w3, 3);
    }

    private static int interpolateColourComponent(final int c0, final int c1, final int c2, final int c3, final double w0, final double w1, final double w2, final double w3, final int component) {
        final int shift = component * 8;
        final int mask = 0xFF << shift;
        final int masked0 = (c0 & mask) >> shift;
        final int masked1 = (c1 & mask) >> shift;
        final int masked2 = (c2 & mask) >> shift;
        final int masked3 = (c3 & mask) >> shift;
        return MathHelper.clamp((int) Math.round(masked0 * w0 + masked1 * w1 + masked2 * w2 + masked3 * w3), 0, 255) << shift;
    }

    private static void interpolate(final MutableGuiQuad q, final Sprite sprite) {
        final float uMin = sprite.getMinU();
        final float uSpan = sprite.getMaxU() - uMin;
        final float vMin = sprite.getMinV();
        final float vSpan = sprite.getMaxV() - vMin;

        for (int i = 0; i < 4; i++) {
            q.sprite(i, uMin + q.spriteU(i) * uSpan, vMin + q.spriteV(i) * vSpan);
        }
    }

    private static void applyModifier(final MutableGuiQuad quad, final VertexModifier modifier) {
        for (int i = 0; i < 4; i++) {
            modifier.apply(quad, i);
        }
    }

    @FunctionalInterface
    private interface VertexModifier {
        void apply(MutableGuiQuad quad, int vertexIndex);
    }
}
