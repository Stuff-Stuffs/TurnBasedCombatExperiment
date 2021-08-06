package io.github.stuff_stuffs.turnbasedcombat.common.battle.event.participant;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.damage.BattleDamagePacket;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantStateView;

public interface PreDamageEvent {
    void onDamage(BattleParticipantStateView state, BattleDamagePacket damagePacket);

    interface Mut {
        BattleDamagePacket onDamage(BattleParticipantState state, BattleDamagePacket damagePacket);
    }
}
