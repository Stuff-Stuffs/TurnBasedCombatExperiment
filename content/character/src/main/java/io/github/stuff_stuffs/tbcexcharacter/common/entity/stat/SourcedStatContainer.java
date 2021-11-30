package io.github.stuff_stuffs.tbcexcharacter.common.entity.stat;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class SourcedStatContainer {
    public static final Codec<SourcedStatContainer> CODEC = Codec.unboundedMap(BattleParticipantStat.REGISTRY.getCodec(), Container.CODEC).xmap(SourcedStatContainer::new, c -> c.stats);
    private final Map<BattleParticipantStat, Container> stats;

    private SourcedStatContainer(final Map<BattleParticipantStat, Container> stats) {
        this.stats = new Reference2ObjectOpenHashMap<>(stats);
        for (final BattleParticipantStat stat : BattleParticipantStat.REGISTRY) {
            if (!this.stats.containsKey(stat)) {
                stats.put(stat, new Container());
            }
        }
    }

    public SourcedStatContainer() {
        stats = new Reference2ObjectOpenHashMap<>();
        for (final BattleParticipantStat stat : BattleParticipantStat.REGISTRY) {
            stats.put(stat, new Container());
        }
    }

    public double getStat(final BattleParticipantStat stat) {
        return stats.get(stat).sum;
    }

    public void forEach(final BattleParticipantStat stat, final Consumer<SourcedStat<?>> consumer) {
        stats.get(stat).stats.forEach(consumer);
    }

    private static final class Container {
        public static final Codec<Container> CODEC = Codec.list(SourcedStat.CODEC).xmap(Container::new, container -> container.stats);
        private final List<SourcedStat<?>> stats;
        private double sum;

        private Container(final List<SourcedStat<?>> stats) {
            this.stats = new ArrayList<>(stats);
            for (final SourcedStat<?> stat : this.stats) {
                sum += stat.getAmount();
            }
        }

        public Container() {
            stats = new ArrayList<>();
            sum = 0;
        }
    }
}
