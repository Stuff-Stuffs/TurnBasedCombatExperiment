package io.github.stuff_stuffs.tbcexgui.mixin;

import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(targets = "net.minecraft.client.render.RenderPhase$Texture")
public interface RenderPhase$TextureAccessor {
    @Accessor(value = "id")
    Optional<Identifier> getId();
}
