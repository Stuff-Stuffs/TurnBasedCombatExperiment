package io.github.stuff_stuffs.tbcextest.client.render.entity;

import io.github.stuff_stuffs.tbcexanimation.client.model.Model;
import io.github.stuff_stuffs.tbcextest.common.entity.TestEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class TestEntityRenderer extends EntityRenderer<TestEntity> {
    public TestEntityRenderer(final EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(final TestEntity entity, final float yaw, final float tickDelta, final MatrixStack matrices, final VertexConsumerProvider vertexConsumers, final int light) {
        final Model model = entity.getModel();
        final int ticks = (int) entity.world.getTime();
        model.render(matrices, vertexConsumers, ticks, tickDelta);
    }

    @Override
    public Identifier getTexture(final TestEntity entity) {
        return null;
    }
}