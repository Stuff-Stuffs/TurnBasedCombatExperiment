package io.github.stuff_stuffs.turnbasedcombat.common.battle.action;

import com.mojang.serialization.DynamicOps;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;

public final class LeaveBattleAction extends BattleAction {
    private final BattleParticipantHandle handle;

    public LeaveBattleAction(final BattleParticipantHandle handle) {
        this.handle = handle;
    }

    @Override
    public void applyToState(final BattleState state) {
        if (!state.removeParticipant(handle)) {
            throw new RuntimeException();
        }
        if (state.getTeamCount() < 2) {
            state.endBattle();
        }
    }

    @Override
    protected <T> T encode(final DynamicOps<T> ops) {
        return BattleParticipantHandle.CODEC.encode(handle, ops, ops.empty()).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
    }

    public static <T> BattleAction decode(final T o, final DynamicOps<T> ops) {
        return new LeaveBattleAction(BattleParticipantHandle.CODEC.decode(ops, o).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        }).getFirst());
    }

    static {
        try {
            BattleAction.register(LeaveBattleAction.class);
        } catch (final NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
