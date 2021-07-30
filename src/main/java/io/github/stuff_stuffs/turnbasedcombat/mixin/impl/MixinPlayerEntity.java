package io.github.stuff_stuffs.turnbasedcombat.mixin.impl;

import com.google.common.collect.Iterables;
import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Team;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.BattleEntity;
import io.github.stuff_stuffs.turnbasedcombat.mixin.api.BattleAwarePlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity implements BattleEntity, BattleAwarePlayer {
    @Shadow
    @Final
    private PlayerInventory inventory;
    @Unique
    private static final Team TEAM = new Team("test_player");
    @Unique
    private BattleHandle currentBattle = null;

    @Override
    public Team getTeam() {
        return TEAM;
    }

    @Override
    public Iterable<ItemStack> tbcex_getInventory() {
        return Iterables.concat(inventory.armor, inventory.main, inventory.main);
    }

    @Override
    public double tbcex_getMaxHealth() {
        return 20;
    }

    @Override
    public double tbcex_getCurrentHealth() {
        return 20;
    }

    @Override
    public double tbcex_getStrength() {
        return 1;
    }

    @Override
    public double tbcex_getIntelligence() {
        return 1;
    }

    @Override
    public double tbcex_getVitality() {
        return 1;
    }

    @Override
    public double tbcex_getDexterity() {
        return 1;
    }

    @Override
    public @Nullable BattleHandle tbcex_getCurrentBattle() {
        return currentBattle;
    }

    @Override
    public void tbcex_setCurrentBattle(@Nullable final BattleHandle handle) {
        if (currentBattle != null && handle != null && !currentBattle.equals(handle)) {
            TurnBasedCombatExperiment.LOGGER.error("Set current battle to {}, while battle {} was active", handle, currentBattle);
        }
        currentBattle = handle;
    }
}
