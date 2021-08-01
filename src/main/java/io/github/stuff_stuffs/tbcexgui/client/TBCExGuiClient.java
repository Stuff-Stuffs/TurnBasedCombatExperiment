package io.github.stuff_stuffs.tbcexgui.client;

import io.github.stuff_stuffs.tbcexgui.client.screen.TestScreen;
import io.github.stuff_stuffs.tbcexgui.client.widget.interaction.ButtonPart;
import io.github.stuff_stuffs.tbcexgui.client.widget.interaction.ButtonState;
import io.github.stuff_stuffs.tbcexgui.client.widget.panel.PanelPart;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.texture.SpriteAtlasTexture;
import org.lwjgl.glfw.GLFW;

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
            for (final PanelPart part : PanelPart.values()) {
                registry.register(part.getIdentifier());
            }
            for (final ButtonPart part : ButtonPart.values()) {
                for (final ButtonState state : ButtonState.values()) {
                    registry.register(part.getIdentifier(state));
                }
            }
        });
    }
}
