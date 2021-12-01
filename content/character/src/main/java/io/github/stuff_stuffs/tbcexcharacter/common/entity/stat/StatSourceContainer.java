package io.github.stuff_stuffs.tbcexcharacter.common.entity.stat;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.tbcexcharacter.common.battle.participant.stats.BattleParticipantStatInfo;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.util.Unit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class StatSourceContainer {
    public static final Codec<StatSourceContainer> CODEC = Codec.unboundedMap(BattleParticipantStat.REGISTRY.getCodec(), Container.CODEC).xmap(StatSourceContainer::new, c -> c.stats);
    private final Map<BattleParticipantStat, Container> stats;

    private StatSourceContainer(final Map<BattleParticipantStat, Container> stats) {
        this.stats = new Reference2ObjectOpenHashMap<>(stats);
        for (final BattleParticipantStat stat : BattleParticipantStat.REGISTRY) {
            if (!this.stats.containsKey(stat)) {
                stats.put(stat, new Container());
            }
        }
    }

    public StatSourceContainer() {
        stats = new Reference2ObjectOpenHashMap<>();
        for (final BattleParticipantStat stat : BattleParticipantStat.REGISTRY) {
            stats.put(stat, new Container());
        }
        for (final BattleParticipantStat stat : BattleParticipantStat.REGISTRY) {
            add(stat, new StatSource<>(StatSources.BASE_TYPE, Unit.INSTANCE, BattleParticipantStatInfo.get(stat).getBase()));
        }
    }

    public double getStat(final BattleParticipantStat stat) {
        return stats.get(stat).sum;
    }

    public void forEach(final BattleParticipantStat stat, final StatSources.ForEach forEach) {
        final Iterator<StatSource<?>> iterator = stats.get(stat).getIterator();
        while (iterator.hasNext()) {
            forEach.accept(iterator.next(), !iterator.hasNext());
        }
    }

    public void add(final BattleParticipantStat stat, final StatSource<?> source) {
        stats.get(stat).add(source);
    }

    public Iterator<StatSource<?>> getIterator(final BattleParticipantStat stat) {
        return stats.get(stat).getIterator();
    }

    private static final class Container {
        public static final Codec<Container> CODEC = Codec.list(StatSource.CODEC).xmap(Container::new, container -> container.stats);
        private final List<StatSource<?>> stats;
        private double sum;

        private Container(final List<StatSource<?>> stats) {
            this.stats = new ArrayList<>(stats);
            for (final StatSource<?> stat : this.stats) {
                sum += stat.getAmount();
            }
        }

        public Container() {
            stats = new ArrayList<>();
            sum = 0;
        }

        public void add(final StatSource<?> stat) {
            stats.add(stat);
            sum += stat.getAmount();
        }

        public Iterator<StatSource<?>> getIterator() {
            return stats.iterator();
        }
    }
}
