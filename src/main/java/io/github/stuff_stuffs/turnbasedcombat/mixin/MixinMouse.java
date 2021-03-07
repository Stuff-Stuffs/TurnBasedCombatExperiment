package io.github.stuff_stuffs.turnbasedcombat.mixin;

import io.github.stuff_stuffs.turnbasedcombat.common.entity.AbstractBattleCameraEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Mouse.class)
public class MixinMouse {
    @Unique
    private boolean last = false;

    @Redirect(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
    private void redirect(final ClientPlayerEntity entity, final double cursorDeltaX, final double cursorDeltaY) {
        final Entity cameraEntity = MinecraftClient.getInstance().cameraEntity;
        boolean prev = last;
        if (cameraEntity instanceof AbstractBattleCameraEntity) {
            last = true;
            cameraEntity.changeLookDirection(cursorDeltaX, cursorDeltaY);
        } else {
            last = false;
            entity.changeLookDirection(cursorDeltaX, cursorDeltaY);
        }
        if(last != prev) {
            if(last) {
                entity.input = new Input();
            } else {
                entity.input = new KeyboardInput(MinecraftClient.getInstance().options);
            }
        }
    }
}
