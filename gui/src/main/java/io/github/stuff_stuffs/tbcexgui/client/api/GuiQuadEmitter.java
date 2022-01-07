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
    GuiQuadEmitter colour(int vertexIndex, int color);

    @Override
    GuiQuadEmitter colour(int c0, int c1, int c2, int c3);

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

    default GuiQuadEmitter rectangle(final double x, final double y, final double width, final double height, final int c0, final int c1, final int c2, final int c3) {
        pos(0, (float) x, (float) y);
        pos(1, (float) x, (float) (y + height));
        pos(2, (float) (x + width), (float) (y + height));
        pos(3, (float) (x + width), (float) y);
        renderMaterial(GuiRenderMaterial.POS_COLOUR_TRANSLUCENT);
        colour(c0, c1, c2, c3);
        return this;
    }

    GuiQuadEmitter emit();
}
