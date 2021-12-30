package io.github.stuff_stuffs.tbcexgui.mixin;

import io.github.stuff_stuffs.tbcexgui.client.screen.RawCharTypeScreen;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MixinKeyboard {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "onChar", at = @At("HEAD"), cancellable = true)
    private void inject(final long window, final int codePoint, final int modifiers, final CallbackInfo ci) {
        if (window == client.getWindow().getHandle()) {
            final Element element = client.currentScreen;
            if (element instanceof RawCharTypeScreen screen && client.getOverlay() == null) {
                screen.onCharTyped(codePoint, modifiers);
                ci.cancel();
            }
        }
    }
}
