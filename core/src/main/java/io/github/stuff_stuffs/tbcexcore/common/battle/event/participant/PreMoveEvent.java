package io.github.stuff_stuffs.tbcexcore.common.battle.event.participant;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexutil.common.path.Path;
import net.minecraft.util.math.BlockPos;

public interface PreMoveEvent {
    void onMove(BattleParticipantStateView state, BlockPos pos, Path path);

    interface Mut {
        boolean onMove(BattleParticipantState state, BlockPos pos, Path path);
    }
}
