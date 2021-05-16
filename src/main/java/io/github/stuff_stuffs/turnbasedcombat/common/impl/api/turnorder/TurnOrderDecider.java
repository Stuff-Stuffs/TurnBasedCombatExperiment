package io.github.stuff_stuffs.turnbasedcombat.common.impl.api.turnorder;

import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleEntity;

public interface TurnOrderDecider {
    void addEntity(BattleEntity entity);

    void removeEntity(BattleEntity entity);

    BattleEntity getCurrent();

    void advance();
}
