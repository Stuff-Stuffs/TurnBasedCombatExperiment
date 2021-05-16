package io.github.stuff_stuffs.turnbasedcombat.mixin;

import io.github.stuff_stuffs.turnbasedcombat.client.screen.StackedBattleScreen;
import io.github.stuff_stuffs.turnbasedcombat.common.component.BattlePlayerComponent;
import io.github.stuff_stuffs.turnbasedcombat.common.component.Components;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Redirect(
            method = "handleInputEvents",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/MinecraftClient;openScreen(Lnet/minecraft/client/gui/screen/Screen;)V"
            ),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/tutorial/TutorialManager;onInventoryOpened()V"
                    ),
                    to = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/gui/screen/advancement/AdvancementsScreen;<init>(Lnet/minecraft/client/network/ClientAdvancementManager;)V"
                    )
            )
    )
    private void handleInventoryOpen(final MinecraftClient client, final Screen screen) {
        final BattlePlayerComponent battlePlayer = Components.BATTLE_PLAYER_COMPONENT_KEY.get(client.player);
        if (battlePlayer.isInBattle()) {
            client.openScreen(new StackedBattleScreen());
        } else {
            client.openScreen(screen);
        }
    }
}
