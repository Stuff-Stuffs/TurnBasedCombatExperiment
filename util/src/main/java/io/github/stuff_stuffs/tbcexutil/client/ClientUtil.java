package io.github.stuff_stuffs.tbcexutil.client;

import com.google.common.base.Suppliers;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.stuff_stuffs.tbcexutil.mixin.AccessorGameRenderer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.command.CommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class ClientUtil implements ClientModInitializer {
    public static final Transformation TRANSFORM_ITEM_HEAD = makeTransform(0, 180, 0, 0, 13, 7, 1, 1, 1);
    public static final Transformation TRANSFORM_ITEM_GUI = Transformation.IDENTITY;
    public static final Transformation TRANSFORM_ITEM_GROUND = makeTransform(0, 0, 0, 0, 2, 0, 0.5f, 0.5f, 0.5f);
    public static final Transformation TRANSFORM_ITEM_FIXED = makeTransform(0, 180, 0, 0, 0, 0, 1f, 1f, 1f);
    public static final Transformation TRANSFORM_ITEM_3RD_PERSON_RIGHT = makeTransform(0, 0, 0, 0, 3f, 1, 0.55f, 0.55f, 0.55f);
    public static final Transformation TRANSFORM_ITEM_3RD_PERSON_LEFT = makeTransform(0, 0, 0, 0, 3f, 1, 0.55f, 0.55f, 0.55f);
    public static final Transformation TRANSFORM_ITEM_1ST_PERSON_RIGHT = makeTransform(0, -90, 25, 1.13f, 3.2f, 1.13f, 0.68f, 0.68f, 0.68f);
    public static final Transformation TRANSFORM_ITEM_1ST_PERSON_LEFT = makeTransform(0, -90, 25, 1.13f, 3.2f, 1.13f, 0.68f, 0.68f, 0.68f);

    //TODO fix this
    public static final ModelTransformation ITEM_TRANSFORMATION = new ModelTransformation(TRANSFORM_ITEM_3RD_PERSON_LEFT, TRANSFORM_ITEM_3RD_PERSON_RIGHT, TRANSFORM_ITEM_1ST_PERSON_LEFT, TRANSFORM_ITEM_1ST_PERSON_RIGHT, TRANSFORM_ITEM_HEAD, TRANSFORM_ITEM_GUI, TRANSFORM_ITEM_GROUND, TRANSFORM_ITEM_FIXED);

    public static final Supplier<Mesh> LAZY_EMPTY_MESH = Suppliers.memoize(() -> RendererAccess.INSTANCE.getRenderer().meshBuilder().build());

    public ClientUtil() {
    }

    public static Vec3d getMouseVector() {
        final MinecraftClient client = MinecraftClient.getInstance();
        final double fov = Math.toRadians(((AccessorGameRenderer) client.gameRenderer).callGetFov(client.gameRenderer.getCamera(), 0.5f, true));
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

    private static Transformation makeTransform(final float rotationX, final float rotationY, final float rotationZ, final float translationX, final float translationY, final float translationZ, final float scaleX, final float scaleY, final float scaleZ) {
        final Vec3f translation = new Vec3f(translationX, translationY, translationZ);
        translation.scale(0.0625f);
        translation.clamp(-5.0F, 5.0F);
        return new Transformation(new Vec3f(rotationX, rotationY, rotationZ), translation, new Vec3f(scaleX, scaleY, scaleZ));
    }

    public static Mesh transform(final Mesh input, final RenderContext.QuadTransform transform) {
        final MeshBuilder builder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
        final QuadEmitter emitter = builder.getEmitter();
        input.forEach(quadView -> {
            quadView.copyTo(emitter);
            if (transform.transform(emitter)) {
                emitter.emit();
            }
        });
        return builder.build();
    }

    public static Mesh merge(final Mesh... inputs) {
        final MeshBuilder builder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
        final QuadEmitter emitter = builder.getEmitter();
        for (final Mesh mesh : inputs) {
            mesh.forEach(quadView -> {
                quadView.copyTo(emitter);
                emitter.emit();
            });
        }
        return builder.build();
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
