package io.github.stuff_stuffs.tbcexgui.client.render;

import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class GuiRenderLayers extends RenderPhase {
    public static final RenderPhase.Cull NO_CULL = RenderPhase.DISABLE_CULLING;
    public static final RenderPhase.Lightmap ENABLE_LIGHTMAP = RenderPhase.ENABLE_LIGHTMAP;
    public static final RenderPhase.Lightmap DISABLE_LIGHTMAP = RenderPhase.DISABLE_LIGHTMAP;
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
    private static final Map<String, ShaderInfo> SHADERS = new Object2ReferenceOpenHashMap<>();
    private static final Map<String, RenderPhase.Shader> SHADERS_PHASE = new Object2ReferenceOpenHashMap<>();
    private static final Map<Identifier, RenderPhase.Texture> TEXTURE_MAP = new Object2ReferenceOpenHashMap<>();
    private static final Map<RenderLayer, BufferBuilder> BUFFERS = new Reference2ObjectOpenHashMap<>();
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

    public static RenderPhase.Shader getShader(final String shaderName, final VertexFormat format) {
        return SHADERS_PHASE.computeIfAbsent(shaderName, name -> createShaderPhase(name, format));
    }

    public static RenderPhase.Texture getTexture(final Identifier id) {
        return TEXTURE_MAP.computeIfAbsent(id, identifier -> new Texture(identifier, false, false));
    }

    private static RenderPhase.Shader createShaderPhase(final String shaderName, final VertexFormat format) {
        final ShaderInfo info = SHADERS.get(shaderName);
        if (info == null) {
            createShader(shaderName, format);
        } else if (!format.equals(info.format)) {
            throw new TBCExException("Differing formats requested on shader");
        }
        return new Shader(() -> SHADERS.get(shaderName).shader);
    }

    private static void createShader(final String name, final VertexFormat format) {
        if (SHADERS.containsKey(name)) {
            throw new TBCExException("Tried to load shader in differing vertex formats");
        }
        final net.minecraft.client.render.Shader shader;
        final GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;
        if (gameRenderer.getShader(name) != null) {
            shader = gameRenderer.getShader(name);
        } else {
            try {
                shader = new net.minecraft.client.render.Shader(RESOURCE_MANAGER, name, format);
            } catch (final IOException e) {
                throw new TBCExException("Error while loading shader", e);
            }
        }
        SHADERS.put(name, new ShaderInfo(shader, format));
    }

    public static void setResourceManager(final ResourceManager resourceManager) {
        RESOURCE_MANAGER = resourceManager;
        final List<Pair<String, VertexFormat>> currentShadersTexture = SHADERS.entrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getValue().format)).toList();
        SHADERS.forEach((name, shaderInfo) -> shaderInfo.shader.close());
        SHADERS.clear();
        for (final Pair<String, VertexFormat> shader : currentShadersTexture) {
            createShader(shader.getFirst(), shader.getSecond());
        }
    }

    private static final class ShaderInfo {
        private final net.minecraft.client.render.Shader shader;
        private final VertexFormat format;

        private ShaderInfo(final net.minecraft.client.render.Shader shader, final VertexFormat format) {
            this.shader = shader;
            this.format = format;
        }
    }
}
