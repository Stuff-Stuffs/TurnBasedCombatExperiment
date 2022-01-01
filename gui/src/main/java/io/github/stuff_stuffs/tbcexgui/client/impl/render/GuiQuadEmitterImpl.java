package io.github.stuff_stuffs.tbcexgui.client.impl.render;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiQuad;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiQuadEmitter;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiRenderMaterial;
import io.github.stuff_stuffs.tbcexgui.client.impl.GuiContextImpl;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Vec2f;

public class GuiQuadEmitterImpl implements GuiQuadEmitter {
    private final GuiContextImpl context;
    private final MutableGuiQuadImpl delegate;

    public GuiQuadEmitterImpl(final GuiContextImpl context, final MutableGuiQuadImpl delegate) {
        this.context = context;
        this.delegate = delegate;
    }

    @Override
    public int tag() {
        return delegate.tag();
    }

    @Override
    public float posByIndex(final int vertexIndex, final int coordinateIndex) {
        return delegate.posByIndex(vertexIndex, coordinateIndex);
    }

    @Override
    public float x(final int vertexIndex) {
        return delegate.x(vertexIndex);
    }

    @Override
    public float y(final int vertexIndex) {
        return delegate.y(vertexIndex);
    }

    @Override
    public float depth() {
        return delegate.depth();
    }

    @Override
    public int colour(final int vertexIndex) {
        return delegate.colour(vertexIndex);
    }

    @Override
    public float spriteU(final int vertexIndex) {
        return delegate.spriteU(vertexIndex);
    }

    @Override
    public float spriteV(final int vertexIndex) {
        return delegate.spriteV(vertexIndex);
    }

    @Override
    public int light(final int vertexIndex) {
        return delegate.light(vertexIndex);
    }

    @Override
    public GuiRenderMaterial renderMaterial() {
        return delegate.renderMaterial();
    }

    @Override
    public GuiQuadEmitter light(final int vertexIndex, final int light) {
        delegate.light(vertexIndex, light);
        return this;
    }

    @Override
    public GuiQuadEmitter tag(final int tag) {
        delegate.tag(tag);
        return this;
    }

    @Override
    public GuiQuadEmitter depth(final float depth) {
        delegate.depth(depth);
        return this;
    }

    @Override
    public GuiQuadEmitter pos(final int vertexIndex, final float x, final float y) {
        delegate.pos(vertexIndex, x, y);
        return this;
    }

    @Override
    public GuiQuadEmitterImpl pos(final int vertexIndex, final Vec2f vec) {
        delegate.pos(vertexIndex, vec);
        return this;
    }

    @Override
    public GuiQuadEmitter colour(final int vertexIndex, final int color) {
        delegate.colour(vertexIndex, color);
        return this;
    }

    @Override
    public GuiQuadEmitterImpl colour(final int c0, final int c1, final int c2, final int c3) {
        delegate.colour(c0, c1, c2, c3);
        return this;
    }

    @Override
    public GuiQuadEmitter sprite(final int vertexIndex, final float u, final float v) {
        delegate.sprite(vertexIndex, u, v);
        return this;
    }

    @Override
    public GuiQuadEmitterImpl spriteUnitSquare() {
        delegate.spriteUnitSquare();
        return this;
    }

    @Override
    public GuiQuadEmitterImpl sprite(final int vertexIndex, final Vec2f uv) {
        delegate.sprite(vertexIndex, uv);
        return this;
    }

    @Override
    public GuiQuadEmitter spriteBake(final Sprite sprite, final int bakeFlags) {
        delegate.spriteBake(sprite, bakeFlags);
        return this;
    }

    @Override
    public GuiQuadEmitter renderMaterial(final GuiRenderMaterial renderMaterial) {
        delegate.renderMaterial(renderMaterial);
        return this;
    }

    @Override
    public GuiQuadEmitter interpolate(final int vertexIndex, final GuiQuad other, final double w0, final double w1, final double w2, final double w3) {
        delegate.interpolate(vertexIndex, other, w0, w1, w2, w3);
        return this;
    }

    @Override
    public GuiQuadEmitter emit() {
        if (context.transformQuad(delegate)) {
            context.acquireDeferred().copy(delegate);
        }
        delegate.reset();
        return this;
    }

    public void reset() {
        delegate.reset();
    }
}
