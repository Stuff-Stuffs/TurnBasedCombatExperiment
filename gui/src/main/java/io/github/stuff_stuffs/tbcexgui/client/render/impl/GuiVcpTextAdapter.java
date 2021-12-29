package io.github.stuff_stuffs.tbcexgui.client.render.impl;

import io.github.stuff_stuffs.tbcexgui.client.render.GuiQuad;
import io.github.stuff_stuffs.tbcexgui.client.render.GuiRenderLayers;
import io.github.stuff_stuffs.tbcexgui.client.render.MutableGuiQuad;
import io.github.stuff_stuffs.tbcexgui.mixin.LightmapTextureManagerAccessor;
import io.github.stuff_stuffs.tbcexgui.mixin.RenderPhase$MultiPhaseParametersAccessor;
import io.github.stuff_stuffs.tbcexgui.mixin.RenderPhase$TextureAccessor;
import io.github.stuff_stuffs.tbcexutil.common.StringInterpolator;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import io.github.stuff_stuffs.tbcexutil.common.colour.IntRgbColour;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.Map;
import java.util.Optional;

public class GuiVcpTextAdapter implements VertexConsumerProvider {
    private static final Map<Identifier, RenderLayer> TEXT_RENDER_LAYERS = new Object2ReferenceOpenHashMap<>();
    private static final StringInterpolator TEXT_RENDER_LAYER_NAME = new StringInterpolator("GuiTextRenderLayer,texture={}");
    private final GuiContextImpl context;

    public GuiVcpTextAdapter(GuiContextImpl context) {
        this.context = context;
    }

    private static RenderLayer createTextRenderLayer(Identifier tex) {
        final RenderLayer.MultiPhase renderLayer = RenderLayer.of(
                TEXT_RENDER_LAYER_NAME.interpolate(tex),
                VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
                VertexFormat.DrawMode.QUADS,
                1024,
                false,
                true,
                RenderLayer.MultiPhaseParameters.builder().
                        texture(GuiRenderLayers.getTexture(tex)).
                        shader(GuiRenderLayers.getShader("position_color_tex_lightmap", true)).
                        transparency(GuiRenderLayers.TRANSLUCENT_TRANSPARENCY).
                        writeMaskState(GuiRenderLayers.COLOR_MASK).
                        target(GuiRenderLayers.TRANSLUCENT_TARGET).
                        build(false)
        );
        GuiRenderLayers.addBuffer(renderLayer, 1024);
        return renderLayer;
    }

    @Override
    public VertexConsumer getBuffer(RenderLayer layer) {
        if (layer.getVertexFormat() != VertexFormats.POSITION_COLOR_TEXTURE_LIGHT && layer instanceof RenderLayer.MultiPhase multiPhase) {
            final RenderLayer.MultiPhaseParameters phases = multiPhase.getPhases();
            final RenderPhase.TextureBase texture = ((RenderPhase$MultiPhaseParametersAccessor) (Object) phases).getTexture();
            if (!(texture instanceof RenderPhase.Texture tex)) {
                throw new UnsupportedOperationException("oops");
            }
            final Optional<Identifier> id = ((RenderPhase$TextureAccessor) tex).getId();
            return new Adapter(context.getVertexConsumerForLayer(TEXT_RENDER_LAYERS.computeIfAbsent(id.orElseThrow(() -> new TBCExException("Text renderer without texture!")), GuiVcpTextAdapter::createTextRenderLayer)));
        } else {
            throw new IllegalArgumentException();
        }
    }


    private final class Adapter implements VertexConsumer {
        private final VertexConsumer vertexDelegate;
        private final MutableGuiQuadImpl quadDelegate = new MutableGuiQuadImpl();
        private final int index = 0;

        private Adapter(VertexConsumer vertexDelegate) {
            this.vertexDelegate = vertexDelegate;
        }

        @Override
        public VertexConsumer vertex(double x, double y, double z) {
            quadDelegate.pos(index, (float) x, (float) y);
            quadDelegate.depth((float) z);
            return this;
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha) {
            quadDelegate.spriteColor(index, new IntRgbColour(red, green, blue).pack(alpha));
            return this;
        }

        @Override
        public VertexConsumer texture(float u, float v) {
            quadDelegate.sprite(index, u, v);
            return null;
        }

        @Override
        public VertexConsumer overlay(int u, int v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public VertexConsumer light(int u, int v) {
            quadDelegate.light(index, LightmapTextureManager.pack(u, v));
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void next() {
            NativeImage image = ((LightmapTextureManagerAccessor) MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager()).getImage();
            if (context.transformQuad(quadDelegate)) {
                for (int i = 0; i < 4; i++) {
                    int blockLight = LightmapTextureManager.getBlockLightCoordinates(quadDelegate.light(i));
                    int skyLight = LightmapTextureManager.getSkyLightCoordinates(quadDelegate.light(i));
                    IntRgbColour colour = new IntRgbColour(quadDelegate.spriteColor(i));
                    int a = quadDelegate.spriteColor(i) >>> 24;
                    int r = colour.r;
                    int g = colour.g;
                    int b = colour.b;
                    int rLight = image.getRed(blockLight, skyLight);
                    int gLight = image.getGreen(blockLight, skyLight);
                    int bLight = image.getBlue(blockLight, skyLight);
                    vertexDelegate.vertex(quadDelegate.x(i), quadDelegate.y(i), quadDelegate.depth());
                    vertexDelegate.color(new IntRgbColour((r * rLight) / 255, (g * gLight) / 255, (b * bLight) / 255).pack(a));
                    vertexDelegate.texture(quadDelegate.spriteU(i), quadDelegate.spriteV(i));
                    vertexDelegate.next();
                }
            }
            quadDelegate.reset();
        }

        @Override
        public void fixedColor(int red, int green, int blue, int alpha) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void unfixColor() {
            throw new UnsupportedOperationException();
        }
    }
}
