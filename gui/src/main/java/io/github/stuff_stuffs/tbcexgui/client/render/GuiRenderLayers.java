package io.github.stuff_stuffs.tbcexgui.client.render;

import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class GuiRenderLayers extends RenderPhase {
    private static final Map<String, net.minecraft.client.render.Shader> SHADERS_TEXTURE = new Object2ReferenceOpenHashMap<>();
    private static final Map<String, net.minecraft.client.render.Shader> SHADERS_NO_TEXTURE = new Object2ReferenceOpenHashMap<>();
    private static final Map<String, RenderPhase.Shader> SHADERS_TEXTURE_PHASE = new Object2ReferenceOpenHashMap<>();
    private static final Map<String, RenderPhase.Shader> SHADERS_NO_TEXTURE_PHASE = new Object2ReferenceOpenHashMap<>();
    public static final RenderPhase.DepthTest NO_DEPTH_TEST = RenderPhase.ALWAYS_DEPTH_TEST;
    public static final RenderPhase.DepthTest DEPTH_TEST = RenderPhase.LEQUAL_DEPTH_TEST;
    public static final RenderPhase.Texture BLOCK_ATLAS_TEXTURE = RenderPhase.BLOCK_ATLAS_TEXTURE;
    public static final RenderPhase.Target MAIN_TARGET = RenderPhase.MAIN_TARGET;
    public static final RenderPhase.Target TRANSLUCENT_TARGET = RenderPhase.TRANSLUCENT_TARGET;
    public static final RenderPhase.Transparency NO_TRANSPARENCY = RenderPhase.NO_TRANSPARENCY;
    public static final RenderPhase.Transparency TRANSLUCENT_TRANSPARENCY = RenderPhase.TRANSLUCENT_TRANSPARENCY;
    public static final RenderPhase.WriteMaskState ALL_MASK = RenderPhase.ALL_MASK;
    public static final RenderPhase.WriteMaskState COLOR_MASK = RenderPhase.COLOR_MASK;
    private static ResourceManager RESOURCE_MANAGER;

    private GuiRenderLayers(String name, Runnable beginAction, Runnable endAction) {
        super(name, beginAction, endAction);
    }

    public static RenderPhase.Shader getShader(String shaderName, boolean texture) {
        if (texture) {
            return SHADERS_TEXTURE_PHASE.computeIfAbsent(shaderName, s -> createShaderPhase(s, true));
        } else {
            return SHADERS_NO_TEXTURE_PHASE.computeIfAbsent(shaderName, s -> createShaderPhase(s, false));
        }
    }

    private static RenderPhase.Shader createShaderPhase(String shaderName, boolean texture) {
        if (texture ? !SHADERS_TEXTURE.containsKey(shaderName) : !SHADERS_NO_TEXTURE.containsKey(shaderName)) {
            createShader(shaderName, texture);
        }
        return new Shader(texture ? () -> SHADERS_TEXTURE.get(shaderName) : () -> SHADERS_NO_TEXTURE.get(shaderName));
    }

    private static void createShader(String name, boolean texture) {
        if (SHADERS_TEXTURE.containsKey(name) || SHADERS_NO_TEXTURE.containsKey(name)) {
            throw new TBCExException("Tried to load shader in NO_TEXTURE and TEXTURE formats");
        }
        net.minecraft.client.render.Shader shader;
        final GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;
        if(gameRenderer.getShader(name)!=null) {
            shader = gameRenderer.getShader(name);
        } else {
            try {
                shader = new net.minecraft.client.render.Shader(RESOURCE_MANAGER, name, texture ? VertexFormats.POSITION_COLOR_TEXTURE : VertexFormats.POSITION_COLOR);
            } catch (IOException e) {
                throw new TBCExException("Error while loading shader", e);
            }
        }
        if (texture) {
            SHADERS_TEXTURE.put(name, shader);
        } else {
            SHADERS_NO_TEXTURE.put(name, shader);
        }
    }

    public static void setResourceManager(ResourceManager resourceManager) {
        RESOURCE_MANAGER = resourceManager;
        List<String> currentShadersTexture = new ArrayList<>(SHADERS_TEXTURE.size());
        currentShadersTexture.addAll(SHADERS_TEXTURE.keySet());
        List<String> currentShadersNoTexture = new ArrayList<>(SHADERS_NO_TEXTURE.size());
        currentShadersNoTexture.addAll(SHADERS_NO_TEXTURE.keySet());
        SHADERS_TEXTURE.forEach((name, shader) -> shader.close());
        SHADERS_TEXTURE.clear();
        SHADERS_NO_TEXTURE.forEach((name, shader) -> shader.close());
        SHADERS_NO_TEXTURE.clear();
        for (String shader : currentShadersTexture) {
            createShader(shader, true);
        }
        for (String shader : currentShadersNoTexture) {
            createShader(shader, false);
        }
    }
}
