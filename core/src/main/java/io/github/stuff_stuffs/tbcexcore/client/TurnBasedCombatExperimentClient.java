package io.github.stuff_stuffs.tbcexcore.client;

import io.github.stuff_stuffs.tbcexcore.client.network.ClientNetwork;
import io.github.stuff_stuffs.tbcexcore.client.render.BoxInfo;
import io.github.stuff_stuffs.tbcexcore.client.render.Render;
import io.github.stuff_stuffs.tbcexcore.client.render.battle.BattleRendererRegistry;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.common.battle.world.BattleBounds;
import io.github.stuff_stuffs.tbcexcore.mixin.api.ClientBattleWorldSupplier;
import io.github.stuff_stuffs.tbcexutil.client.DebugRenderers;
import it.unimi.dsi.fastutil.HashCommon;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TurnBasedCombatExperimentClient implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("TBCExClient");
    private static final List<BoxInfo> BOX_INFOS = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        Render.init();
        ClientNetwork.init();

        ClientTickEvents.START_WORLD_TICK.register(world -> ((ClientBattleWorldSupplier) world).tbcex_getBattleWorld().tick());
        DebugRenderers.register("battle_bounds", context -> {
            final VertexConsumerProvider consumers = context.consumers();
            final MatrixStack matrices = context.matrixStack();
            final ClientWorld world = context.world();
            final Frustum frustum = context.frustum();
            final Vec3d pos = MinecraftClient.getInstance().getCameraEntity().getCameraPosVec(context.tickDelta());
            final Random random = new Random(0);
            for (final Battle battle : ((ClientBattleWorldSupplier) world).tbcex_getBattleWorld()) {
                final BattleBounds bounds = battle.getState().getBounds();
                final Box box = bounds.getBox();
                if (frustum.isVisible(box)) {
                    matrices.push();
                    matrices.translate(-pos.x, -pos.y, -pos.z);
                    random.setSeed(HashCommon.murmurHash3(HashCommon.murmurHash3((long) battle.getHandle().id())));
                    WorldRenderer.drawBox(matrices, consumers.getBuffer(RenderLayer.LINES), box, random.nextFloat(), random.nextFloat(), random.nextFloat(), 1);
                    matrices.pop();
                }
            }
        }, DebugRenderers.Stage.POST_ENTITY);
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            VertexConsumerProvider vertexConsumers = context.consumers();
            MatrixStack matrices = context.matrixStack();
            Camera camera = context.camera();
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.LINES);
            double x = camera.getPos().x;
            double y = camera.getPos().y;
            double z = camera.getPos().z;
            for (BoxInfo info : BOX_INFOS) {
                WorldRenderer.drawBox(matrices, vertexConsumer, info.x0-x, info.y0-y, info.z0-z, info.x1-x, info.y1-y, info.z1-z, (float)info.r, (float)info.g, (float)info.b, (float)info.a);
            }
            BOX_INFOS.clear();
        });
    }

    public static void addBoxInfo(BoxInfo boxInfo) {
        BOX_INFOS.add(boxInfo);
    }
}
