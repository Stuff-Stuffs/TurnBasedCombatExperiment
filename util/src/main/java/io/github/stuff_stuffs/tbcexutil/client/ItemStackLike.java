package io.github.stuff_stuffs.tbcexutil.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public interface ItemStackLike {
    void render(MatrixStack matrices, double mouseX, double mouseY, float delta, VertexConsumerProvider vertexConsumers);
}
