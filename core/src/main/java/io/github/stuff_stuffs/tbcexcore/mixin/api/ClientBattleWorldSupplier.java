package io.github.stuff_stuffs.tbcexcore.mixin.api;

import io.github.stuff_stuffs.tbcexcore.common.battle.world.ClientBattleWorld;

public interface ClientBattleWorldSupplier extends BattleWorldSupplier {
    @Override
    ClientBattleWorld tbcex_getBattleWorld();
}
