package io.github.stuff_stuffs.tbcexcore.common;

import io.github.stuff_stuffs.tbcexcore.common.battle.action.BattleActionRegistry;
import io.github.stuff_stuffs.tbcexcore.common.battle.damage.BattleDamageType;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.ParticipantComponents;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;
import io.github.stuff_stuffs.tbcexcore.common.network.Network;
import io.github.stuff_stuffs.tbcexutil.common.LoggerUtil;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;

public class TurnBasedCombatExperiment implements ModInitializer {
    public static final String MOD_ID = "tbcexcore";
    public static final Logger LOGGER = LoggerUtil.LOGGER;

    public static Identifier createId(final String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        Network.init();
        BattleDamageType.init();
        BattleActionRegistry.init();
        BattleParticipantStat.init();
        BattleEquipmentSlot.init();
        ParticipantComponents.init();
    }
}
