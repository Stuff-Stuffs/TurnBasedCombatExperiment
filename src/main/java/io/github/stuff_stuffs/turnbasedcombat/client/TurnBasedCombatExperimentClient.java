package io.github.stuff_stuffs.turnbasedcombat.client;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.stuff_stuffs.turnbasedcombat.client.render.Render;
import io.github.stuff_stuffs.turnbasedcombat.client.render.debug.DebugRenderers;
import io.github.stuff_stuffs.turnbasedcombat.mixin.api.ClientBattleWorldSupplier;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.command.CommandSource;
import net.minecraft.text.TranslatableText;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class TurnBasedCombatExperimentClient implements ClientModInitializer {
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
            reader.setCursor(reader.getCursor()-1);
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
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            return CommandSource.suggestMatching(DebugRenderers.getKeys(), builder);
        }
    }
}
