package io.github.stuff_stuffs.turnbasedcombat.mixin;

import io.github.stuff_stuffs.turnbasedcombat.common.component.BattlePlayerComponent;
import io.github.stuff_stuffs.turnbasedcombat.common.component.Components;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "attackEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;attack(Lnet/minecraft/entity/Entity;)V"), cancellable = true)
    private void hook(final PlayerEntity player, final Entity target, final CallbackInfo ci) {
        final BattlePlayerComponent battlePlayer = Components.BATTLE_PLAYER_COMPONENT_KEY.get(player);
        if (battlePlayer.isInBattle()) {
            ci.cancel();
        }
    }

    @Inject(method = "interactEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;interact(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"), cancellable = true)
    private void hook(final PlayerEntity player, final Entity entity, final Hand hand, final CallbackInfoReturnable<ActionResult> cir) {
        final BattlePlayerComponent battlePlayer = Components.BATTLE_PLAYER_COMPONENT_KEY.get(player);
        if (battlePlayer.isInBattle()) {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

    @Inject(method = "interactBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/BlockHitResult;getBlockPos()Lnet/minecraft/util/math/BlockPos;"), cancellable = true)
    private void hook(final ClientPlayerEntity player, final ClientWorld world, final Hand hand, final BlockHitResult hitResult, final CallbackInfoReturnable<ActionResult> cir) {
        final BattlePlayerComponent battlePlayer = Components.BATTLE_PLAYER_COMPONENT_KEY.get(player);
        if (battlePlayer.isInBattle()) {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

    @Inject(method = "interactItem", at = @At(value = "HEAD"), cancellable = true)
    private void hook(final PlayerEntity player, final World world, final Hand hand, final CallbackInfoReturnable<ActionResult> cir) {
        final BattlePlayerComponent battlePlayer = Components.BATTLE_PLAYER_COMPONENT_KEY.get(player);
        if (battlePlayer.isInBattle()) {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

    @Inject(method = "interactEntityAtLocation", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;interactAt(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"), cancellable = true)
    private void hook(final PlayerEntity player, final Entity entity, final EntityHitResult hitResult, final Hand hand, final CallbackInfoReturnable<ActionResult> cir) {
        final BattlePlayerComponent battlePlayer = Components.BATTLE_PLAYER_COMPONENT_KEY.get(player);
        if (battlePlayer.isInBattle()) {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

    @Inject(method = "isFlyingLocked", at = @At("HEAD"), cancellable = true)
    private void hook(final CallbackInfoReturnable<Boolean> cir) {
        final BattlePlayerComponent battlePlayer = Components.BATTLE_PLAYER_COMPONENT_KEY.get(client.player);
        if (battlePlayer.isInBattle()) {
            cir.setReturnValue(true);
        }
    }
}
