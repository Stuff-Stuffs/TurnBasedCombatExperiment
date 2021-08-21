package io.github.stuff_stuffs.tbcexanimation.client;

import io.github.stuff_stuffs.tbcexutil.client.DebugRenderers;
import net.fabricmc.api.ClientModInitializer;

public class TBCExAnimationClient implements ClientModInitializer {
    public static final String BONE_DEBUG_RENDERER = "animation_bone";

    @Override
    public void onInitializeClient() {
        DebugRenderers.register(BONE_DEBUG_RENDERER, context -> {
        }, DebugRenderers.Stage.POST_ENTITY);
    }
}
