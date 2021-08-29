package io.github.stuff_stuffs.tbcexanimation.client.model.part;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface ModelPart {
    void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, Vec3d pos);

    void tick(double timeSinceLast);
}
