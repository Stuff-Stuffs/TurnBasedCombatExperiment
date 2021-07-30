package io.github.stuff_stuffs.turnbasedcombat.mixin.impl;

import io.github.stuff_stuffs.turnbasedcombat.mixin.api.BattleAwareEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerInteractionManager.class)
public class MixinServerPlayerInteractionManager {
    @Shadow
    @Final
    protected ServerPlayerEntity player;

    @Inject(method = "setGameMode", at = @At("HEAD"), cancellable = true)
    private void ignoreSetGameModeWhileInBattle(final GameMode gameMode, final GameMode previousGameMode, final CallbackInfo ci) {
        if (((BattleAwareEntity) player).tbcex_getCurrentBattle() != null && gameMode != GameMode.SPECTATOR) {
            ci.cancel();
        }
    }
}
