package io.github.stuff_stuffs.turnbasedcombat.common.battle.event.participant;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.damage.BattleDamagePacket;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantStateView;

public interface PostDamageEvent {
    void onDamage(BattleParticipantStateView state, BattleDamagePacket damagePacket);

    interface Mut {
        void onDamage(BattleParticipantState state, BattleDamagePacket damagePacket);
    }
}
