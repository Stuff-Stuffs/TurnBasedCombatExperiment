package io.github.stuff_stuffs.turnbasedcombat.common.battle.action;

import com.mojang.serialization.DynamicOps;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipant;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;
import net.minecraft.nbt.NbtOps;

public final class JoinBattleAction extends BattleAction {
    private final BattleParticipant participant;

    public JoinBattleAction(final BattleParticipant participant) {
        this.participant = participant;
    }

    @Override
    public void applyToState(final BattleState state) {
        //TODO implement actual copying
        final BattleParticipant participant = decode(encode(NbtOps.INSTANCE), NbtOps.INSTANCE).participant;
        state.addParticipant(participant);
    }

    @Override
    protected <T> T encode(final DynamicOps<T> ops) {
        return BattleParticipant.CODEC.encode(participant, ops, ops.empty()).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
    }

    public static <T> JoinBattleAction decode(final T o, final DynamicOps<T> ops) {
        return new JoinBattleAction(BattleParticipant.CODEC.decode(ops, o).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        }).getFirst());
    }

    static {
        try {
            BattleAction.register(JoinBattleAction.class);
        } catch (final NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
