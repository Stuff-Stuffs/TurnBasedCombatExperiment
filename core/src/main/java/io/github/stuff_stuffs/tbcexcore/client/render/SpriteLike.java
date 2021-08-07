package io.github.stuff_stuffs.tbcexcore.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public interface SpriteLike {
    Supplier<SpriteLike> MISSING = () -> of(MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(new Identifier("nope", "nope")));

    static SpriteLike of(final Sprite sprite) {
        return new SpriteLike() {
            @Override
            public void bind(final int slot) {
                RenderSystem.setShaderTexture(slot, sprite.getAtlas().getId());
            }

            @Override
            public float getMinU() {
                return sprite.getMinU();
            }

            @Override
            public float getMaxU() {
                return sprite.getMaxU();
            }

            @Override
            public float getMinV() {
                return sprite.getMinV();
            }

            @Override
            public float getMaxV() {
                return sprite.getMaxV();
            }
        };
    }

    void bind(int slot);

    float getMinU();

    float getMaxU();

    float getMinV();

    float getMaxV();
}
