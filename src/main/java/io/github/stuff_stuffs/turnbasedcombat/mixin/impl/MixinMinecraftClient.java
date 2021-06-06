package io.github.stuff_stuffs.turnbasedcombat.mixin.impl;

import io.github.stuff_stuffs.turnbasedcombat.client.battle.data.ClientBattleWorld;
import io.github.stuff_stuffs.turnbasedcombat.client.screen.BattleScreen;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.BattleEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {
    @Shadow
    @Nullable
    public ClientWorld world;

    @Shadow
    public abstract void openScreen(@Nullable Screen screen);

    @Shadow @Nullable public ClientPlayerEntity player;

    @Inject(method = "openScreen", at = @At("HEAD"), cancellable = true)
    private void inject(final Screen screen, final CallbackInfo ci) {
        if (screen instanceof InventoryScreen) {
            final Battle battle = ClientBattleWorld.get(world).getBattle((BattleEntity) this.player);
            if (battle != null) {
                openScreen(new BattleScreen());
                ci.cancel();
            }
        }
    }
}
