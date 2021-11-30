package io.github.stuff_stuffs.tbcexcharacter.client;

import io.github.stuff_stuffs.tbcexcharacter.client.screen.PlayerInfoScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class TBCExCharacterClient implements ClientModInitializer {
    public static final KeyBinding CHARACTER_SCREEN = KeyBindingHelper.registerKeyBinding(new KeyBinding("todo", GLFW.GLFW_KEY_B, "tbcex"));

    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            while (CHARACTER_SCREEN.wasPressed()) {
                MinecraftClient.getInstance().setScreen(new PlayerInfoScreen());
            }
        });
    }
}
