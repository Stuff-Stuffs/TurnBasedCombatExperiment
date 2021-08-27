package io.github.stuff_stuffs.tbcexgui.client;

import io.github.stuff_stuffs.tbcexgui.client.widget.interaction.ButtonState;
import io.github.stuff_stuffs.tbcexutil.client.NinePatch;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

import java.util.Locale;

@Environment(EnvType.CLIENT)
public class TBCExGuiClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
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
