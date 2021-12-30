package io.github.stuff_stuffs.tbcexgui.client.api;

public interface GuiQuad {
    int tag();

    float posByIndex(int vertexIndex, int coordinateIndex);

    float x(int vertexIndex);

    float y(int vertexIndex);

    float depth();

    int spriteColor(int vertexIndex);

    float spriteU(int vertexIndex);

    float spriteV(int vertexIndex);

    int light(int vertexIndex);

    GuiRenderMaterial renderMaterial();
}
