package io.github.stuff_stuffs.tbcexcore.client;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.stuff_stuffs.tbcexcore.client.render.Render;
import io.github.stuff_stuffs.tbcexcore.client.render.debug.DebugRenderers;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.world.BattleBounds;
import io.github.stuff_stuffs.tbcexcore.mixin.api.ClientBattleWorldSupplier;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class TurnBasedCombatExperimentClient implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("TBCExClient");

    @Override
    public void onInitializeClient() {
        Render.init();
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("debugRenderer").then(ClientCommandManager.argument("renderer", DebugRendererArgument.debugRendererArgument()).then(ClientCommandManager.argument("on", BoolArgumentType.bool()).executes(context -> {
            final String renderer = context.getArgument("renderer", String.class);
            final boolean on = BoolArgumentType.getBool(context, "on");
            DebugRenderers.set(renderer, on);
            return 0;
        }))));
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
    }

    private static class DebugRendererArgument implements ArgumentType<String> {
        private static final SimpleCommandExceptionType EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("turn_base_combat.debugRenderer.invalid"));

        private DebugRendererArgument() {
        }

        public static DebugRendererArgument debugRendererArgument() {
            return new DebugRendererArgument();
        }

        @Override
        public String parse(final StringReader reader) throws CommandSyntaxException {
            final int i = reader.getCursor();
            final String s = reader.readStringUntil(' ');
            //cursed
            reader.setCursor(reader.getCursor() - 1);
            if (DebugRenderers.contains(s)) {
                return s;
            }
            reader.setCursor(i);
            throw EXCEPTION.createWithContext(reader);
        }

        @Override
        public Collection<String> getExamples() {
            return new ObjectArrayList<>(DebugRenderers.getKeys());
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
            return CommandSource.suggestMatching(DebugRenderers.getKeys(), builder);
        }
    }
}
