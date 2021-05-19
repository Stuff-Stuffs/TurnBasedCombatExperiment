package io.github.stuff_stuffs.turnbasedcombat.common.battle.action;

public final class BattleActions {
    public static void init() {
        BattleAction.register(EndBattleAction.class, EndBattleAction::decode);
        BattleAction.register(EndTurnAction.class, EndTurnAction::decode);
        BattleAction.register(JoinBattleAction.class, JoinBattleAction::decode);
        BattleAction.register(LeaveBattleAction.class, LeaveBattleAction::decode);
        BattleAction.register(NoopBattleAction.class, NoopBattleAction::decode);
    }

    private BattleActions() {
    }
}
