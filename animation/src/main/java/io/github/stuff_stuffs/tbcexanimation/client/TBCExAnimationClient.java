package io.github.stuff_stuffs.tbcexanimation.client;

import io.github.stuff_stuffs.tbcexanimation.client.resource.ModelManager;
import io.github.stuff_stuffs.tbcexanimation.client.resource.ModelPartScalerArgumentApplier;
import io.github.stuff_stuffs.tbcexanimation.client.resource.ModelPartTextureSwapperArgumentApplier;
import io.github.stuff_stuffs.tbcexutil.client.DebugRenderers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class TBCExAnimationClient implements ClientModInitializer {
    public static final String BONE_DEBUG_RENDERER = "animation_bone";
    public static final ModelManager MODEL_MANAGER = new ModelManager();

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(MODEL_MANAGER);
        DebugRenderers.register(BONE_DEBUG_RENDERER, context -> {
        }, DebugRenderers.Stage.POST_ENTITY);
        MODEL_MANAGER.addModifier("texture_swap", new ModelPartTextureSwapperArgumentApplier());
        MODEL_MANAGER.addModifier("scale", new ModelPartScalerArgumentApplier());
    }
}
