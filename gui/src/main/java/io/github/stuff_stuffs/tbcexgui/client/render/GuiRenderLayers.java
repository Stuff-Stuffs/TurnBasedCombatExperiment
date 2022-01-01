package io.github.stuff_stuffs.tbcexgui.client.render;

import io.github.stuff_stuffs.tbcexutil.common.CachingFunction;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class GuiRenderLayers extends RenderPhase {
    public static final RenderPhase.DepthTest NO_DEPTH_TEST = RenderPhase.ALWAYS_DEPTH_TEST;
    public static final RenderPhase.DepthTest DEPTH_TEST = RenderPhase.LEQUAL_DEPTH_TEST;
    public static final RenderPhase.Target MAIN_TARGET = RenderPhase.MAIN_TARGET;
    public static final RenderPhase.Target TRANSLUCENT_TARGET = RenderPhase.TRANSLUCENT_TARGET;
    public static final RenderPhase.Transparency NO_TRANSPARENCY = RenderPhase.NO_TRANSPARENCY;
    public static final RenderPhase.Transparency TRANSLUCENT_TRANSPARENCY = RenderPhase.TRANSLUCENT_TRANSPARENCY;
    public static final RenderPhase.WriteMaskState ALL_MASK = RenderPhase.ALL_MASK;
    public static final RenderPhase.WriteMaskState COLOR_MASK = RenderPhase.COLOR_MASK;
    private static final RenderPhase.WriteMaskState NONE_MASK = new WriteMaskState(false, false);
    public static final RenderLayer STENCIL_LAYER = RenderLayer.of("tbcex_gui_stencil", VertexFormats.POSITION, VertexFormat.DrawMode.QUADS, 1024, false, false, RenderLayer.MultiPhaseParameters.builder().depthTest(NO_DEPTH_TEST).shader(RenderPhase.POSITION_SHADER).writeMaskState(NONE_MASK).build(false));
    private static final Map<String, net.minecraft.client.render.Shader> SHADERS_TEXTURE = new Object2ReferenceOpenHashMap<>();
    private static final Map<String, net.minecraft.client.render.Shader> SHADERS_NO_TEXTURE = new Object2ReferenceOpenHashMap<>();
    private static final Map<String, RenderPhase.Shader> SHADERS_TEXTURE_PHASE = new Object2ReferenceOpenHashMap<>();
    private static final Map<String, RenderPhase.Shader> SHADERS_NO_TEXTURE_PHASE = new Object2ReferenceOpenHashMap<>();
    private static final Map<Identifier, RenderPhase.Texture> TEXTURE_MAP = new Object2ReferenceOpenHashMap<>();
    private static final Map<RenderLayer, BufferBuilder> BUFFERS = new Reference2ObjectOpenHashMap<>();
    private static final Function<Identifier, RenderLayer> POS_COLOUR_TEX_LAYER = new CachingFunction<>(id -> RenderLayer.of("tbcex_gui_pos_colour_transparent", VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS, 1024, false, true, RenderLayer.MultiPhaseParameters.builder().writeMaskState(ALL_MASK).target(MAIN_TARGET).transparency(NO_TRANSPARENCY).shader(RenderPhase.POSITION_COLOR_TEXTURE_SHADER).texture(getTexture(id)).build(false)));
    private static final Function<Identifier, RenderLayer> POS_COLOUR_TEX_LAYER_TRANSPARENT = new CachingFunction<>(id -> RenderLayer.of("tbcex_gui_pos_colour_transparent", VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS, 1024, false, true, RenderLayer.MultiPhaseParameters.builder().writeMaskState(COLOR_MASK).target(TRANSLUCENT_TARGET).transparency(TRANSLUCENT_TRANSPARENCY).shader(RenderPhase.POSITION_COLOR_TEXTURE_SHADER).texture(getTexture(id)).build(false)));
    private static final RenderPhase.Shader POS_COLOUR_SHADER = new Shader(GameRenderer::getPositionColorShader);
    public static final RenderLayer POSITION_COLOUR_TRANSPARENT_LAYER = RenderLayer.of("tbcex_gui_pos_colour_transparent", VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS, 1024, false, true, RenderLayer.MultiPhaseParameters.builder().writeMaskState(COLOR_MASK).target(TRANSLUCENT_TARGET).transparency(TRANSLUCENT_TRANSPARENCY).shader(POS_COLOUR_SHADER).build(false));
    private static final VertexConsumerProvider.Immediate VERTEX_CONSUMERS = VertexConsumerProvider.immediate(BUFFERS, new BufferBuilder(1024));
    private static ResourceManager RESOURCE_MANAGER;

    private GuiRenderLayers(final String name, final Runnable beginAction, final Runnable endAction) {
        super(name, beginAction, endAction);
    }

    public static void addBuffer(final RenderLayer renderLayer, final int capacity) {
        if (!BUFFERS.containsKey(renderLayer)) {
            BUFFERS.put(renderLayer, new BufferBuilder(capacity));
        }
    }

    public static VertexConsumerProvider.Immediate getVertexConsumers() {
        return VERTEX_CONSUMERS;
    }

    public static RenderPhase.Shader getShader(final String shaderName, final boolean texture) {
        if (texture) {
            return SHADERS_TEXTURE_PHASE.computeIfAbsent(shaderName, s -> createShaderPhase(s, true));
        } else {
            return SHADERS_NO_TEXTURE_PHASE.computeIfAbsent(shaderName, s -> createShaderPhase(s, false));
        }
    }

    public static RenderPhase.Texture getTexture(final Identifier id) {
        return TEXTURE_MAP.computeIfAbsent(id, identifier -> new Texture(identifier, false, false));
    }

    private static RenderPhase.Shader createShaderPhase(final String shaderName, final boolean texture) {
        if (texture ? !SHADERS_TEXTURE.containsKey(shaderName) : !SHADERS_NO_TEXTURE.containsKey(shaderName)) {
            createShader(shaderName, texture);
        }
        return new Shader(texture ? () -> SHADERS_TEXTURE.get(shaderName) : () -> SHADERS_NO_TEXTURE.get(shaderName));
    }

    private static void createShader(final String name, final boolean texture) {
        if (SHADERS_TEXTURE.containsKey(name) || SHADERS_NO_TEXTURE.containsKey(name)) {
            throw new TBCExException("Tried to load shader in NO_TEXTURE and TEXTURE formats");
        }
        final net.minecraft.client.render.Shader shader;
        final GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;
        if (gameRenderer.getShader(name) != null) {
            shader = gameRenderer.getShader(name);
        } else {
            try {
                shader = new net.minecraft.client.render.Shader(RESOURCE_MANAGER, name, texture ? VertexFormats.POSITION_COLOR_TEXTURE : VertexFormats.POSITION_COLOR);
            } catch (final IOException e) {
                throw new TBCExException("Error while loading shader", e);
            }
        }
        if (texture) {
            SHADERS_TEXTURE.put(name, shader);
        } else {
            SHADERS_NO_TEXTURE.put(name, shader);
        }
    }

    public static void setResourceManager(final ResourceManager resourceManager) {
        RESOURCE_MANAGER = resourceManager;
        final List<String> currentShadersTexture = new ArrayList<>(SHADERS_TEXTURE.size());
        currentShadersTexture.addAll(SHADERS_TEXTURE.keySet());
        final List<String> currentShadersNoTexture = new ArrayList<>(SHADERS_NO_TEXTURE.size());
        currentShadersNoTexture.addAll(SHADERS_NO_TEXTURE.keySet());
        SHADERS_TEXTURE.forEach((name, shader) -> shader.close());
        SHADERS_TEXTURE.clear();
        SHADERS_NO_TEXTURE.forEach((name, shader) -> shader.close());
        SHADERS_NO_TEXTURE.clear();
        for (final String shader : currentShadersTexture) {
            createShader(shader, true);
        }
        for (final String shader : currentShadersNoTexture) {
            createShader(shader, false);
        }
    }

    public static RenderLayer getPositionColourTextureLayer(final Identifier texture, final boolean b) {
        if (b) {
            return POS_COLOUR_TEX_LAYER_TRANSPARENT.apply(texture);
        }
        return POS_COLOUR_TEX_LAYER.apply(texture);
    }
}
