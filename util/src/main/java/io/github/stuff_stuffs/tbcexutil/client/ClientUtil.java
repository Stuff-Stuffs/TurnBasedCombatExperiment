package io.github.stuff_stuffs.tbcexutil.client;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public final class ClientUtil implements ClientModInitializer {
    private ClientUtil() {
    }

    public static Vec3d getMouseVector() {
        final MinecraftClient client = MinecraftClient.getInstance();
        final double fov = Math.toRadians(client.options.fov);
        final Vec3f vec3f = new Vec3f((float) (client.getWindow().getFramebufferWidth() / 2d - client.mouse.getX()), (float) (client.getWindow().getFramebufferHeight() / 2d - client.mouse.getY()), (client.getWindow().getFramebufferHeight() / 2f) / ((float) Math.tan(fov / 2d)));
        final Quaternion rotation = client.gameRenderer.getCamera().getRotation();
        vec3f.rotate(rotation);
        vec3f.normalize();
        return new Vec3d(vec3f);
    }

    public static int tweakComponent(final int colour, final int componentIndex, final double factor) {
        assert 0 <= colour && componentIndex < 4;
        final int shift = componentIndex * 8;
        final int mask = 0xFF << shift;
        final int component = (colour & mask) >>> shift;
        final int tweaked = Math.max(Math.min((int) Math.round(component * factor), 255), 0);
        final int notComponents = colour & ~mask;
        final int shiftTweaked = tweaked << shift;
        return notComponents | shiftTweaked;
    }

    @Override
    public void onInitializeClient() {
        DebugRenderers.init();
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("debugRenderer").then(ClientCommandManager.argument("renderer", DebugRendererArgument.debugRendererArgument()).then(ClientCommandManager.argument("on", BoolArgumentType.bool()).executes(context -> {
            final String renderer = context.getArgument("renderer", String.class);
            final boolean on = BoolArgumentType.getBool(context, "on");
            DebugRenderers.set(renderer, on);
            return 0;
        }))));
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
