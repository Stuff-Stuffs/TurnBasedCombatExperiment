package io.github.stuff_stuffs.turnbasedcombat.common.battle.action.attack;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.effect.EntityEffectCollection;

import java.util.Collections;
import java.util.List;

public final class BasicAttackBattleAction extends BattleAction {
    public static final Codec<BasicAttackBattleAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BattleParticipantHandle.CODEC.fieldOf("attacker").forGetter(action -> action.handle),
            Codec.list(Codec.pair(BattleParticipantHandle.CODEC, AttackInfo.CODEC)).fieldOf("attacks").forGetter(action -> action.attacks),
            EntityEffectCollection.CODEC.fieldOf("attackerEffects").forGetter(action -> action.attackerEffects)
    ).apply(instance, BasicAttackBattleAction::new));

    private final List<Pair<BattleParticipantHandle, AttackInfo>> attacks;
    private final EntityEffectCollection attackerEffects;

    public BasicAttackBattleAction(final BattleParticipantHandle attacker, final BattleParticipantHandle target, final AttackInfo attack) {
        this(attacker, Collections.singletonList(Pair.of(target,attack)), new EntityEffectCollection());
    }

    public BasicAttackBattleAction(final BattleParticipantHandle attacker, final BattleParticipantHandle target, final AttackInfo attack, EntityEffectCollection attackerEffects) {
        this(attacker, Collections.singletonList(Pair.of(target,attack)), attackerEffects);
    }

    public BasicAttackBattleAction(final BattleParticipantHandle attacker, final List<Pair<BattleParticipantHandle, AttackInfo>> attacks, EntityEffectCollection attackerEffects) {
        super(attacker);
        this.attacks = attacks;
        this.attackerEffects = attackerEffects;
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
            attacker.addAllEffects(attackerEffects);
        } else {
            throw new RuntimeException("Invalid attacker");
        }
        for (final Pair<BattleParticipantHandle, AttackInfo> attack : attacks) {
            final EntityState target = state.getParticipant(attack.getFirst());
            if (target == null) {
                throw new RuntimeException();
            }
            final AttackInfo attackInfo = attack.getSecond();
            attackInfo.applyTarget(attacker, target, state);
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
