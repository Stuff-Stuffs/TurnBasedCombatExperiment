package io.github.stuff_stuffs.tbcexgui.client.render.impl;

import io.github.stuff_stuffs.tbcexgui.client.render.GuiRenderMaterial;
import net.minecraft.client.render.RenderLayer;

public class GuiRenderMaterialImpl implements GuiRenderMaterial {
    private final boolean depthTest;
    private final boolean translucent;
    private final boolean ignoreTexture;
    private final String shader;
    private final RenderLayer renderLayer;

    public GuiRenderMaterialImpl(boolean depthTest, boolean translucent, boolean ignoreTexture, String shader, RenderLayer renderLayer) {
        this.depthTest = depthTest;
        this.translucent = translucent;
        this.ignoreTexture = ignoreTexture;
        this.shader = shader;
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
    public String shader() {
        return shader;
    }

    public RenderLayer getRenderLayer() {
        return renderLayer;
    }
}
