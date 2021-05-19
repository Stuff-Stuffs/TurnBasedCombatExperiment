package io.github.stuff_stuffs.turnbasedcombat.common.battle.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;

public final class LeaveBattleAction extends BattleAction {
    public static final Codec<LeaveBattleAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BattleParticipantHandle.CODEC.fieldOf("handle").forGetter(action -> action.handle),
            BattleParticipantHandle.CODEC.fieldOf("target").forGetter(action -> action.target)
    ).apply(instance, LeaveBattleAction::new));
    private final BattleParticipantHandle target;

    public LeaveBattleAction(final BattleParticipantHandle handle, final BattleParticipantHandle target) {
        super(handle);
        this.target = target;
    }

    @Override
    public void applyToState(final BattleState state) {
        if (!state.removeParticipant(target)) {
            throw new RuntimeException();
        }
        if (state.getTeamCount() < 2) {
            state.endBattle();
        }
    }

    @Override
    protected <T> T encode(final DynamicOps<T> ops) {
        return CODEC.encodeStart(ops, this).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
    }

    public static <T> LeaveBattleAction decode(final T o, final DynamicOps<T> ops) {
        return CODEC.parse(ops, o).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
    }
}
