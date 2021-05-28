package io.github.stuff_stuffs.turnbasedcombat.common.battle.action.attack;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;

import java.util.Collections;
import java.util.List;

public final class BasicAttackBattleAction extends BattleAction {
    public static final Codec<BasicAttackBattleAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BattleParticipantHandle.CODEC.fieldOf("attacker").forGetter(action -> action.handle),
            Codec.list(Codec.pair(BattleParticipantHandle.CODEC, AttackInfo.CODEC)).fieldOf("attacks").forGetter(action -> action.attacks)
    ).apply(instance, BasicAttackBattleAction::new));

    private final List<Pair<BattleParticipantHandle, AttackInfo>> attacks;

    public BasicAttackBattleAction(final BattleParticipantHandle attacker, final BattleParticipantHandle target, final AttackInfo attack) {
        this(attacker, Collections.singletonList(Pair.of(target, attack)));
    }

    public BasicAttackBattleAction(final BattleParticipantHandle attacker, final List<Pair<BattleParticipantHandle, AttackInfo>> attacks) {
        super(attacker);
        this.attacks = attacks;
    }

    @Override
    public void applyToState(final BattleState state) {
        if (!handle.isUniversal()) {
            if (!handle.participantId().equals(state.getCurrentTurn().getId())) {
                throw new RuntimeException("Invalid attacker");
            }
        }
        for (final Pair<BattleParticipantHandle, AttackInfo> attack : attacks) {
            final EntityState target = state.getParticipant(attack.getFirst());
            if (target == null) {
                throw new RuntimeException();
            }
            final AttackInfo attackInfo = attack.getSecond();
            attackInfo.applyTarget(target);
        }
    }

    @Override
    protected <T> T encode(final DynamicOps<T> ops) {
        return CODEC.encodeStart(ops, this).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
    }

    public static <T> BasicAttackBattleAction decode(final T o, final DynamicOps<T> ops) {
        return CODEC.parse(ops, o).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
    }
}
