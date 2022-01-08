package io.github.stuff_stuffs.tbcexgui.client.impl.render;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiRenderMaterial;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiRenderMaterialFinder;
import io.github.stuff_stuffs.tbcexgui.client.impl.GuiContextImpl;
import io.github.stuff_stuffs.tbcexgui.mixin.RenderPhase$MultiPhaseParametersAccessor;
import io.github.stuff_stuffs.tbcexgui.mixin.RenderPhase$TextureAccessor;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import io.github.stuff_stuffs.tbcexutil.common.colour.IntRgbColour;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Optional;

public class GuiVcpTextAdapter implements VertexConsumerProvider {
    private static final Map<Identifier, GuiRenderMaterial> TEXT_RENDER_LAYERS = new Object2ReferenceOpenHashMap<>();
    private final GuiContextImpl context;

    public GuiVcpTextAdapter(final GuiContextImpl context) {
        this.context = context;
    }

    @Override
    public VertexConsumer getBuffer(final RenderLayer layer) {
        if (layer.getVertexFormat() == VertexFormats.POSITION_COLOR_TEXTURE_LIGHT && layer instanceof RenderLayer.MultiPhase multiPhase) {
            final RenderLayer.MultiPhaseParameters phases = multiPhase.getPhases();
            final RenderPhase.TextureBase texture = ((RenderPhase$MultiPhaseParametersAccessor) (Object) phases).getTexture();
            if (!(texture instanceof RenderPhase.Texture tex)) {
                throw new UnsupportedOperationException("oops");
            }
            final Optional<Identifier> id = ((RenderPhase$TextureAccessor) tex).getId();
            return new Adapter(TEXT_RENDER_LAYERS.computeIfAbsent(id.orElseThrow(() -> new TBCExException("Text renderer without texture!")), GuiVcpTextAdapter::createTextRenderLayer));
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static GuiRenderMaterial createTextRenderLayer(final Identifier tex) {
        return GuiRenderMaterialFinder.finder().ignoreTexture(false).depthTest(true).ignoreLight(false).translucent(false).texture(tex).shader("rendertype_text", "rendertype_text").find().remember(new Identifier("tbcexgui", "text_" + tex.getNamespace() + "_" + tex.getPath()));
    }

    private final class Adapter implements VertexConsumer {
        private final MutableGuiQuadImpl quadDelegate = new MutableGuiQuadImpl();
        private final GuiRenderMaterial renderMaterial;
        private int index = 0;

        private Adapter(final GuiRenderMaterial renderMaterial) {
            this.renderMaterial = renderMaterial;
        }

        @Override
        public VertexConsumer vertex(final double x, final double y, final double z) {
            quadDelegate.pos(index, (float) x, (float) y);
            quadDelegate.depth((float) z);
            return this;
        }

        @Override
        public VertexConsumer color(final int red, final int green, final int blue, final int alpha) {
            quadDelegate.colour(index, new IntRgbColour(red, green, blue).pack(alpha));
            return this;
        }

        @Override
        public VertexConsumer texture(final float u, final float v) {
            quadDelegate.sprite(index, u, v);
            return this;
        }

        @Override
        public VertexConsumer overlay(final int u, final int v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public VertexConsumer light(final int u, final int v) {
            quadDelegate.light(index, LightmapTextureManager.pack(u, v));
            return this;
        }

        @Override
        public VertexConsumer normal(final float x, final float y, final float z) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void next() {
            index++;
            if (index == 4) {
                quadDelegate.renderMaterial(renderMaterial);
                if (context.transformQuad(quadDelegate)) {
                    quadDelegate.depth(quadDelegate.depth() - 0.001F);
                    context.acquireDeferred().copy(quadDelegate);
                }
                index = 0;
                quadDelegate.reset();
            }
        }

        @Override
        public void fixedColor(final int red, final int green, final int blue, final int alpha) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void unfixColor() {
            throw new UnsupportedOperationException();
        }
    }
}
