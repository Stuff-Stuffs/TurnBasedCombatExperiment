package io.github.stuff_stuffs.turnbasedcombat.common.battle.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.effect.EntityEffectFactory;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.effect.EntityEffectFactoryType;

import java.util.List;

public final class AddEffectsBattleAction extends BattleAction {
    public static final Codec<AddEffectsBattleAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BattleParticipantHandle.CODEC.fieldOf("target").forGetter(action -> action.handle),
            Codec.list(EntityEffectFactoryType.CODEC).fieldOf("effectFactories").forGetter(action -> action.effectFactories)
    ).apply(instance, AddEffectsBattleAction::new));
    private final List<EntityEffectFactory> effectFactories;

    public AddEffectsBattleAction(final BattleParticipantHandle target, final List<EntityEffectFactory> effectFactories) {
        super(target);
        this.effectFactories = effectFactories;
    }

    @Override
    public void applyToState(final BattleState state) {
        final EntityState participant = state.getParticipant(handle);
        if (participant == null) {
            throw new RuntimeException();
        }
        participant.addAllEffects(effectFactories);
    }

    @Override
    protected <T> T encode(final DynamicOps<T> ops) {
        return CODEC.encodeStart(ops, this).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
    }

    public static <T> AddEffectsBattleAction decode(final T o, final DynamicOps<T> ops) {
        return CODEC.parse(ops, o).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
    }
}
