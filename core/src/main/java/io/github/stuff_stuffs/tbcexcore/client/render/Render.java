package io.github.stuff_stuffs.tbcexcore.client.render;

import io.github.stuff_stuffs.tbcexcore.client.render.debug.DebugRenderers;
import io.github.stuff_stuffs.tbcexcore.client.render.entity.NoopRenderer;
import io.github.stuff_stuffs.tbcexcore.common.entity.EntityTypes;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

public final class Render {

    private Render() {
    }

    public static void init() {
        DebugRenderers.init();
        EntityRendererRegistry.INSTANCE.register(EntityTypes.TEST_ENTITY_TYPE, NoopRenderer::new);
    }
}
