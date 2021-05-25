package io.github.stuff_stuffs.turnbasedcombat.common.battle.effect;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;
import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

public final class EntityEffectCollection {
    public static final Codec<EntityEffectCollection> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<EntityEffectCollection, T>> decode(final DynamicOps<T> ops, final T input) {
            final MapLike<T> map = ops.getMap(input).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final int nextId = ops.getNumberValue(map.get("nextId"), 0).intValue();
            final Reference2IntMap<EntityEffectRegistry.Type<?>> ids = new Reference2IntOpenHashMap<>();
            final SortedMap<EntityEffectRegistry.Type<?>, EntityEffect> effects = new Object2ObjectAVLTreeMap<>();
            ops.getList(map.get("effects")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            }).accept(in -> {
                final MapLike<T> entry = ops.getMap(in).getOrThrow(false, s -> {
                    throw new RuntimeException(s);
                });
                final int id = ops.getNumberValue(entry.get("id")).getOrThrow(false, s -> {
                    throw new RuntimeException(s);
                }).intValue();
                final EntityEffect effect = EntityEffectRegistry.CODEC.decode(ops, entry.get("data")).getOrThrow(false, s -> {
                    throw new RuntimeException(s);
                }).getFirst();
                ids.put(effect.getType(), id);
                effects.put(effect.getType(), effect);

            });
            return DataResult.success(Pair.of(new EntityEffectCollection(ids, nextId, effects), ops.empty()));
        }

        @Override
        public <T> DataResult<T> encode(final EntityEffectCollection input, final DynamicOps<T> ops, final T prefix) {
            final ListBuilder<T> builder = ops.listBuilder();
            for (final Map.Entry<EntityEffectRegistry.Type<?>, EntityEffect> entry : input.effects.entrySet()) {
                builder.add(
                        ops.mapBuilder().
                                add(
                                        "data",
                                        EntityEffectRegistry.CODEC.encodeStart(ops, entry.getValue())
                                ).add(
                                        "id",
                                        ops.createInt(input.ids.getInt(entry.getKey()))
                                ).build(ops.empty())
                );
            }
            return ops.mapBuilder().add(
                    "effects",
                    builder.build(ops.empty())
            ).add(
                    "nextId",
                    ops.createInt(input.nextId)
            ).build(ops.empty());
        }
    };
    private final Reference2IntMap<EntityEffectRegistry.Type<?>> ids;
    private int nextId;
    private final SortedMap<EntityEffectRegistry.Type<?>, EntityEffect> effects;

    private EntityEffectCollection(final Reference2IntMap<EntityEffectRegistry.Type<?>> ids, final int nextId, final SortedMap<EntityEffectRegistry.Type<?>, EntityEffect> effects) {
        this.ids = ids;
        this.nextId = nextId;
        this.effects = effects;
    }

    public EntityEffectCollection() {
        ids = new Reference2IntOpenHashMap<>();
        effects = new Object2ObjectAVLTreeMap<>();
    }

    public void add(final EntityEffect effect) {
        final EntityEffect current = effects.get(effect.getType());
        if (current != null) {
            final EntityEffect combined = current.getType().combiner.apply(current, effect);
            effects.remove(current.getType());
            effects.put(combined.getType(), combined);
        } else {
            ids.put(effect.getType(), nextId++);
            effects.put(effect.getType(), effect);
        }
    }

    public void clear(final EntityEffectRegistry.Type<?> type) {
        if (effects.remove(type) != null) {
            ids.removeInt(type);
        }
    }

    public @Nullable EntityEffect get(final EntityEffectRegistry.Type<?> type) {
        return effects.get(type);
    }

    public void tick(final EntityState entityState, final BattleStateView battleState) {
        final SortedSet<Map.Entry<EntityEffectRegistry.Type<?>, EntityEffect>> effectSet = new ObjectAVLTreeSet<>((o1, o2) -> {
            final int comp = Integer.compare(o1.getValue().getApplicationStage(), o2.getValue().getApplicationStage());
            if (comp == 0) {
                return Integer.compare(ids.getInt(o1.getValue().getType()), ids.getInt(o2.getValue().getType()));
            }
            return comp;
        });
        effectSet.addAll(effects.entrySet());
        for (final Map.Entry<EntityEffectRegistry.Type<?>, EntityEffect> entry : effectSet) {
            entry.getValue().tick(entityState, battleState);
            if (entry.getValue().shouldRemove()) {
                ids.removeInt(entry.getKey());
                effects.remove(entry.getKey());
            }
        }
    }
}
