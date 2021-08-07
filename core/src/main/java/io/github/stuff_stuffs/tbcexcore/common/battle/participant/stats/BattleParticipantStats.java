package io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.entity.BattleEntity;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleOpenHashMap;

import java.util.Map;

public final class BattleParticipantStats {
    public static final Codec<BattleParticipantStats> CODEC = Codec.unboundedMap(BattleParticipantStat.REGISTRY, Codec.DOUBLE).xmap(BattleParticipantStats::new, stats -> stats.baseStats);
    private final Reference2DoubleMap<BattleParticipantStat> baseStats;
    private final BattleParticipantStatModifiers modifiers;

    private BattleParticipantStats(final Map<BattleParticipantStat, Double> baseStats) {
        this.baseStats = new Reference2DoubleOpenHashMap<>(baseStats);
        modifiers = new BattleParticipantStatModifiers();
    }

    public BattleParticipantStats(final BattleEntity entity) {
        baseStats = new Reference2DoubleOpenHashMap<>();
        for (final BattleParticipantStat stat : BattleParticipantStat.REGISTRY) {
            baseStats.put(stat, stat.extract(entity));
        }
        modifiers = new BattleParticipantStatModifiers();
    }

    public BattleParticipantStatModifiers.Handle modify(final BattleParticipantStat stat, final BattleParticipantStatModifier modifier) {
        return modifiers.modify(stat, modifier);
    }

    public double getRaw(BattleParticipantStat stat) {
        return baseStats.getDouble(stat);
    }

    public double calculate(final BattleParticipantStat stat, final BattleStateView battleState, final BattleParticipantStateView participantState) {
        return modifiers.calculate(stat, getRaw(stat), battleState, participantState);
    }
}
