package io.github.stuff_stuffs.tbcexgui.mixin;

import net.minecraft.client.render.RenderPhase;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(RenderPhase.Texture.class)
public interface RenderPhase$TextureAccessor {
    @Accessor(value = "id")
    Optional<Identifier> getId();
}
