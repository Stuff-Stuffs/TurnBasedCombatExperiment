package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.stat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.util.CodecUtil;
import it.unimi.dsi.fastutil.ints.Int2ReferenceLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class EntityStats {
    public static final Codec<EntityStats> CODEC = Codec.unboundedMap(EntityStatType.REGISTRY, ModifierList.CODEC).xmap(EntityStats::new, stats -> stats.modifiers);
    private final Map<EntityStatType<?>, ModifierList<?>> modifiers;

    private EntityStats(final Map<EntityStatType<?>, ModifierList<?>> modifiers) {
        this.modifiers = new Reference2ObjectOpenHashMap<>(modifiers);
    }

    public EntityStats() {
        modifiers = new Reference2ObjectOpenHashMap<>();
    }

    public <T> EntityStatModifierHandle addModifier(final EntityStatType<T> type, final EntityStatModifier<T> modifier) {
        final ModifierList<T> modifiers = (ModifierList<T>) this.modifiers.computeIfAbsent(type, ModifierList::new);
        return modifiers.add(modifier);
    }

    public void removeModifier(final EntityStatModifierHandle handle) {
        final ModifierList<?> modifiers = this.modifiers.computeIfAbsent(handle.type(), ModifierList::new);
        modifiers.remove(handle);
    }

    public <T> T get(final EntityStatType<T> type) {
        final T val = type.defaultValue().get();
        final ModifierList<T> modifiers = (ModifierList<T>) this.modifiers.get(type);
        if (modifiers != null) {
            return modifiers.apply(val);
        }
        return val;
    }

    private static class ModifierList<T> {
        public static final Codec<ModifierList<?>> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityStatType.REGISTRY.fieldOf("type").forGetter(list -> list.statType),
                CodecUtil.createLinkedMapCodec(Codec.INT, EntityStatModifierType.CODEC).fieldOf("modifiers").forGetter(list -> (Map<Integer, EntityStatModifier<?>>) (Object) list.modifiers)
        ).apply(instance, ModifierList::new));
        private final EntityStatType<T> statType;
        private int nextId = 0;
        private final Int2ReferenceMap<EntityStatModifier<T>> modifiers;
        private final List<EntityStatModifier<T>> sorted;

        private ModifierList(final EntityStatType<T> statType, final Map<Integer, EntityStatModifier<?>> map) {
            this.statType = statType;
            modifiers = new Int2ReferenceLinkedOpenHashMap<>();
            sorted = new ReferenceArrayList<>();
            int max = 0;
            for (final Map.Entry<Integer, EntityStatModifier<?>> entry : map.entrySet()) {
                max = Math.max(max, entry.getKey());
                modifiers.put(entry.getKey().intValue(), (EntityStatModifier<T>) entry.getValue());
                sorted.add((EntityStatModifier<T>) entry.getValue());
            }
            sorted.sort(Comparator.comparingInt(EntityStatModifier::getApplicationStage));
            nextId = max + 1;
        }

        private ModifierList(final EntityStatType<T> statType) {
            this.statType = statType;
            modifiers = new Int2ReferenceLinkedOpenHashMap<>();
            sorted = new ReferenceArrayList<>();
        }

        public EntityStatModifierHandle add(final EntityStatModifier<T> modifier) {
            final EntityStatModifierHandle handle = new EntityStatModifierHandle(statType, nextId++);
            modifiers.put(handle.id(), modifier);
            sorted.add(modifier);
            sorted.sort(Comparator.comparingInt(EntityStatModifier::getApplicationStage));
            return handle;
        }

        public void remove(final EntityStatModifierHandle handle) {
            if (handle.type() != statType) {
                throw new IllegalArgumentException();
            }
            final EntityStatModifier<T> removed = modifiers.remove(handle.id());
            if (removed != null) {
                sorted.remove(removed);
            }
        }

        public T apply(T value) {
            for (final EntityStatModifier<T> modifier : sorted) {
                value = modifier.modify(value);
            }
            return value;
        }
    }
}
