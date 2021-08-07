package io.github.stuff_stuffs.tbcexcore.common.battle.event.participant;

import io.github.stuff_stuffs.tbcexcore.common.battle.damage.BattleDamagePacket;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;

public interface PreDamageEvent {
    void onDamage(BattleParticipantStateView state, BattleDamagePacket damagePacket);

    interface Mut {
        BattleDamagePacket onDamage(BattleParticipantState state, BattleDamagePacket damagePacket);
    }
}
