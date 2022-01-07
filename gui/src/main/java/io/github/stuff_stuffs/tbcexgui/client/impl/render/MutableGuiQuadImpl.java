package io.github.stuff_stuffs.tbcexgui.client.impl.render;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiRenderMaterial;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiRenderMaterialFinder;
import io.github.stuff_stuffs.tbcexgui.client.api.MutableGuiQuad;
import net.minecraft.client.texture.Sprite;

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
