package io.github.stuff_stuffs.turnbasedcombat.common.battle.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;

public final class NoopBattleAction extends BattleAction {
    public static final Codec<NoopBattleAction> CODEC = BattleParticipantHandle.CODEC.xmap(NoopBattleAction::new, action -> action.handle);

    public NoopBattleAction(final BattleParticipantHandle handle) {
        super(handle);
    }

    @Override
    public void applyToState(final BattleState state) {

    }

    @Override
    protected <T> T encode(final DynamicOps<T> ops) {
        return CODEC.encodeStart(ops, this).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
    }

    public static <T> NoopBattleAction decode(final T o, final DynamicOps<T> ops) {
        return CODEC.parse(ops, o).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
    }
}
