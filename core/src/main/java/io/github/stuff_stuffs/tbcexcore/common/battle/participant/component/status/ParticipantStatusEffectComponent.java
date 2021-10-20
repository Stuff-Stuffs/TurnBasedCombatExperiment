package io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.status;

import com.google.common.collect.Iterables;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.AbstractParticipantComponent;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.ParticipantComponents;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParticipantStatusEffectComponent extends AbstractParticipantComponent implements ParticipantStatusEffectComponentView {
    public static final Codec<ParticipantStatusEffectComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.list(ParticipantStatusEffect.CODEC).xmap(l -> {
                        Map<ParticipantStatusEffects.Type, ParticipantStatusEffect> effectMap = new Reference2ReferenceOpenHashMap<>();
                        for (Pair<ParticipantStatusEffects.Type, ParticipantStatusEffect> pair : l) {
                            effectMap.put(pair.getFirst(), pair.getSecond());
                        }
                        return effectMap;
                    }, ParticipantStatusEffectComponent::getComponentStream).fieldOf("effects").forGetter(component -> component.statusEffectMap)
            ).apply(instance, ParticipantStatusEffectComponent::new)
    );
    private final Map<ParticipantStatusEffects.Type, ParticipantStatusEffect> statusEffectMap;

    public ParticipantStatusEffectComponent(final Map<ParticipantStatusEffects.Type, ParticipantStatusEffect> statusEffectMap) {
        this.statusEffectMap = statusEffectMap;
    }

    @Override
    public void init(final BattleParticipantState state) {
        super.init(state);
        for (final ParticipantStatusEffect effect : statusEffectMap.values()) {
            effect.init(state);
        }
    }

    @Override
    public void deinitEvents() {
        super.deinitEvents();
        for (final ParticipantStatusEffect effect : statusEffectMap.values()) {
            effect.deinit();
        }
    }

    public void apply(final ParticipantStatusEffect effect) {
        final ParticipantStatusEffects.Type type = effect.getType();
        final ParticipantStatusEffect cur = statusEffectMap.remove(type);
        if (cur == null) {
            statusEffectMap.put(type, effect);
            effect.init(state);
        } else {
            cur.deinit();
            final ParticipantStatusEffect combined = type.combiner.apply(cur, effect);
            if (combined != null) {
                if (combined.getType() != type) {
                    //TODO
                    throw new RuntimeException();
                }
                statusEffectMap.put(type, combined);
                combined.init(state);
            }
        }
    }

    @Override
    public ParticipantStatusEffect getStatusEffect(final ParticipantStatusEffects.Type type) {
        return statusEffectMap.get(type);
    }

    @Override
    public Iterable<ParticipantStatusEffects.Type> getActiveStatusEffects() {
        return Iterables.unmodifiableIterable(statusEffectMap.keySet());
    }

    @Override
    public ParticipantComponents.Type<?, ?> getType() {
        return ParticipantComponents.STATUS_EFFECT_COMPONENT_TYPE;
    }

    private static List<Pair<ParticipantStatusEffects.Type, ParticipantStatusEffect>> getComponentStream(final Map<ParticipantStatusEffects.Type, ParticipantStatusEffect> map) {
        final List<Pair<ParticipantStatusEffects.Type, ParticipantStatusEffect>> componentList = new ArrayList<>(map.size());
        for (final Map.Entry<ParticipantStatusEffects.Type, ParticipantStatusEffect> entry : map.entrySet()) {
            componentList.add(Pair.of(entry.getKey(), entry.getValue()));
        }
        return componentList;
    }
}
