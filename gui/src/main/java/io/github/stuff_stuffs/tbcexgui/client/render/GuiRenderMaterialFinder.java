package io.github.stuff_stuffs.tbcexgui.client.render;

import io.github.stuff_stuffs.tbcexgui.client.render.impl.GuiRenderMaterialFinderImpl;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public interface GuiRenderMaterialFinder {
    static GuiRenderMaterialFinder finder() {
        return new GuiRenderMaterialFinderImpl(false, false, false, false, "position_color_tex_lightmap", "position_color_lightmap", SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
    }

    GuiRenderMaterialFinder depthTest(boolean depthTest);

    GuiRenderMaterialFinder translucent(boolean translucent);

    GuiRenderMaterialFinder ignoreTexture(boolean ignore);

    GuiRenderMaterialFinder shader(String textureShader, String noTextureShader);

    GuiRenderMaterialFinder texture(Identifier identifier);

    GuiRenderMaterialFinder ignoreLight(boolean ignoreLight);

    GuiRenderMaterial find();

    @Nullable GuiRenderMaterial find(Identifier id);
}
