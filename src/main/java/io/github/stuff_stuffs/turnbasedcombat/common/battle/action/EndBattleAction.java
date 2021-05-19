package io.github.stuff_stuffs.turnbasedcombat.common.battle.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;

public final class EndBattleAction extends BattleAction {
    public static final Codec<EndBattleAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BattleParticipantHandle.CODEC.fieldOf("handle").forGetter(action -> action.handle)
    ).apply(instance, EndBattleAction::new));

    private EndBattleAction(final BattleParticipantHandle handle) {
        super(handle);
    }

    @Override
    public void applyToState(final BattleState state) {
        if (handle.isUniversal()) {
            state.endBattle();
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    protected <T> T encode(final DynamicOps<T> ops) {
        return CODEC.encodeStart(ops, this).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
    }

    public static <T> EndBattleAction decode(final T o, final DynamicOps<T> ops) {
        return CODEC.parse(ops, o).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
    }
}
