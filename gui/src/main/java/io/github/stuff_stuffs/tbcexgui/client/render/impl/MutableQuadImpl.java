package io.github.stuff_stuffs.tbcexgui.client.render.impl;

import io.github.stuff_stuffs.tbcexgui.client.render.GuiRenderMaterialFinder;
import io.github.stuff_stuffs.tbcexgui.client.render.GuiRenderMaterial;
import io.github.stuff_stuffs.tbcexgui.client.render.MutableGuiQuad;
import net.minecraft.client.texture.Sprite;

import java.util.Arrays;

public class MutableQuadImpl implements MutableGuiQuad {
    private static final GuiRenderMaterial DEFAULT_RENDER_MATERIAL = GuiRenderMaterialFinder.finder().find();
    private final float[] xs = new float[4];
    private final float[] ys = new float[4];
    private final float[] us = new float[4];
    private final float[] vs = new float[4];
    private final int[] spriteColours = new int[4];
    private GuiRenderMaterial renderMaterial = DEFAULT_RENDER_MATERIAL;
    private int tag;
    private float depth;

    @Override
    public int tag() {
        return tag;
    }

    @Override
    public float posByIndex(int vertexIndex, int coordinateIndex) {
        return (switch (coordinateIndex) {
            case 0 -> xs;
            case 1 -> ys;
            default -> throw new IllegalArgumentException();
        })[vertexIndex];
    }

    @Override
    public float x(int vertexIndex) {
        return xs[vertexIndex];
    }

    @Override
    public float y(int vertexIndex) {
        return ys[vertexIndex];
    }

    @Override
    public float depth() {
        return depth;
    }

    @Override
    public int spriteColor(int vertexIndex) {
        return spriteColours[vertexIndex];
    }

    @Override
    public float spriteU(int vertexIndex) {
        return us[vertexIndex];
    }

    @Override
    public float spriteV(int vertexIndex) {
        return vs[vertexIndex];
    }

    @Override
    public GuiRenderMaterial renderMaterial() {
        return renderMaterial;
    }

    @Override
    public MutableGuiQuad tag(int tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public MutableGuiQuad depth(float depth) {
        this.depth = depth;
        return this;
    }

    @Override
    public MutableGuiQuad pos(int vertexIndex, float x, float y) {
        xs[vertexIndex] = x;
        ys[vertexIndex] = y;
        return this;
    }

    @Override
    public MutableGuiQuad spriteColor(int vertexIndex, int color) {
        spriteColours[vertexIndex] = color;
        return this;
    }

    @Override
    public MutableGuiQuad sprite(int vertexIndex, float u, float v) {
        us[vertexIndex] = u;
        vs[vertexIndex] = v;
        return this;
    }

    @Override
    public MutableGuiQuad spriteBake(Sprite sprite, int bakeFlags) {
        if((bakeFlags&BAKE_ROTATE_90)!=0) {
            applyModifier(this, ROTATIONS[1]);
        }
        if((bakeFlags&BAKE_ROTATE_180)!=0) {
            applyModifier(this, ROTATIONS[2]);
        }
        if((bakeFlags&BAKE_ROTATE_270)!=0) {
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
    public MutableGuiQuad renderMaterial(GuiRenderMaterial renderMaterial) {
        this.renderMaterial = renderMaterial;
        return this;
    }

    public void reset() {
        Arrays.fill(xs, 0);
        Arrays.fill(ys, 0);
        Arrays.fill(us, 0);
        Arrays.fill(vs, 0);
        Arrays.fill(spriteColours, 0);
        renderMaterial = DEFAULT_RENDER_MATERIAL;
        tag = 0;
        depth = 0;
    }

    private static void interpolate(MutableGuiQuad q, Sprite sprite) {
        final float uMin = sprite.getMinU();
        final float uSpan = sprite.getMaxU() - uMin;
        final float vMin = sprite.getMinV();
        final float vSpan = sprite.getMaxV() - vMin;

        for (int i = 0; i < 4; i++) {
            q.sprite(i, uMin + q.spriteU(i) * uSpan, vMin + q.spriteV(i) * vSpan);
        }
    }

    @FunctionalInterface
    private interface VertexModifier {
        void apply(MutableGuiQuad quad, int vertexIndex);
    }

    private static void applyModifier(MutableGuiQuad quad, VertexModifier modifier) {
        for (int i = 0; i < 4; i++) {
            modifier.apply(quad, i);
        }
    }

    private static final VertexModifier[] ROTATIONS = new VertexModifier[] { null, (q, i) -> q.sprite(i, q.spriteV(i), q.spriteU(i)), //90
            (q, i) -> q.sprite(i, 1 - q.spriteU(i), 1 - q.spriteV(i)), //180
            (q, i) -> q.sprite(i, 1 - q.spriteV(i), q.spriteU(i)) // 270
    };
}
