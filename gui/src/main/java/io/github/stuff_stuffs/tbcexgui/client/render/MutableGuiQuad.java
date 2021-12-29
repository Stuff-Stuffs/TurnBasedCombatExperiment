package io.github.stuff_stuffs.tbcexgui.client.render;

import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Vec2f;

public interface MutableGuiQuad extends GuiQuad {
    int BAKE_ROTATE_NONE = 0;
    int BAKE_ROTATE_90 = 1;
    int BAKE_ROTATE_180 = 2;
    int BAKE_ROTATE_270 = 3;
    int BAKE_FLIP_U = 4;
    int BAKE_FLIP_V = 8;

    MutableGuiQuad light(int vertexIndex, int light);

    MutableGuiQuad tag(int tag);

    MutableGuiQuad depth(float depth);

    MutableGuiQuad pos(int vertexIndex, float x, float y);

    default MutableGuiQuad pos(int vertexIndex, Vec2f vec) {
        return pos(vertexIndex, vec.x, vec.y);
    }

    MutableGuiQuad spriteColor(int vertexIndex, int color);

    default MutableGuiQuad spriteColor(int c0, int c1, int c2, int c3) {
        spriteColor(0, c0);
        spriteColor(1, c1);
        spriteColor(2, c2);
        spriteColor(3, c3);
        return this;
    }

    MutableGuiQuad sprite(int vertexIndex, float u, float v);

    default MutableGuiQuad spriteUnitSquare() {
        sprite(0, 0, 0);
        sprite(1, 0, 1);
        sprite(2, 1, 1);
        sprite(3, 1, 0);
        return this;
    }

    default MutableGuiQuad sprite(int vertexIndex, Vec2f uv) {
        return sprite(vertexIndex, uv.x, uv.y);
    }

    MutableGuiQuad spriteBake(Sprite sprite, int bakeFlags);

    MutableGuiQuad renderMaterial(GuiRenderMaterial renderMaterial);

    MutableGuiQuad interpolate(int vertexIndex, GuiQuad other, double w0, double w1, double w2, double w3);
}
