package io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target;

import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface TargetType<T extends TargetInstance> {
    @Nullable T find(Vec3d pos, Vec3d direction, BattleParticipantHandle user, Battle battle);

    void render(@Nullable TargetInstance hovered, List<TargetInstance> targeted, BattleParticipantHandle user, BattleStateView battle, float tickDelta);

    boolean isAnyValid(BattleParticipantHandle user, Battle battle);
}
