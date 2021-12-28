package io.github.stuff_stuffs.tbcexgui.client.render.impl;

import io.github.stuff_stuffs.tbcexgui.client.render.GuiRenderLayers;
import io.github.stuff_stuffs.tbcexgui.client.render.GuiRenderMaterial;
import io.github.stuff_stuffs.tbcexgui.client.render.GuiRenderMaterialFinder;
import io.github.stuff_stuffs.tbcexutil.common.StringInterpolator;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

import java.util.Map;

public class GuiRenderMaterialFinderImpl implements GuiRenderMaterialFinder {
    private static final MaterialFactory[] FACTORIES;
    private final boolean depthTest;
    private final boolean translucent;
    private final boolean ignoreTexture;
    private final String textureShader;
    private final String noTextureShader;
    private final Identifier texture;

    public GuiRenderMaterialFinderImpl(boolean depthTest, boolean translucent, boolean ignoreTexture, String textureShader, String noTextureShader, Identifier texture) {
        this.depthTest = depthTest;
        this.translucent = translucent;
        this.ignoreTexture = ignoreTexture;
        this.textureShader = textureShader;
        this.noTextureShader = noTextureShader;
        this.texture = texture;
    }

    private static int getFactoryIndex(boolean depthTest, boolean translucent, boolean ignoreTexture) {
        int key = 0;
        if (depthTest) {
            key |= 1;
        }
        if (translucent) {
            key |= 2;
        }
        if (ignoreTexture) {
            key |= 4;
        }
        return key;
    }

    @Override
    public GuiRenderMaterialFinder depthTest(boolean depthTest) {
        return new GuiRenderMaterialFinderImpl(depthTest, translucent, ignoreTexture, textureShader, noTextureShader, texture);
    }

    @Override
    public GuiRenderMaterialFinder translucent(boolean translucent) {
        return new GuiRenderMaterialFinderImpl(depthTest, translucent, ignoreTexture, textureShader, noTextureShader, texture);
    }

    @Override
    public GuiRenderMaterialFinder ignoreTexture(boolean ignore) {
        return new GuiRenderMaterialFinderImpl(depthTest, translucent, ignoreTexture, textureShader, noTextureShader, texture);
    }

    @Override
    public GuiRenderMaterialFinder shader(String textureShader, String noTextureShader) {
        return new GuiRenderMaterialFinderImpl(depthTest, translucent, ignoreTexture, textureShader, noTextureShader, texture);
    }

    @Override
    public GuiRenderMaterialFinder texture(Identifier identifier) {
        return new GuiRenderMaterialFinderImpl(depthTest, translucent, ignoreTexture, textureShader, noTextureShader, identifier);
    }

    @Override
    public GuiRenderMaterial find() {
        return FACTORIES[getFactoryIndex(depthTest, translucent, ignoreTexture)].create(ignoreTexture ? noTextureShader : textureShader, texture);
    }

    static {
        FACTORIES = new MaterialFactory[8];
        FACTORIES[getFactoryIndex(false, false, false)] = new MaterialFactory(false, false, false);
        FACTORIES[getFactoryIndex(false, false, true)] = new MaterialFactory(false, false, true);
        FACTORIES[getFactoryIndex(false, true, false)] = new MaterialFactory(false, true, false);
        FACTORIES[getFactoryIndex(false, true, true)] = new MaterialFactory(false, true, true);

        FACTORIES[getFactoryIndex(true, false, false)] = new MaterialFactory(true, false, false);
        FACTORIES[getFactoryIndex(true, false, true)] = new MaterialFactory(true, false, true);
        FACTORIES[getFactoryIndex(true, true, false)] = new MaterialFactory(true, true, false);
        FACTORIES[getFactoryIndex(true, true, true)] = new MaterialFactory(true, true, true);
    }

    private static class MaterialFactory {
        private static final StringInterpolator RENDER_LAYER_NAME = new StringInterpolator("gui{DepthTest={},Translucent={},IgnoreTexture={},Shader={}}");
        private final boolean depthTest;
        private final boolean translucent;
        private final boolean ignoreTexture;
        private final Map<String, RenderLayer> cache;

        private MaterialFactory(boolean depthTest, boolean translucent, boolean ignoreTexture) {
            this.depthTest = depthTest;
            this.translucent = translucent;
            this.ignoreTexture = ignoreTexture;
            cache = new Object2ReferenceOpenHashMap<>(2);
        }

        public GuiRenderMaterialImpl create(String shader, Identifier texture) {
            return new GuiRenderMaterialImpl(depthTest, translucent, ignoreTexture, shader, texture, createRenderLayer(shader, texture));
        }

        private RenderLayer createRenderLayer(String shader, Identifier texture) {
            return cache.computeIfAbsent(shader, s -> RenderLayer.of(
                            RENDER_LAYER_NAME.interpolate(depthTest, translucent, ignoreTexture, s),
                            ignoreTexture ? VertexFormats.POSITION_COLOR : VertexFormats.POSITION_COLOR_TEXTURE,
                            VertexFormat.DrawMode.QUADS,
                            1024,
                            false,
                            translucent,
                            RenderLayer.MultiPhaseParameters.builder().
                                    shader(GuiRenderLayers.getShader(s, !ignoreTexture)).
                                    depthTest(depthTest ? GuiRenderLayers.DEPTH_TEST : GuiRenderLayers.NO_DEPTH_TEST).
                                    texture(GuiRenderLayers.getTexture(texture)).
                                    target(translucent ? GuiRenderLayers.TRANSLUCENT_TARGET : GuiRenderLayers.MAIN_TARGET).
                                    transparency(translucent ? GuiRenderLayers.TRANSLUCENT_TRANSPARENCY : GuiRenderLayers.NO_TRANSPARENCY).
                                    writeMaskState(translucent ? GuiRenderLayers.COLOR_MASK : GuiRenderLayers.ALL_MASK).
                                    build(false)
                    )
            );
        }
    }
}
