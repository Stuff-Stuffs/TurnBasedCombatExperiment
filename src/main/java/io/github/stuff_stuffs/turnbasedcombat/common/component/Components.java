package io.github.stuff_stuffs.turnbasedcombat.common.component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;

public final class Components {
    public static final ComponentKey<BattlePlayerComponent> BATTLE_PLAYER_COMPONENT_KEY = ComponentRegistryV3.INSTANCE.getOrCreate(TurnBasedCombatExperiment.createId("battle_player"), BattlePlayerComponent.class);
    public static final ComponentKey<BattleWorldComponent> BATTLE_WORLD_COMPONENT_KEY = ComponentRegistryV3.INSTANCE.getOrCreate(TurnBasedCombatExperiment.createId("battle_world"), BattleWorldComponent.class);
    public static final ComponentKey<BattleEntityComponent> BATTLE_ENTITY_COMPONENT_KEY = ComponentRegistryV3.INSTANCE.getOrCreate(TurnBasedCombatExperiment.createId("battle_entity"), BattleEntityComponent.class);

    private Components() {
    }
}
