package io.github.stuff_stuffs.turnbasedcombat.common.battle.action.attack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;

public final class AttackBattleAction extends BattleAction {
    public static final Codec<AttackBattleAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BattleParticipantHandle.CODEC.fieldOf("attacker").forGetter(action -> action.handle),
            BattleParticipantHandle.CODEC.fieldOf("target").forGetter(action -> action.target),
            AttackInfo.CODEC.fieldOf("attack").forGetter(action -> action.attack)
    ).apply(instance, AttackBattleAction::new));

    private final BattleParticipantHandle target;
    private final AttackInfo attack;

    public AttackBattleAction(final BattleParticipantHandle attacker, final BattleParticipantHandle target, final AttackInfo attack) {
        super(attacker);
        this.target = target;
        this.attack = attack;
    }

    @Override
    public void applyToState(final BattleState state) {
        final EntityState attacker;
        if (handle.isUniversal()) {
            attacker = null;
        } else if (handle.participantId().equals(state.getCurrentTurn().getId())) {
            attacker = state.getParticipant(handle);
            if (attacker == null) {
                throw new RuntimeException();
            }
            attack.applyAttacker(attacker, state);
        } else {
            throw new RuntimeException("Invalid attacker");
        }
        final EntityState target = state.getParticipant(this.target);
        if (target == null) {
            throw new RuntimeException();
        }
        attack.applyTarget(attacker, target, state);
    }

    @Override
    protected <T> T encode(final DynamicOps<T> ops) {
        return CODEC.encodeStart(ops, this).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
    }

    public static <T> AttackBattleAction decode(final T o, final DynamicOps<T> ops) {
        return CODEC.parse(ops, o).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
    }
}
