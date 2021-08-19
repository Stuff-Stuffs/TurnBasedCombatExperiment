package io.github.stuff_stuffs.tbcexanimation.client.model;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public interface ModelPart {
    void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers);
}
