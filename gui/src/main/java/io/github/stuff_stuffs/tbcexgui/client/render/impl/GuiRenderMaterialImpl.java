package io.github.stuff_stuffs.tbcexgui.client.render.impl;

import io.github.stuff_stuffs.tbcexgui.client.render.GuiRenderMaterial;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class GuiRenderMaterialImpl implements GuiRenderMaterial {
    private final boolean depthTest;
    private final boolean translucent;
    private final boolean ignoreTexture;
    private final boolean ignoreLight;
    private final String shader;
    private final Identifier texture;
    private final RenderLayer renderLayer;

    public GuiRenderMaterialImpl(boolean depthTest, boolean translucent, boolean ignoreTexture, boolean ignoreLight, String shader, Identifier texture, RenderLayer renderLayer) {
        this.depthTest = depthTest;
        this.translucent = translucent;
        this.ignoreTexture = ignoreTexture;
        this.ignoreLight = ignoreLight;
        this.shader = shader;
        this.texture = texture;
        this.renderLayer = renderLayer;
    }

    @Override
    public boolean depthTest() {
        return depthTest;
    }

    @Override
    public boolean translucent() {
        return translucent;
    }

    @Override
    public boolean ignoreTexture() {
        return ignoreTexture;
    }

    @Override
    public boolean ignoreLight() {
        return ignoreLight;
    }

    @Override
    public String shader() {
        return shader;
    }

    @Override
    public Identifier texture() {
        return texture;
    }

    @Override
    public void remember(Identifier id) {
        GuiRenderMaterialFinderImpl.remember(id, this);
    }

    public RenderLayer getRenderLayer() {
        return renderLayer;
    }
}
