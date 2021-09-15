package io.github.stuff_stuffs.tbcexgui.client.render;

import io.github.stuff_stuffs.tbcexutil.common.CachingFunction;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.client.render.*;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;

import java.util.Map;

public final class GuiRenderLayers extends RenderLayer {
    public static final RenderLayer POSITION_COLOUR_LAYER = RenderLayer.of("gui_position_colour", VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS, 1024, false, true, MultiPhaseParameters.builder().cull(RenderPhase.DISABLE_CULLING).shader(RenderPhase.COLOR_SHADER).transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY).target(RenderPhase.TRANSLUCENT_TARGET).writeMaskState(RenderPhase.COLOR_MASK).build(false));
    public static final CachingFunction<Identifier, RenderLayer> POSITION_COLOUR_TEXTURE_LAYER = new CachingFunction<>(id -> RenderLayer.of("gui_position_colour_texture:" + id, VertexFormats.POSITION_COLOR_TEXTURE, VertexFormat.DrawMode.QUADS, 256, false, true, MultiPhaseParameters.builder().shader(RenderPhase.POSITION_COLOR_TEXTURE_SHADER).cull(RenderPhase.DISABLE_CULLING).texture(new Texture(id, false, false)).transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY).target(RenderPhase.TRANSLUCENT_TARGET).writeMaskState(RenderPhase.COLOR_MASK).build(false)));
    private static final Map<RenderLayer, BufferBuilder> GUI_BUFFERS;
    private static int LAST_BUFFER_COUNT = -1;
    private static final BufferBuilder FALLBACK_BUFFER;
    private static VertexConsumerProvider.Immediate immediate = null;

    public static RenderLayer getPositionColourTextureLayer(final Identifier texture) {
        return POSITION_COLOUR_TEXTURE_LAYER.apply(texture);
    }

    private GuiRenderLayers(final String name, final VertexFormat vertexFormat, final VertexFormat.DrawMode drawMode, final int expectedBufferSize, final boolean hasCrumbling, final boolean translucent, final Runnable startAction, final Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    public static VertexConsumerProvider.Immediate getVertexConsumer() {
        if (immediate == null) {
            immediate = VertexConsumerProvider.immediate(GUI_BUFFERS, FALLBACK_BUFFER);
        }
        return immediate;
    }

    //TODO is this really necessary?
    public static void updateBuffers() {
        if (POSITION_COLOUR_TEXTURE_LAYER.cacheSize() != LAST_BUFFER_COUNT) {
            GUI_BUFFERS.forEach((r, b) -> b.clear());
            GUI_BUFFERS.clear();
            RenderLayer renderLayer = RenderLayer.getText(Style.DEFAULT_FONT_ID);
            BufferBuilder builder = new BufferBuilder(renderLayer.getExpectedBufferSize());
            GuiRenderLayers.GUI_BUFFERS.put(renderLayer, builder);

            renderLayer = RenderLayer.getTextSeeThrough(Style.DEFAULT_FONT_ID);
            builder = new BufferBuilder(renderLayer.getExpectedBufferSize());
            GuiRenderLayers.GUI_BUFFERS.put(renderLayer, builder);

            renderLayer = RenderLayer.getTextPolygonOffset(Style.DEFAULT_FONT_ID);
            builder = new BufferBuilder(renderLayer.getExpectedBufferSize());
            GuiRenderLayers.GUI_BUFFERS.put(renderLayer, builder);

            renderLayer = RenderLayer.getTextIntensity(Style.DEFAULT_FONT_ID);
            builder = new BufferBuilder(renderLayer.getExpectedBufferSize());
            GuiRenderLayers.GUI_BUFFERS.put(renderLayer, builder);

            renderLayer = RenderLayer.getTextIntensitySeeThrough(Style.DEFAULT_FONT_ID);
            builder = new BufferBuilder(renderLayer.getExpectedBufferSize());
            GuiRenderLayers.GUI_BUFFERS.put(renderLayer, builder);

            renderLayer = RenderLayer.getTextIntensityPolygonOffset(Style.DEFAULT_FONT_ID);
            builder = new BufferBuilder(renderLayer.getExpectedBufferSize());
            GuiRenderLayers.GUI_BUFFERS.put(renderLayer, builder);

            renderLayer = POSITION_COLOUR_LAYER;
            builder = new BufferBuilder(renderLayer.getExpectedBufferSize());
            GuiRenderLayers.GUI_BUFFERS.put(renderLayer, builder);

            for (final Map.Entry<Identifier, RenderLayer> entry : POSITION_COLOUR_TEXTURE_LAYER) {
                renderLayer = entry.getValue();
                GuiRenderLayers.GUI_BUFFERS.put(renderLayer, new BufferBuilder(renderLayer.getExpectedBufferSize()));
            }
            LAST_BUFFER_COUNT = POSITION_COLOUR_TEXTURE_LAYER.cacheSize();
        }
    }

    static {
        GUI_BUFFERS = new Reference2ReferenceOpenHashMap<>();
        FALLBACK_BUFFER = new BufferBuilder(4096);
    }
}
