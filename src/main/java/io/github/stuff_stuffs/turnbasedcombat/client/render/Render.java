package io.github.stuff_stuffs.turnbasedcombat.client.render;

import io.github.stuff_stuffs.turnbasedcombat.client.render.entity.NoopRenderer;
import io.github.stuff_stuffs.turnbasedcombat.client.render.entity.PlayerMarkerRenderer;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.EntityTypes;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

public final class Render {

    public static void init() {
        EntityRendererRegistry.INSTANCE.register(EntityTypes.BATTLE_CAMERA_ENTITY_TYPE, NoopRenderer::new);
        EntityRendererRegistry.INSTANCE.register(EntityTypes.PLAYER_MARKER_ENTITY_TYPE, PlayerMarkerRenderer::new);
    }

    private Render() {
    }
}
