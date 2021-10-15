package io.github.stuff_stuffs.tbcexcore.client.render.debug;

import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.world.BattleBounds;
import io.github.stuff_stuffs.tbcexcore.mixin.api.ClientBattleWorldSupplier;
import io.github.stuff_stuffs.tbcexutil.client.DebugRenderer;
import it.unimi.dsi.fastutil.HashCommon;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class BattleBoundsDebugRenderer implements DebugRenderer {
    public static final BattleBoundsDebugRenderer INSTANCE = new BattleBoundsDebugRenderer();

    private BattleBoundsDebugRenderer() {
    }

    @Override
    public void render(final WorldRenderContext context) {
        final VertexConsumerProvider consumers = context.consumers();
        final MatrixStack matrices = context.matrixStack();
        final ClientWorld world = context.world();
        final Vec3d pos = MinecraftClient.getInstance().getCameraEntity().getCameraPosVec(context.tickDelta());
        final Random random = new Random(0);
        for (final Battle battle : ((ClientBattleWorldSupplier) world).tbcex_getBattleWorld()) {
            final BattleBounds bounds = battle.getState().getBounds();
            final Box box = bounds.getBox();
            matrices.push();
            matrices.translate(-pos.x, -pos.y, -pos.z);
            random.setSeed(HashCommon.murmurHash3(HashCommon.murmurHash3((long) battle.getHandle().id())));
            WorldRenderer.drawBox(matrices, consumers.getBuffer(RenderLayer.LINES), box, random.nextFloat(), random.nextFloat(), random.nextFloat(), 1);
            matrices.pop();
        }
    }
}
