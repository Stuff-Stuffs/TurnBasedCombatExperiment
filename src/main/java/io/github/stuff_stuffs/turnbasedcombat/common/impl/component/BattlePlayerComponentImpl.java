package io.github.stuff_stuffs.turnbasedcombat.common.impl.component;

import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.VanillaAbilities;
import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.turnbasedcombat.common.api.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.component.BattleEntityComponent;
import io.github.stuff_stuffs.turnbasedcombat.common.component.BattlePlayerComponent;
import io.github.stuff_stuffs.turnbasedcombat.common.component.BattleWorldComponent;
import io.github.stuff_stuffs.turnbasedcombat.common.component.Components;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Lazy;
import org.jetbrains.annotations.Nullable;

public class BattlePlayerComponentImpl implements BattlePlayerComponent {
    public static final AbilitySource ABILITY_SOURCE = Pal.getAbilitySource(TurnBasedCombatExperiment.createId("battle_camera"));
    private final PlayerEntity playerEntity;
    private final Lazy<BattleEntityComponent> delegate;

    public BattlePlayerComponentImpl(final PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
        delegate = new Lazy<>(() -> Components.BATTLE_ENTITY_COMPONENT_KEY.get(playerEntity));
    }

    @Override
    public boolean isInBattle() {
        return delegate.get().isInBattle();
    }

    @Override
    public @Nullable BattleHandle getBattleHandle() {
        return delegate.get().getBattleHandle();
    }

    public void setBattleHandle(final @Nullable BattleHandle battleHandle) {
        final BattleHandle handle = delegate.get().getBattleHandle();
        if (handle != null && !handle.equals(battleHandle)) {
            final Battle battle = BattleWorldComponent.getFromHandle(handle, playerEntity.world);
            if (battle != null && !battle.isActive()) {
                battle.remove((BattleEntity) playerEntity);
            }
        }
        ((BattleEntityComponentImpl) delegate.get()).setBattleHandle(battleHandle);
    }

    @Override
    public void readFromNbt(final CompoundTag tag) {
        delegate.get().readFromNbt(tag);
    }

    @Override
    public void writeToNbt(final CompoundTag tag) {
        delegate.get().writeToNbt(tag);
    }

    private boolean isPlayerInWorld() {
        return ((ServerWorld) playerEntity.world).getEntity(playerEntity.getUuid()) != null;
    }

    @Override
    public void tick() {
        if (!playerEntity.world.isClient()) {
            if (isPlayerInWorld() && isInBattle()) {
                Pal.grantAbility(playerEntity, VanillaAbilities.ALLOW_FLYING, ABILITY_SOURCE);
                Pal.grantAbility(playerEntity, VanillaAbilities.FLYING, ABILITY_SOURCE);
                Pal.grantAbility(playerEntity, VanillaAbilities.LIMIT_WORLD_MODIFICATIONS, ABILITY_SOURCE);
                Pal.grantAbility(playerEntity, VanillaAbilities.INVULNERABLE, ABILITY_SOURCE);
            } else {
                Pal.revokeAbility(playerEntity, VanillaAbilities.ALLOW_FLYING, ABILITY_SOURCE);
                Pal.revokeAbility(playerEntity, VanillaAbilities.FLYING, ABILITY_SOURCE);
                Pal.revokeAbility(playerEntity, VanillaAbilities.LIMIT_WORLD_MODIFICATIONS, ABILITY_SOURCE);
                Pal.revokeAbility(playerEntity, VanillaAbilities.INVULNERABLE, ABILITY_SOURCE);
            }
        }
    }
}
