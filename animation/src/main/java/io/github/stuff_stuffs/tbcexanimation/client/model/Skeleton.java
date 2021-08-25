package io.github.stuff_stuffs.tbcexanimation.client.model;

import io.github.stuff_stuffs.tbcexanimation.client.animation.Animation;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface Skeleton {
    @Nullable Animation getCurrentAnimation();

    void setAnimation(Animation animation, boolean stopCurrent);

    Skeleton copy(boolean copyState);

    boolean containsBone(String name);

    Set<String> getBones();

    @Nullable ModelBoneInstance getBone(String name);

    void tick(int ticks, double partialTick);

    void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int ticks, double partialTick, World world, Vec3d pos);
}
