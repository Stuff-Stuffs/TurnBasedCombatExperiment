package io.github.stuff_stuffs.tbcexanimation.client.model;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface Skeleton {
    Skeleton copy(boolean copyState);

    boolean containsBone(String name);

    Set<String> getBones();

    @Nullable ModelBoneInstance getBone(String name);

    void tick(int ticks, double partialTick);

    void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int ticks, double partialTick);
}
