package io.github.stuff_stuffs.turnbasedcombat.common.api;

import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.PlayerAbility;
import io.github.ladysnake.pal.SimpleAbilityTracker;
import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.turnbasedcombat.common.network.sender.MovementAbility;
import net.minecraft.server.network.ServerPlayerEntity;

public final class TurnBasedCombatAbilities {
    public static final PlayerAbility DISABLED_MOVEMENT = Pal.registerAbility(TurnBasedCombatExperiment.createId("disabled_movement"), (playerAbility, playerEntity) -> new SimpleAbilityTracker(playerAbility, playerEntity) {
        @Override
        protected void sync() {
            if (((ServerPlayerEntity) playerEntity).networkHandler != null) {
                MovementAbility.send(playerEntity, isEnabled());
            }
        }
    });

    public static void init() {
    }

    private TurnBasedCombatAbilities() {
    }
}
