package io.github.stuff_stuffs.turnbasedcombat.common;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleActions;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.turn.TurnChooserTypeRegistry;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.EntityTypes;
import io.github.stuff_stuffs.turnbasedcombat.common.network.Network;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class TurnBasedCombatExperiment implements ModInitializer {
    public static final String MOD_ID = "turn_based_combat";

    public static Identifier createId(final String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        EntityTypes.init();
        TurnChooserTypeRegistry.init();
        Network.init();
        BattleActions.init();
    }
}
