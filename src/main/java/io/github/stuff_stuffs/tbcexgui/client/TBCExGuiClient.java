package io.github.stuff_stuffs.tbcexgui.client;

import io.github.stuff_stuffs.tbcexgui.client.screen.TestScreen;
import io.github.stuff_stuffs.tbcexgui.client.util.NinePatch;
import io.github.stuff_stuffs.tbcexgui.client.widget.interaction.ButtonState;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;

@Environment(EnvType.CLIENT)
public class TBCExGuiClient implements ClientModInitializer {
    public static final KeyBinding TEST_SCREEN_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding("test.test.test", GLFW.GLFW_KEY_G, "misc"));

    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_WORLD_TICK.register(world -> {
            while (TEST_SCREEN_KEY.wasPressed()) {
                MinecraftClient.getInstance().openScreen(TestScreen.build());
            }
        });
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
            Identifier base = new Identifier("tbcexgui", "gui/panel");
            for (final NinePatch.Part part : NinePatch.Part.values()) {
                registry.register(part.append(base));
            }
            for (final ButtonState state : ButtonState.values()) {
                base = new Identifier("tbcexgui", "gui/button/" + state.name().toLowerCase(Locale.ROOT));
                for (final NinePatch.Part part : NinePatch.Part.values()) {
                    registry.register(part.append(base));
                }
            }
            base = new Identifier("tbcexgui", "gui/hotbar/single");
            for (NinePatch.Part part : NinePatch.Part.values()) {
                registry.register(part.append(base));
            }
            base = new Identifier("tbcexgui", "gui/hotbar/single/selected");
            for (NinePatch.Part part : NinePatch.Part.values()) {
                registry.register(part.append(base));
            }
        });
    }
}
