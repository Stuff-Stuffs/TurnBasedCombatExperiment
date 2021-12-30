package io.github.stuff_stuffs.tbcexgui.client;

import io.github.stuff_stuffs.tbcexgui.client.render.GuiRenderLayers;
import io.github.stuff_stuffs.tbcexgui.client.render.NinePatch;
import io.github.stuff_stuffs.tbcexgui.client.render.TooltipRenderer;
import io.github.stuff_stuffs.tbcexgui.client.screen.TBCExScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TBCExGuiClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
            final Identifier base = new Identifier("tbcexgui", "gui/tooltip");
            for (final NinePatch.Part part : NinePatch.Part.values()) {
                registry.register(part.append(base));
            }
            registry.register(new Identifier("tbcexgui", "gui/transparent"));
        });
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenEvents.beforeRender(screen).register((s, matrices, mouseX, mouseY, tickDelta) -> TooltipRenderer.clear());
            if (screen instanceof TBCExScreen) {
                ScreenEvents.afterRender(screen).register((s, matrices, mouseX, mouseY, tickDelta) -> TooltipRenderer.renderAll());
            }
        });
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier("tbcexgui", "shader_listener");
            }

            @Override
            public void reload(final ResourceManager manager) {
                GuiRenderLayers.setResourceManager(manager);
            }
        });
    }
}
