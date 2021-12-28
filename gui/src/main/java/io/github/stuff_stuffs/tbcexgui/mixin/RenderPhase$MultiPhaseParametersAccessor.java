package io.github.stuff_stuffs.tbcexgui.mixin;

import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.render.RenderLayer$MultiPhaseParameters")
public interface RenderPhase$MultiPhaseParametersAccessor {
    @Accessor(value = "texture")
    RenderPhase.TextureBase getTexture();
}
