package io.github.stuff_stuffs.tbcexcore.common.battle.participant.component;

import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import io.github.stuff_stuffs.tbcexutil.common.HorizontalDirection;
import net.minecraft.util.math.BlockPos;

public interface ParticipantPosComponentView extends ParticipantComponent {
    BlockPos getPos();

    BattleParticipantBounds getBounds();
}
