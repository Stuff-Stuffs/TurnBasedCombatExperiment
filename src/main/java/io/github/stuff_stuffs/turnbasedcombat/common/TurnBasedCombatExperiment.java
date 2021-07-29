package io.github.stuff_stuffs.turnbasedcombat.common;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleActionRegistry;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.damage.BattleDamageType;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.stats.BattleParticipantStat;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.EntityTypes;
import io.github.stuff_stuffs.turnbasedcombat.common.network.Network;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class TurnBasedCombatExperiment implements ModInitializer {
    public static final String MOD_ID = "turn_based_combat";

    public static Identifier createId(final String path) {
        return new Identifier(MOD_ID, path);
    }

    public static int getMaxTurnTime() {
        return 5;
    }

    public static int getMaxTurnCount() {
        return 100;
    }

    @Override
    public void onInitialize() {
        EntityTypes.init();
        Network.init();
        BattleDamageType.init();
        BattleActionRegistry.init();
        BattleParticipantStat.init();
    }
}
