package io.github.stuff_stuffs.tbcexcore.mixin.impl;

import io.github.stuff_stuffs.tbcexcore.client.gui.BattleInventoryScreen;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleAwareEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {
    @Shadow public abstract void setScreen(@Nullable Screen screen);

    @Redirect(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
    private void redirect(final MinecraftClient client, final Screen screen) {
        if (screen instanceof InventoryScreen) {
            PlayerEntity player = client.player;
            final BattleHandle battleHandle = ((BattleAwareEntity) player).tbcex_getCurrentBattle();
            if (battleHandle != null) {
                setScreen(new BattleInventoryScreen(new BattleParticipantHandle(battleHandle, player.getUuid()), player.world));
                return;
            }
        }
        setScreen(screen);
    }
}
