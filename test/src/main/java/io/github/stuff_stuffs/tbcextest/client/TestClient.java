package io.github.stuff_stuffs.tbcextest.client;

import io.github.stuff_stuffs.tbcextest.client.render.entity.TestEntityRenderer;
import io.github.stuff_stuffs.tbcextest.client.render.model.ToolPartsTestItemModel;
import io.github.stuff_stuffs.tbcextest.common.entity.EntityTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.util.Identifier;

public class TestClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(EntityTypes.TEST_ENTITY_TYPE, TestEntityRenderer::new);
        final Identifier id = new Identifier("tbcextest", "item/parts");
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(resourceManager -> (resourceId, context) -> {
            if (resourceId.equals(id)) {
                return new ToolPartsTestItemModel();
            }
            return null;
        });
    }
}