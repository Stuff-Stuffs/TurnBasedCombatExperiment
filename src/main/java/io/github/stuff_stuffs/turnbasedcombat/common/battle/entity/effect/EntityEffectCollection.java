package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.effect;

import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.entity.effect.*;
import io.github.stuff_stuffs.turnbasedcombat.common.util.CodecUtil;
import it.unimi.dsi.fastutil.objects.Reference2ObjectLinkedOpenHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class EntityEffectCollection implements Iterable<EntityEffectType> {
    public static final Codec<EntityEffectCollection> CODEC = CodecUtil.createLinkedMapCodec(EntityEffectType.REGISTRY, EntityEffectType.CODEC).xmap(EntityEffectCollection::new, effects -> effects.effects);
    private final Map<EntityEffectType, EntityEffect> effects;

    private EntityEffectCollection(final Map<EntityEffectType, EntityEffect> map) {
        effects = new Reference2ObjectLinkedOpenHashMap<>(map);
    }

    public EntityEffectCollection() {
        effects = new Reference2ObjectLinkedOpenHashMap<>();
    }

    public boolean add(final EntityEffectFactory factory, final EntityState entityState) {
        final EntityEffect effect = factory.create(entityState);
        final EntityEffect current = effects.get(effect.getType());
        if (current != null) {
            if (!entityState.getEvent(PreEntityCombineEffect.class).invoker().onCombineEffect(entityState, effect, current)) {
                final EntityEffect combined = effect.getType().combine(effect, current);
                if (combined.getType() != effect.getType()) {
                    throw new RuntimeException();
                }
                effects.put(combined.getType(), combined);
                current.deinitEvents();
                combined.initEvents(entityState);
                entityState.getEvent(PostEntityCombineEffect.class).invoker().onCombineEffect(entityState, effect, current, combined);
                return true;
            }
        } else {
            if (!entityState.getEvent(PreEntityAddEffect.class).invoker().onEntityAddEffect(entityState, effect)) {
                effects.put(effect.getType(), effect);
                effect.initEvents(entityState);
                entityState.getEvent(PostEntityAddEffect.class).invoker().onEntityAddEffect(entityState, effect);
                return true;
            }
        }
        return false;
    }

    public void addAll(final List<EntityEffectFactory> effects, final EntityState state) {
        for (final EntityEffectFactory effect : effects) {
            add(effect, state);
        }
    }

    public boolean clear(final EntityEffectType type, final EntityState entityState) {
        final EntityEffect effect = effects.get(type);
        if (effect != null) {
            if (!entityState.getEvent(PreEntityRemoveEffect.class).invoker().onRemoveEffect(entityState, effect)) {
                effects.remove(type);
                effect.deinitEvents();
                entityState.getEvent(PostEntityRemoveEffect.class).invoker().onRemoveEffect(entityState, effect);
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

    public EntityEffect get(final EntityEffectType type) {
        return effects.get(type);
    }

    @NotNull
    @Override
    public Iterator<EntityEffectType> iterator() {
        return Iterators.unmodifiableIterator(effects.keySet().iterator());
    }
}
