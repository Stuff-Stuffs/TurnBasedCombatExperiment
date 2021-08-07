package io.github.stuff_stuffs.tbcexcore.common.battle.event.participant;

import io.github.stuff_stuffs.tbcexcore.common.battle.damage.BattleDamagePacket;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;

public interface PostDamageEvent {
    void onDamage(BattleParticipantStateView state, BattleDamagePacket damagePacket);

    interface Mut {
        void onDamage(BattleParticipantState state, BattleDamagePacket damagePacket);
    }
}
