package io.github.stuff_stuffs.tbcexcore.mixin.impl;

import com.google.common.collect.Iterables;
import io.github.stuff_stuffs.tbcexcore.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.Team;
import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.common.entity.BattleEntity;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleAwareEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity implements BattleEntity, BattleAwareEntity {
    @Shadow
    @Final
    private PlayerInventory inventory;

    @Shadow
    public abstract ItemStack getEquippedStack(EquipmentSlot slot);

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
        return Iterables.concat(inventory.armor, inventory.main, inventory.offHand);
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
    public BattleParticipantBounds getBounds() {
        return BattleParticipantBounds.builder().add(TurnBasedCombatExperiment.createId("body"), new Box(-0.5, 0, -0.5, 0.5, 1.5, 0.5)).add(TurnBasedCombatExperiment.createId("head"), new Box(-0.25, 1.5, -0.25, 0.25, 2, 0.25)).build();
    }

    @Override
    public @Nullable BattleHandle tbcex_getCurrentBattle() {
        return currentBattle;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void tbcex_setCurrentBattle(@Nullable final BattleHandle handle) {
        if (currentBattle != null && handle != null && !currentBattle.equals(handle)) {
            TurnBasedCombatExperiment.LOGGER.error("Set current battle to {}, while battle {} was active", handle, currentBattle);
        }
        currentBattle = handle;
        if ((Object) this instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.changeGameMode(GameMode.SPECTATOR);
        }
    }

    @Override
    public @Nullable ItemStack tbcex_getEquipped(final BattleEquipmentSlot slot) {
        if (slot == BattleEquipmentSlot.HEAD_SLOT) {
            return getEquippedStack(EquipmentSlot.HEAD);
        } else if (slot == BattleEquipmentSlot.CHEST_SLOT) {
            return getEquippedStack(EquipmentSlot.CHEST);
        } else if (slot == BattleEquipmentSlot.LEGS_SLOT) {
            return getEquippedStack(EquipmentSlot.LEGS);
        } else if (slot == BattleEquipmentSlot.FEET_SLOT) {
            return getEquippedStack(EquipmentSlot.FEET);
        }
        return null;
    }
}
