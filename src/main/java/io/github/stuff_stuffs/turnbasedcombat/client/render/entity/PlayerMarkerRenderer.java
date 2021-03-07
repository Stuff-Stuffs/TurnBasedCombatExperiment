package io.github.stuff_stuffs.turnbasedcombat.client.render.entity;

import io.github.stuff_stuffs.turnbasedcombat.common.entity.PlayerMarkerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;

public class PlayerMarkerRenderer extends EntityRenderer<PlayerMarkerEntity> {
    public PlayerMarkerRenderer(final EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(final PlayerMarkerEntity entity, final float yaw, final float tickDelta, final MatrixStack matrices, final VertexConsumerProvider vertexConsumers, final int light) {
        final ClientWorld world = (ClientWorld) entity.world;
        if (world != null) {
            final ClientPlayerEntity playerEntity = MinecraftClient.getInstance().player;
            if (playerEntity != null) {
                dispatcher.render(playerEntity, 0, 0, 0, playerEntity.yaw, tickDelta, matrices, vertexConsumers, light);
            }
        }
    }

    @Override
    public Identifier getTexture(final PlayerMarkerEntity entity) {
        return dispatcher.getRenderer(MinecraftClient.getInstance().player).getTexture(MinecraftClient.getInstance().player);
    }
}
