package io.github.stuff_stuffs.turnbasedcombat.mixin;

import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.api.Team;
import io.github.stuff_stuffs.turnbasedcombat.common.component.BattlePlayerComponent;
import io.github.stuff_stuffs.turnbasedcombat.common.component.Components;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity implements BattleEntity {
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void damageHook(final DamageSource source, final float amount, final CallbackInfoReturnable<Boolean> cir) {
        final BattlePlayerComponent component = Components.BATTLE_PLAYER_COMPONENT_KEY.get(this);
        if (component.isInBattle()) {
            cir.setReturnValue(false);
        }
    }

    @Override
    public Team getTeam() {
        return Team.DEFAULT_PLAYER_TEAM;
    }
}
