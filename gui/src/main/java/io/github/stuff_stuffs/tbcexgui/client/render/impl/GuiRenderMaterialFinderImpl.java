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
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class GuiRenderMaterialFinderImpl implements GuiRenderMaterialFinder {
    private static final MaterialFactory[] FACTORIES;
    private static final Map<Identifier, GuiRenderMaterialImpl> NAMED_MATERIALS = new Object2ReferenceOpenHashMap<>();
    private final boolean depthTest;
    private final boolean translucent;
    private final boolean ignoreTexture;
    private final boolean ignoreLight;
    private final String textureShader;
    private final String noTextureShader;
    private final Identifier texture;

    public GuiRenderMaterialFinderImpl(boolean depthTest, boolean translucent, boolean ignoreTexture, boolean ignoreLight, String textureShader, String noTextureShader, Identifier texture) {
        this.depthTest = depthTest;
        this.translucent = translucent;
        this.ignoreTexture = ignoreTexture;
        this.ignoreLight = ignoreLight;
        this.textureShader = textureShader;
        this.noTextureShader = noTextureShader;
        this.texture = texture;
    }

    public static void remember(Identifier id, GuiRenderMaterialImpl material) {
        if(NAMED_MATERIALS.putIfAbsent(id, material)==null) {
            GuiRenderLayers.addBuffer(material.getRenderLayer(), 1024);
        }
    }

    public static @Nullable GuiRenderMaterialImpl retrieve(Identifier id) {
        return NAMED_MATERIALS.get(id);
    }

    private static int getFactoryIndex(boolean depthTest, boolean translucent, boolean ignoreTexture, boolean ignoreLight) {
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
        if (ignoreLight) {
            key |= 8;
        }
        return key;
    }

    @Override
    public GuiRenderMaterialFinder depthTest(boolean depthTest) {
        return new GuiRenderMaterialFinderImpl(depthTest, translucent, ignoreTexture, ignoreLight, textureShader, noTextureShader, texture);
    }

    @Override
    public GuiRenderMaterialFinder translucent(boolean translucent) {
        return new GuiRenderMaterialFinderImpl(depthTest, translucent, ignoreTexture, ignoreLight, textureShader, noTextureShader, texture);
    }

    @Override
    public GuiRenderMaterialFinder ignoreTexture(boolean ignore) {
        return new GuiRenderMaterialFinderImpl(depthTest, translucent, ignoreTexture, ignoreLight, textureShader, noTextureShader, texture);
    }

    @Override
    public GuiRenderMaterialFinder shader(String textureShader, String noTextureShader) {
        return new GuiRenderMaterialFinderImpl(depthTest, translucent, ignoreTexture, ignoreLight, textureShader, noTextureShader, texture);
    }

    @Override
    public GuiRenderMaterialFinder texture(Identifier texture) {
        return new GuiRenderMaterialFinderImpl(depthTest, translucent, ignoreTexture, ignoreLight, textureShader, noTextureShader, texture);
    }

    @Override
    public GuiRenderMaterialFinder ignoreLight(boolean ignoreLight) {
        return new GuiRenderMaterialFinderImpl(depthTest, translucent, ignoreTexture, ignoreLight, textureShader, noTextureShader, texture);
    }

    @Override
    public GuiRenderMaterial find() {
        return FACTORIES[getFactoryIndex(depthTest, translucent, ignoreTexture, ignoreLight)].create(ignoreTexture ? noTextureShader : textureShader, texture);
    }

    @Override
    public @Nullable GuiRenderMaterial find(Identifier id) {
        return retrieve(id);
    }

    static {
        FACTORIES = new MaterialFactory[16];
        FACTORIES[getFactoryIndex(false, false, false, false)] = new MaterialFactory(false, false, false, false);
        FACTORIES[getFactoryIndex(false, false, true, false)] = new MaterialFactory(false, false, true, false);
        FACTORIES[getFactoryIndex(false, true, false, false)] = new MaterialFactory(false, true, false, false);
        FACTORIES[getFactoryIndex(false, true, true, false)] = new MaterialFactory(false, true, true, false);
        FACTORIES[getFactoryIndex(true, false, false, false)] = new MaterialFactory(true, false, false, false);
        FACTORIES[getFactoryIndex(true, false, true, false)] = new MaterialFactory(true, false, true, false);
        FACTORIES[getFactoryIndex(true, true, false, false)] = new MaterialFactory(true, true, false, false);
        FACTORIES[getFactoryIndex(true, true, true, false)] = new MaterialFactory(true, true, true, false);
        FACTORIES[getFactoryIndex(false, false, false, true)] = new MaterialFactory(false, false, false, true);
        FACTORIES[getFactoryIndex(false, false, true, true)] = new MaterialFactory(false, false, true, true);
        FACTORIES[getFactoryIndex(false, true, false, true)] = new MaterialFactory(false, true, false, true);
        FACTORIES[getFactoryIndex(false, true, true, true)] = new MaterialFactory(false, true, true, true);
        FACTORIES[getFactoryIndex(true, false, false, true)] = new MaterialFactory(true, false, false, true);
        FACTORIES[getFactoryIndex(true, false, true, true)] = new MaterialFactory(true, false, true, true);
        FACTORIES[getFactoryIndex(true, true, false, true)] = new MaterialFactory(true, true, false, true);
        FACTORIES[getFactoryIndex(true, true, true, true)] = new MaterialFactory(true, true, true, true);
    }

    private static class MaterialFactory {
        private static final StringInterpolator RENDER_LAYER_NAME = new StringInterpolator("gui{DepthTest={},Translucent={},IgnoreTexture={},Shader={}}");
        private final boolean depthTest;
        private final boolean translucent;
        private final boolean ignoreTexture;
        private final boolean ignoreLight;
        private final Map<String, RenderLayer> cache;

        private MaterialFactory(boolean depthTest, boolean translucent, boolean ignoreTexture, boolean ignoreLight) {
            this.depthTest = depthTest;
            this.translucent = translucent;
            this.ignoreTexture = ignoreTexture;
            this.ignoreLight = ignoreLight;
            cache = new Object2ReferenceOpenHashMap<>(2);
        }

        public GuiRenderMaterialImpl create(String shader, Identifier texture) {
            return new GuiRenderMaterialImpl(depthTest, translucent, ignoreTexture, ignoreLight, shader, texture, createRenderLayer(shader, texture));
        }

        private RenderLayer createRenderLayer(String shader, Identifier texture) {
            return cache.computeIfAbsent(shader, s -> RenderLayer.of(
                            RENDER_LAYER_NAME.interpolate(depthTest, translucent, ignoreTexture, s),
                            getVertexFormat(),
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

        private VertexFormat getVertexFormat() {
            if (ignoreTexture) {
                return ignoreLight ? VertexFormats.POSITION_COLOR : VertexFormats.POSITION_COLOR_LIGHT;
            } else {
                return ignoreLight ? VertexFormats.POSITION_COLOR_TEXTURE : VertexFormats.POSITION_COLOR_TEXTURE_LIGHT;
            }
        }
    }
}
