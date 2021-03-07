package io.github.stuff_stuffs.turnbasedcombat.mixin;

import io.github.stuff_stuffs.turnbasedcombat.common.entity.AbstractBattleCameraEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.SetCameraEntityS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity {
    @Shadow private Entity cameraEntity;

    @Shadow public ServerPlayNetworkHandler networkHandler;

    @Inject(method = "setCameraEntity", at = @At("HEAD"), cancellable = true)
    private void inject(Entity entity, CallbackInfo ci) {
        if(entity instanceof AbstractBattleCameraEntity) {
            if(entity!=this.cameraEntity) {
                this.cameraEntity = entity;
                this.networkHandler.sendPacket(new SetCameraEntityS2CPacket(this.cameraEntity));
            }
            ci.cancel();
        }
    }
}
