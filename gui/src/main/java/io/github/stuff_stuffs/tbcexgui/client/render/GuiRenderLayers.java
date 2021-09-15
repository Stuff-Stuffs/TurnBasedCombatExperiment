package io.github.stuff_stuffs.tbcexgui.client.render;

import net.minecraft.client.render.*;

public final class GuiRenderLayers extends RenderLayer {
    public static final RenderLayer POSITION_COLOUR_LAYER = RenderLayer.of("gui_position_colour", VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS, 1024, false, false, MultiPhaseParameters.builder().cull(RenderPhase.DISABLE_CULLING).shader(RenderPhase.COLOR_SHADER).transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY).target(RenderPhase.TRANSLUCENT_TARGET).build(false));

    private GuiRenderLayers(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }
}
