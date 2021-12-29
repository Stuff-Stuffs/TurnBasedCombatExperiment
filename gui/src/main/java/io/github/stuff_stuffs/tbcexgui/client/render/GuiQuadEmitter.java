package io.github.stuff_stuffs.tbcexgui.client.render;

import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Vec2f;

public interface GuiQuadEmitter extends MutableGuiQuad {
    GuiQuadEmitter tag(int tag);

    GuiQuadEmitter depth(float depth);

    GuiQuadEmitter light(int vertexIndex, int light);

    GuiQuadEmitter pos(int vertexIndex, float x, float y);

    GuiQuadEmitter pos(int vertexIndex, Vec2f vec);

    GuiQuadEmitter spriteColor(int vertexIndex, int color);

    GuiQuadEmitter spriteColor(int c0, int c1, int c2, int c3);

    GuiQuadEmitter sprite(int vertexIndex, float u, float v);

    GuiQuadEmitter spriteUnitSquare();

    GuiQuadEmitter sprite(int vertexIndex, Vec2f uv);

    GuiQuadEmitter spriteBake(Sprite sprite, int bakeFlags);

    GuiQuadEmitter renderMaterial(GuiRenderMaterial renderMaterial);

    GuiQuadEmitter interpolate(int vertexIndex, GuiQuad other, double w0, double w1, double w2, double w3);

    GuiQuadEmitter emit();
}
