package io.github.stuff_stuffs.tbcexcore.client;

import io.github.stuff_stuffs.tbcexcore.client.network.ClientNetwork;
import io.github.stuff_stuffs.tbcexcore.client.render.BoxInfo;
import io.github.stuff_stuffs.tbcexcore.client.render.Render;
import io.github.stuff_stuffs.tbcexcore.client.render.debug.BattleParticipantBoundsDebugRenderer;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.world.BattleBounds;
import io.github.stuff_stuffs.tbcexcore.mixin.api.ClientBattleWorldSupplier;
import io.github.stuff_stuffs.tbcexutil.client.DebugRenderers;
import it.unimi.dsi.fastutil.HashCommon;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class TurnBasedCombatExperimentClient implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("TBCExClient");
    //TODO https://github.com/FabricMC/fabric/issues/1772
    public static final KeyBinding ALT_MODE_KEYBINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding("tbcex.alt_mod_key", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_INSERT, "tbcex"));
    private static final List<BoxInfo> BOX_INFOS = new ArrayList<>();
    private static final List<Consumer<WorldRenderContext>> RENDER_PRIMITIVES = new ArrayList<>();

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
        DebugRenderers.register("battle_particpant_bounds", BattleParticipantBoundsDebugRenderer.INSTANCE, DebugRenderers.Stage.POST_ENTITY);
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            final VertexConsumerProvider vertexConsumers = context.consumers();
            final MatrixStack matrices = context.matrixStack();
            final Camera camera = context.camera();
            final VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.LINES);
            final double x = camera.getPos().x;
            final double y = camera.getPos().y;
            final double z = camera.getPos().z;
            matrices.push();
            matrices.translate(-x, -y, -z);
            for (final BoxInfo info : BOX_INFOS) {
                WorldRenderer.drawBox(matrices, vertexConsumer, info.x0, info.y0, info.z0, info.x1, info.y1, info.z1, (float) info.r, (float) info.g, (float) info.b, (float) info.a);
            }
            for (final Consumer<WorldRenderContext> renderPrimitive : RENDER_PRIMITIVES) {
                renderPrimitive.accept(context);
            }
            matrices.pop();
            BOX_INFOS.clear();
            RENDER_PRIMITIVES.clear();
        });
    }

    public static void addBoxInfo(final BoxInfo boxInfo) {
        BOX_INFOS.add(boxInfo);
    }

    public static void addRenderPrimitive(final Consumer<WorldRenderContext> primitive) {
        RENDER_PRIMITIVES.add(primitive);
    }
}
