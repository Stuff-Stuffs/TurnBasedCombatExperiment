package io.github.stuff_stuffs.tbcexcore.mixin.impl;

import io.github.stuff_stuffs.tbcexcore.client.gui.BattleHud;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleAwareEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {
    @Unique
    private BattleHud hud;

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void renderBattleHud(final MatrixStack matrices, final float tickDelta, final CallbackInfo ci) {
        setupHud();
    }

    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void cancelHotbarRendering(final float tickDelta, final MatrixStack matrices, final CallbackInfo ci) {
        if (hud != null) {
            ci.cancel();
        }
    }

    @Inject(method = "renderStatusBars", at = @At("HEAD"), cancellable = true)
    private void cancelStatusBarRendering(final MatrixStack matrices, final CallbackInfo ci) {
        if (hud != null) {
            ci.cancel();
        }
    }

    @Inject(method = "renderMountHealth", at = @At("HEAD"), cancellable = true)
    private void cancelMountHealthBarRendering(final MatrixStack matrices, final CallbackInfo ci) {
        if (hud != null) {
            ci.cancel();
        }
    }

    @Inject(method = "renderMountJumpBar", at = @At("HEAD"), cancellable = true)
    private void cancelMountJumpBarRendering(final MatrixStack matrices, final int x, final CallbackInfo ci) {
        if (hud != null) {
            ci.cancel();
        }
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void cancelExperienceBarRendering(final MatrixStack matrices, final int x, final CallbackInfo ci) {
        if (hud != null) {
            ci.cancel();
        }
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    private void cancelStatusEffectOverlayRendering(final MatrixStack matrices, final CallbackInfo ci) {
        if (hud != null) {
            ci.cancel();
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/SpectatorHud;render(Lnet/minecraft/client/util/math/MatrixStack;F)V"))
    private void redirectSpectatorRendering(final SpectatorHud spectatorHud, final MatrixStack matrices, final float tickDelta) {
        if (hud != null) {
            hud.render(matrices, tickDelta);
        } else {
            spectatorHud.render(matrices);
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/SpectatorHud;render(Lnet/minecraft/client/util/math/MatrixStack;)V"))
    private void redirectSpectatorRendering(final SpectatorHud spectatorHud, final MatrixStack matrices) {
        if (hud == null) {
            spectatorHud.render(matrices);
        }
    }

    private void setupHud() {
        final BattleHandle handle = ((BattleAwareEntity) client.player).tbcex_getCurrentBattle();
        if (handle != null) {
            if (hud == null || !hud.matches(handle)) {
                hud = new BattleHud(handle, client.player);
            }
        } else {
            hud = null;
        }
    }
}
