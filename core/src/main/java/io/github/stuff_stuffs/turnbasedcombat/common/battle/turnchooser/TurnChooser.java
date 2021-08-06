package io.github.stuff_stuffs.turnbasedcombat.common.battle.turnchooser;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantHandle;

import java.util.ArrayList;
import java.util.List;

//TODO variants
public final class TurnChooser {
    private final List<BattleParticipantHandle> participants;
    private int index;

    public TurnChooser(final BattleStateView view) {
        participants = new ArrayList<>();
        view.getEvent(BattleStateView.POST_PARTICIPANT_JOIN_EVENT).register((battleStateView, participantView) -> participants.add(participantView.getHandle()));
        view.getEvent(BattleStateView.POST_PARTICIPANT_LEAVE_EVENT).register((battleState, participantView) -> {
            int ind = -1;
            for (int i = 0; i < participants.size(); i++) {
                if (participants.get(i).equals(participantView.getHandle())) {
                    ind = i;
                    break;
                }
            }
            if (ind == -1) {
                index = 0;
            } else {
                participants.remove(ind);
                if (index >= ind) {
                    index--;
                    if (index < 0) {
                        index = participants.size() - index;
                    }
                }
            }
        });
    }

    public BattleParticipantHandle getCurrentTurn() {
        return participants.get(index);
    }

    public void advance() {
        index = (index + 1) % participants.size();
    }

    public boolean valid() {
        return participants.size() > 0;
    }
}
