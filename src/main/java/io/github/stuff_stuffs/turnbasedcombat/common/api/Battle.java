package io.github.stuff_stuffs.turnbasedcombat.common.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

public interface Battle {
    Logger LOGGER = LogManager.getLogger("Battle");

    boolean remove(BattleEntity battleEntity);

    Set<BattleEntity> getBattleEntities();

    Set<BattleEntity> getAllies(BattleEntity battleEntity);

    Set<BattleEntity> getEnemies(BattleEntity battleEntity);

    boolean contains(BattleEntity battleEntity);

    void addParticipant(BattleEntity battleEntity);

    boolean isActive();

    void end(EndingReason reason);

    EndingReason getEndingReason();

    Set<BattleEntity> getActiveBattleEntities();

    void tick();

    BattleEntity getCurrentTurnEntity();

    BattleHandle getHandle();

    BattleBounds getBounds();

    BattleLog getLog();

    enum EndingReason {
        COMPLETED,
        NO_ACTIVE_PARTICIPANTS
    }
}
