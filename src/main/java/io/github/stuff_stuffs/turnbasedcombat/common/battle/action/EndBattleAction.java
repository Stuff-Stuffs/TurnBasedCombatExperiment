package io.github.stuff_stuffs.turnbasedcombat.common.battle.action;

import com.mojang.serialization.DynamicOps;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;

public final class EndBattleAction extends BattleAction {
    public static EndBattleAction INSTANCE = new EndBattleAction();

    private EndBattleAction() {
    }

    @Override
    public void applyToState(final BattleState state) {
        state.endBattle();
    }

    @Override
    protected <T> T encode(final DynamicOps<T> ops) {
        return ops.empty();
    }

    public static <T> EndBattleAction decode(final T o, final DynamicOps<T> ops) {
        return INSTANCE;
    }

    static {
        try {
            register(EndBattleAction.class);
        } catch (final NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
