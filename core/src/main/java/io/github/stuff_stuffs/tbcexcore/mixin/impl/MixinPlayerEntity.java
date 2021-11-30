package io.github.stuff_stuffs.tbcexcore.mixin.impl;

import io.github.stuff_stuffs.tbcexcore.common.TBCExCore;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity implements BattleAwareEntity {
    @Unique
    private BattleHandle currentBattle = null;

    @Override
    public @Nullable BattleHandle tbcex_getCurrentBattle() {
        return currentBattle;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void tbcex_setCurrentBattle(@Nullable final BattleHandle handle) {
        if (currentBattle != null && handle != null && !currentBattle.equals(handle)) {
            TBCExCore.LOGGER.error("Set current battle to {}, while battle {} was active", handle, currentBattle);
        }
        currentBattle = handle;
        if (handle != null) {
            if ((Object) this instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.changeGameMode(GameMode.SPECTATOR);
            }
        } else {
            if ((Object) this instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.changeGameMode(GameMode.SURVIVAL);
            }
        }
    }
}
