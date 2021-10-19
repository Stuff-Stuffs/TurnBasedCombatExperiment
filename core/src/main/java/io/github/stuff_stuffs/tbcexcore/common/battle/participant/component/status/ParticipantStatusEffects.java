package io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.status;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.tbcexcore.common.TBCExCore;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.entity.BattleEntity;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.registry.Registry;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

public final class ParticipantStatusEffects {
    public static final Registry<Type> REGISTRY = FabricRegistryBuilder.createSimple(Type.class, TBCExCore.createId("status_effect")).buildAndRegister();

    public static final class Type {
        public final BiFunction<BattleParticipantStateView, BattleEntity, ParticipantStatusEffect> extractor;
        public final BinaryOperator<ParticipantStatusEffect> combiner;
        public final Codec<ParticipantStatusEffect> codec;

        public Type(final BiFunction<BattleParticipantStateView, BattleEntity, ParticipantStatusEffect> extractor, final BinaryOperator<ParticipantStatusEffect> combiner, final Codec<ParticipantStatusEffect> codec) {
            this.extractor = extractor;
            this.combiner = combiner;
            this.codec = codec;
        }
    }

    private ParticipantStatusEffects() {
    }
}
