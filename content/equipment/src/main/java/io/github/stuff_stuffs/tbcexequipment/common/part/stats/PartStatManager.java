package io.github.stuff_stuffs.tbcexequipment.common.part.stats;

import io.github.stuff_stuffs.tbcexequipment.common.part.Part;
import io.github.stuff_stuffs.tbcexequipment.common.part.data.PartData;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.util.Identifier;

import java.util.Map;

public final class PartStatManager {
    private final Map<PartStat<?>, Container<?>> statMap = new Reference2ObjectOpenHashMap<>();
    private final Map<Identifier, PartStat<?>> stats = new Object2ReferenceOpenHashMap<>();

    public <K> K get(final PartData partData, final PartStat<K> stat, final PartStatContext ctx) {
        return ((Container<K>) statMap.get(stat)).extract(partData, ctx);
    }

    public <K> PartStat<K> getStat(final Identifier id, final Class<K> type, final Extractor<PartData, K> defaultExtractor) {
        final PartStat<?> stat = stats.computeIfAbsent(id, identifier -> new PartStat<K>() {
            @Override
            public Identifier getId() {
                return id;
            }

            @Override
            public Class<K> getType() {
                return type;
            }

            @Override
            public String toString() {
                return "PartStat: " + id;
            }
        });
        if (!statMap.containsKey(stat)) {
            statMap.put(stat, new Container<>(defaultExtractor));
        } else {
            if (stats.get(id).getType() != type) {
                throw new TBCExException("Tried to register same part stat with different types");
            }
        }
        return (PartStat<K>) stat;
    }

    public <T extends PartData, K> void register(final PartStat<K> stat, final Part<T> part, final Extractor<T, K> extractor) {
        ((Container<K>) statMap.get(stat)).registerExtractor(part, extractor);
    }

    private static final class Container<K> {
        private final Extractor<PartData, K> defaultExtractor;
        private final Map<Part<?>, Extractor<?, K>> extractors;

        private Container(final Extractor<PartData, K> defaultExtractor) {
            this.defaultExtractor = defaultExtractor;
            extractors = new Reference2ObjectOpenHashMap<>();
        }

        public <T extends PartData> void registerExtractor(final Part<T> part, final Extractor<T, K> extractor) {
            if (extractors.put(part, extractor) != null) {
                throw new TBCExException("Tried to register a part stat extractor twice");
            }
        }

        public <T extends PartData> K extract(final PartData part, final PartStatContext ctx) {
            return thunk(part.getType(), part, ctx);
        }

        private <T extends PartData> K thunk(final Part<T> part, final PartData data, final PartStatContext ctx) {
            return extract(part, (T) data, ctx);
        }

        public <T extends PartData> K extract(final Part<T> type, final T data, final PartStatContext ctx) {
            final Extractor<?, K> extractor = extractors.get(type);
            if (extractor != null) {
                return ((Extractor<T, K>) extractor).extract(data, ctx);
            }
            return defaultExtractor.extract(data, ctx);
        }
    }

    public interface Extractor<T extends PartData, K> {
        K extract(T data, PartStatContext ctx);
    }
}
