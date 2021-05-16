package io.github.stuff_stuffs.turnbasedcombat.client.render.debug;

import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.turnbasedcombat.client.screen.battle.MoveBattleScreen;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleBounds;
import io.github.stuff_stuffs.turnbasedcombat.common.component.Components;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.api.AbstractBattleImpl;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.component.ClientBattleWorldComponent;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;

import java.util.Collection;
import java.util.Map;

public final class DebugRenderers {
    private static final Map<String, Pair<DebugRender, Stage>> DEBUG_RENDERS = new Object2ReferenceOpenHashMap<>();
    private static final Object2BooleanMap<String> TOGGLES = new Object2BooleanOpenHashMap<>();

    public static void init() {
        for (final Map.Entry<String, Pair<DebugRender, Stage>> entry : DEBUG_RENDERS.entrySet()) {
            final String name = entry.getKey();
            final DebugRender debugRender = entry.getValue().getFirst();
            switch (entry.getValue().getSecond()) {
                case POST_ENTITY:
                    WorldRenderEvents.AFTER_ENTITIES.register(context -> {
                        if (TOGGLES.getBoolean(name)) {
                            debugRender.render(context);
                        }
                    });
                    break;
                case POST_TRANSLUCENT:
                    WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
                        if (TOGGLES.getBoolean(name)) {
                            debugRender.render(context);
                        }
                    });
                    break;
                case PRE_DEBUG:
                    WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> {
                        if (TOGGLES.getBoolean(name)) {
                            debugRender.render(context);
                        }
                    });
                    break;
            }
        }
    }

    public static void register(final String name, final DebugRender debugRender, final Stage stage) {
        if (DEBUG_RENDERS.put(name, new Pair<>(debugRender, stage)) != null) {
            throw new RuntimeException("Duplicate named debug renderers");
        }
        TOGGLES.put(name, false);
    }

    private DebugRenderers() {
    }

    public static boolean contains(final String s) {
        return TOGGLES.containsKey(s);
    }

    public static void set(final String renderer, final boolean on) {
        TOGGLES.put(renderer, on);
    }

    public static Collection<String> getKeys() {
        return TOGGLES.keySet();
    }


    public enum Stage {
        POST_ENTITY,
        POST_TRANSLUCENT,
        PRE_DEBUG
    }

    static {
        register("battle_bounding_boxes", context -> {
            context.matrixStack().push();
            context.matrixStack().translate(-context.camera().getPos().x, -context.camera().getPos().y, -context.camera().getPos().z);
            final ClientBattleWorldComponent battleWorld = (ClientBattleWorldComponent) Components.BATTLE_WORLD_COMPONENT_KEY.get(context.world());
            battleWorld.forEach(battle -> {
                final BattleBounds bounds = battle.map(AbstractBattleImpl::getBounds, ClientBattleWorldComponent.PendingBattle::getBounds);
                final BlockPos min = bounds.getMin();
                final BlockPos max = bounds.getMax();
                final float r = battle.map(ba -> 1f, ba -> 0f);
                final float g = 0;
                final float b = battle.map(ba -> 0f, ba -> 1f);
                WorldRenderer.drawBox(context.matrixStack(), context.consumers().getBuffer(RenderLayer.LINES), min.getX(), min.getY(), min.getZ(), max.getX() + 1, max.getY() + 1, max.getZ() + 1, r, g, b, 1, r, g, b);
            });
            context.matrixStack().pop();
        }, Stage.POST_ENTITY);
        final Identifier id = new Identifier("textures/block/white_wool.png");
        register("move_spaces", context -> {
            final float tickDelta = context.tickDelta();
            final MatrixStack matrices = context.matrixStack();
            matrices.push();
            matrices.translate(-context.camera().getPos().x, -context.camera().getPos().y, -context.camera().getPos().z);
            final VertexConsumer consumers = context.consumers().getBuffer(RenderLayer.getEntityTranslucent(id));
            double time = MinecraftClient.getInstance().world.getTime() + tickDelta;
            double transparency = Math.abs((time%80)-40)/40;
            for (final BlockPos position : MoveBattleScreen.POSITIONS) {
                matrices.push();
                matrices.translate(position.getX(), position.getY(), position.getZ());
                final Matrix4f model = matrices.peek().getModel();
                consumers.vertex(model, 0, 0.001f, 0).color(0, 0, 128, 64 + (int)(128 * transparency)).texture(0, 0).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.pack(15, 15)).normal(0, 1, 0).next();
                consumers.vertex(model, 0, 0.001f, 1).color(0, 0, 128, 64 + (int)(128 * transparency)).texture(0, 1).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.pack(15, 15)).normal(0, 1, 0).next();
                consumers.vertex(model, 1, 0.001f, 1).color(0, 0, 128, 64 + (int)(128 * transparency)).texture(1, 1).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.pack(15, 15)).normal(0, 1, 0).next();
                consumers.vertex(model, 1, 0.001f, 0).color(0, 0, 128, 64 + (int)(128 * transparency)).texture(1, 0).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.pack(15, 15)).normal(0, 1, 0).next();
                matrices.pop();
            }
            matrices.pop();
        }, Stage.POST_ENTITY);
    }
}
