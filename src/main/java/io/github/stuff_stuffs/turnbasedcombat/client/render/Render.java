package io.github.stuff_stuffs.turnbasedcombat.client.render;

import io.github.stuff_stuffs.turnbasedcombat.client.render.debug.DebugRenderers;
import io.github.stuff_stuffs.turnbasedcombat.client.render.entity.NoopRenderer;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.EntityTypes;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

public final class Render {

    public static void init() {
        DebugRenderers.init();
        EntityRendererRegistry.INSTANCE.register(EntityTypes.TEST_ENTITY_TYPE, NoopRenderer::new);
    }

    private Render() {
    }
}
