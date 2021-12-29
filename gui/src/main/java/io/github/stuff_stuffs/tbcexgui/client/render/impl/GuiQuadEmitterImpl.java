package io.github.stuff_stuffs.tbcexgui.client.render.impl;

import io.github.stuff_stuffs.tbcexgui.client.render.GuiQuad;
import io.github.stuff_stuffs.tbcexgui.client.render.GuiQuadEmitter;
import io.github.stuff_stuffs.tbcexgui.client.render.GuiRenderMaterial;
import io.github.stuff_stuffs.tbcexgui.client.render.MutableGuiQuad;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Vec2f;

public class GuiQuadEmitterImpl implements GuiQuadEmitter {
    private final VertexConsumerProvider vertexConsumers;
    private final GuiContextImpl context;
    private final MutableGuiQuadImpl delegate;

    public GuiQuadEmitterImpl(VertexConsumerProvider vertexConsumers, GuiContextImpl context, MutableGuiQuadImpl delegate) {
        this.vertexConsumers = vertexConsumers;
        this.context = context;
        this.delegate = delegate;
    }

    @Override
    public GuiQuadEmitterImpl pos(int vertexIndex, Vec2f vec) {
        delegate.pos(vertexIndex, vec);
        return this;
    }

    @Override
    public GuiQuadEmitterImpl spriteColor(int c0, int c1, int c2, int c3) {
        delegate.spriteColor(c0, c1, c2, c3);
        return this;
    }

    @Override
    public GuiQuadEmitterImpl spriteUnitSquare() {
        delegate.spriteUnitSquare();
        return this;
    }

    @Override
    public GuiQuadEmitterImpl sprite(int vertexIndex, Vec2f uv) {
        delegate.sprite(vertexIndex, uv);
        return this;
    }

    @Override
    public int tag() {
        return delegate.tag();
    }

    @Override
    public float posByIndex(int vertexIndex, int coordinateIndex) {
        return delegate.posByIndex(vertexIndex, coordinateIndex);
    }

    @Override
    public float x(int vertexIndex) {
        return delegate.x(vertexIndex);
    }

    @Override
    public float y(int vertexIndex) {
        return delegate.y(vertexIndex);
    }

    @Override
    public float depth() {
        return delegate.depth();
    }

    @Override
    public int spriteColor(int vertexIndex) {
        return delegate.spriteColor(vertexIndex);
    }

    @Override
    public float spriteU(int vertexIndex) {
        return delegate.spriteU(vertexIndex);
    }

    @Override
    public float spriteV(int vertexIndex) {
        return delegate.spriteV(vertexIndex);
    }

    @Override
    public int light(int vertexIndex) {
        return delegate.light(vertexIndex);
    }

    @Override
    public GuiQuadEmitter tag(int tag) {
        delegate.tag(tag);
        return this;
    }

    @Override
    public GuiQuadEmitter depth(float depth) {
        delegate.depth(depth);
        return this;
    }

    @Override
    public GuiQuadEmitter light(int vertexIndex, int light) {
        delegate.light(vertexIndex, light);
        return this;
    }

    @Override
    public GuiRenderMaterial renderMaterial() {
        return delegate.renderMaterial();
    }

    @Override
    public GuiQuadEmitter renderMaterial(GuiRenderMaterial renderMaterial) {
        delegate.renderMaterial(renderMaterial);
        return this;
    }

    @Override
    public GuiQuadEmitter interpolate(int vertexIndex, GuiQuad other, double w0, double w1, double w2, double w3) {
        delegate.interpolate(vertexIndex, other, w0, w1, w2, w3);
        return this;
    }

    @Override
    public GuiQuadEmitter pos(int vertexIndex, float x, float y) {
        delegate.pos(vertexIndex, x, y);
        return this;
    }

    @Override
    public GuiQuadEmitter spriteColor(int vertexIndex, int color) {
        delegate.spriteColor(vertexIndex, color);
        return this;
    }

    @Override
    public GuiQuadEmitter sprite(int vertexIndex, float u, float v) {
        delegate.sprite(vertexIndex, u, v);
        return this;
    }

    @Override
    public GuiQuadEmitter spriteBake(Sprite sprite, int bakeFlags) {
        delegate.spriteBake(sprite, bakeFlags);
        return this;
    }

    @Override
    public GuiQuadEmitter emit() {
        final GuiRenderMaterialImpl renderMaterial = (GuiRenderMaterialImpl) renderMaterial();
        final VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderMaterial.getRenderLayer());
        int colourModifier = renderMaterial.translucent() ? 0 : 0xFF000000;
        if (context.transformQuad(delegate)) {
            for (int i = 0; i < 4; i++) {
                vertexConsumer.vertex(x(i), y(i), depth());
                vertexConsumer.color(spriteColor(i) | colourModifier);
                if (!renderMaterial.ignoreTexture()) {
                    vertexConsumer.texture(spriteU(i), spriteV(i));
                }
                if(!renderMaterial.ignoreLight()) {
                    vertexConsumer.light(light(i));
                }
                vertexConsumer.next();
            }
        }
        delegate.reset();
        return this;
    }

    public void reset() {
        delegate.reset();
    }
}
