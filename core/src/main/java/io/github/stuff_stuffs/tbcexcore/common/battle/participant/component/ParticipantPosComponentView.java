package io.github.stuff_stuffs.tbcexcore.common.battle.participant.component;

import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import io.github.stuff_stuffs.tbcexutil.common.HorizontalDirection;
import net.minecraft.util.math.BlockPos;

public interface ParticipantPosComponentView extends ParticipantComponent {
    HorizontalDirection getFacing();

    BlockPos getPos();

    BattleParticipantBounds getBounds();

    BattleParticipantBounds getBounds(HorizontalDirection facing);
}
