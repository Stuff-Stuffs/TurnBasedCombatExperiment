package io.github.stuff_stuffs.turnbasedcombat.common.battle.action;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.attack.BasicAttackBattleAction;

public final class BattleActions {
    public static void init() {
        BattleAction.register(EndBattleAction.class, EndBattleAction::decode);
        BattleAction.register(EndTurnAction.class, EndTurnAction::decode);
        BattleAction.register(JoinBattleAction.class, JoinBattleAction::decode);
        BattleAction.register(LeaveBattleAction.class, LeaveBattleAction::decode);
        BattleAction.register(NoopBattleAction.class, NoopBattleAction::decode);
        BattleAction.register(BasicAttackBattleAction.class, BasicAttackBattleAction::decode);
        BattleAction.register(AddEffectsBattleAction.class, AddEffectsBattleAction::decode);
    }

    private BattleActions() {
    }
}
