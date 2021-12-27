package io.github.stuff_stuffs.tbcexgui.client.render.impl;

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
    private final MutableQuadImpl delegate = new MutableQuadImpl();

    public GuiQuadEmitterImpl(VertexConsumerProvider vertexConsumers, GuiContextImpl context) {
        this.vertexConsumers = vertexConsumers;
        this.context = context;
    }

    @Override
    public MutableGuiQuad pos(int vertexIndex, Vec2f vec) {
        return delegate.pos(vertexIndex, vec);
    }

    @Override
    public MutableGuiQuad spriteColor(int c0, int c1, int c2, int c3) {
        return delegate.spriteColor(c0, c1, c2, c3);
    }

    @Override
    public MutableGuiQuad spriteUnitSquare() {
        return delegate.spriteUnitSquare();
    }

    @Override
    public MutableGuiQuad sprite(int vertexIndex, Vec2f uv) {
        return delegate.sprite(vertexIndex, uv);
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
    public MutableGuiQuad tag(int tag) {
        return delegate.tag(tag);
    }

    @Override
    public MutableGuiQuad depth(float depth) {
        return delegate.depth(depth);
    }

    @Override
    public GuiRenderMaterial renderMaterial() {
        return delegate.renderMaterial();
    }

    @Override
    public MutableGuiQuad renderMaterial(GuiRenderMaterial renderMaterial) {
        return delegate.renderMaterial(renderMaterial);
    }

    @Override
    public MutableGuiQuad pos(int vertexIndex, float x, float y) {
        return delegate.pos(vertexIndex, x, y);
    }

    @Override
    public MutableGuiQuad spriteColor(int vertexIndex, int color) {
        return delegate.spriteColor(vertexIndex, color);
    }

    @Override
    public MutableGuiQuad sprite(int vertexIndex, float u, float v) {
        return delegate.sprite(vertexIndex, u, v);
    }

    @Override
    public MutableGuiQuad spriteBake(Sprite sprite, int bakeFlags) {
        return delegate.spriteBake(sprite, bakeFlags);
    }

    @Override
    public MutableGuiQuad emit() {
        final GuiRenderMaterialImpl renderMaterial = (GuiRenderMaterialImpl) renderMaterial();
        final VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderMaterial.getRenderLayer());
        int colourModifier = renderMaterial.translucent() ? 0 : 0xFF000000;
        context.transformQuad(delegate);
        for (int i = 0; i < 4; i++) {
            vertexConsumer.vertex(x(i), y(i), depth());
            vertexConsumer.color(spriteColor(i) | colourModifier);
            if (!renderMaterial.ignoreTexture()) {
                vertexConsumer.texture(spriteU(i), spriteV(i));
            }
            vertexConsumer.next();
        }
        delegate.reset();
        return this;
    }
}
