package io.github.stuff_stuffs.tbcexgui.client.render;

import io.github.stuff_stuffs.tbcexgui.client.render.impl.GuiRenderMaterialFinderImpl;

public interface GuiRenderMaterialFinder {
    static GuiRenderMaterialFinder finder() {
        return new GuiRenderMaterialFinderImpl(false, false, false, "position_color_texture", "position_color");
    }

    GuiRenderMaterialFinder depthTest(boolean depthTest);

    GuiRenderMaterialFinder translucent(boolean translucent);

    GuiRenderMaterialFinder ignoreTexture(boolean ignore);

    GuiRenderMaterialFinder shader(String textureShader, String noTextureShader);

    GuiRenderMaterial find();
}
