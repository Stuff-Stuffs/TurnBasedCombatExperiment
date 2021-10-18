package io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats;

import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import net.minecraft.text.Text;

import java.util.Comparator;

public interface BattleParticipantStatModifier {
    Text getTooltip();

    double modify(double in, BattleStateView battleState, BattleParticipantStateView participantState);

    int getPhase();

    Stage getStage();

    enum Stage {
        MULTIPLICATION,
        ADDITION;
        public static final Comparator<Stage> COMPARATOR = Comparator.naturalOrder();
    }
}
