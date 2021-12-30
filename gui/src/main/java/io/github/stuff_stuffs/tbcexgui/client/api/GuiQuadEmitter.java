package io.github.stuff_stuffs.tbcexgui.client.api;

import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Vec2f;

public interface GuiQuadEmitter extends MutableGuiQuad {
    @Override
    GuiQuadEmitter light(int vertexIndex, int light);

    @Override
    GuiQuadEmitter tag(int tag);

    @Override
    GuiQuadEmitter depth(float depth);

    @Override
    GuiQuadEmitter pos(int vertexIndex, float x, float y);

    @Override
    GuiQuadEmitter pos(int vertexIndex, Vec2f vec);

    @Override
    GuiQuadEmitter spriteColor(int vertexIndex, int color);

    @Override
    GuiQuadEmitter spriteColor(int c0, int c1, int c2, int c3);

    @Override
    GuiQuadEmitter sprite(int vertexIndex, float u, float v);

    @Override
    GuiQuadEmitter spriteUnitSquare();

    @Override
    GuiQuadEmitter sprite(int vertexIndex, Vec2f uv);

    @Override
    GuiQuadEmitter spriteBake(Sprite sprite, int bakeFlags);

    @Override
    GuiQuadEmitter renderMaterial(GuiRenderMaterial renderMaterial);

    @Override
    GuiQuadEmitter interpolate(int vertexIndex, GuiQuad other, double w0, double w1, double w2, double w3);

    GuiQuadEmitter emit();
}
