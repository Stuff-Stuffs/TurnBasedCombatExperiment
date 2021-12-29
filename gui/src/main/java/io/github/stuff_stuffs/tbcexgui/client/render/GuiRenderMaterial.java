package io.github.stuff_stuffs.tbcexgui.client.render;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

public interface GuiRenderMaterial {
    GuiRenderMaterial POS_COLOUR = GuiRenderMaterialFinder.finder().depthTest(true).ignoreLight(true).ignoreTexture(true).translucent(false).find();
    GuiRenderMaterial POS_COLOUR_TRANSLUCENT = GuiRenderMaterialFinder.finder().depthTest(true).ignoreLight(true).texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).translucent(true).find();
    GuiRenderMaterial POS_COLOUR_TEXTURE = GuiRenderMaterialFinder.finder().depthTest(true).ignoreLight(true).texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).translucent(false).find();
    GuiRenderMaterial POS_COLOUR_TEXTURE_TRANSLUCENT = GuiRenderMaterialFinder.finder().depthTest(true).ignoreLight(true).texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).translucent(true).find();

    boolean depthTest();

    boolean translucent();

    boolean ignoreTexture();

    boolean ignoreLight();

    String shader();

    Identifier texture();

    void remember(Identifier id);
}
