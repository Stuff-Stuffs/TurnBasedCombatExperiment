package io.github.stuff_stuffs.turnbasedcombat.common.battle.action;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.attack.AttackBattleAction;

public final class BattleActions {
    public static void init() {
        BattleAction.register(EndBattleAction.class, EndBattleAction::decode);
        BattleAction.register(EndTurnAction.class, EndTurnAction::decode);
        BattleAction.register(JoinBattleAction.class, JoinBattleAction::decode);
        BattleAction.register(LeaveBattleAction.class, LeaveBattleAction::decode);
        BattleAction.register(NoopBattleAction.class, NoopBattleAction::decode);
        BattleAction.register(AttackBattleAction.class, AttackBattleAction::decode);
    }

    private BattleActions() {
    }
}
