package io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.status;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.ParticipantComponent;

public interface ParticipantStatusEffectComponentView extends ParticipantComponent {
    ParticipantStatusEffect getStatusEffect(ParticipantStatusEffects.Type type);

    Iterable<ParticipantStatusEffects.Type> getActiveStatusEffects();
}
