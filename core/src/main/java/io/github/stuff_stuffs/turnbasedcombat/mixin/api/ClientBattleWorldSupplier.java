package io.github.stuff_stuffs.turnbasedcombat.mixin.api;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.world.ClientBattleWorld;

public interface ClientBattleWorldSupplier extends BattleWorldSupplier {
    @Override
    ClientBattleWorld tbcex_getBattleWorld();
}
