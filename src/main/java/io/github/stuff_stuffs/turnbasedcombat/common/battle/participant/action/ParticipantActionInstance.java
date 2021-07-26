package io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.action;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.util.EitherList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public final class ParticipantActionInstance {
    private final ParticipantActionInfo info;
    private final BattleStateView battleState;
    private final BattleParticipantHandle user;
    private final EitherList<BlockPos, BattleParticipantHandle> list;
    private Set<BattleParticipantHandle> acceptableHandles;
    private Set<BlockPos> acceptablePositions;
    private TargetType nextTargetType;

    public ParticipantActionInstance(final ParticipantActionInfo info, final BattleStateView battleState, final BattleParticipantHandle user) {
        this.info = info;
        this.battleState = battleState;
        this.user = user;
        list = new EitherList<>();
        update();
    }

    public @Nullable TargetType getNextType() {
        return nextTargetType;
    }

    public boolean canActivate() {
        return info.canActivate(battleState, user, list);
    }

    public void activate() {
        if (info.canActivate(battleState, user, list)) {
            info.activate(battleState, user, list);
        } else {
            throw new RuntimeException();
        }
    }

    public Iterable<BlockPos> getValidTargetPositions() {
        return info.getValidTargetPositions(battleState, user, list);
    }

    public Iterable<BattleParticipantHandle> getValidTargetParticipants() {
        return info.getValidTargetParticipants(battleState, user, list);
    }

    public void acceptPosition(final BlockPos pos) {
        if (acceptablePositions != null) {
            if (acceptablePositions.contains(pos)) {
                list.addLeft(pos);
                update();
            } else {
                throw new RuntimeException();
            }
        } else {
            throw new RuntimeException();
        }
    }

    public void acceptParticipant(final BattleParticipantHandle pos) {
        if (acceptableHandles != null) {
            if (acceptableHandles.contains(pos)) {
                list.addRight(pos);
                update();
            } else {
                throw new RuntimeException();
            }
        } else {
            throw new RuntimeException();
        }
    }

    private void update() {
        nextTargetType = info.getNextTargetType(list);
        if (getNextType() == null) {
            acceptableHandles = null;
            acceptablePositions = null;
        } else {
            switch (getNextType()) {
                case ANY -> {
                    acceptableHandles = info.getValidTargetParticipants(battleState, user, list);
                    acceptablePositions = info.getValidTargetPositions(battleState, user, list);
                }
                case POSITION -> {
                    acceptableHandles = null;
                    acceptablePositions = info.getValidTargetPositions(battleState, user, list);
                }
                case PARTICIPANT -> {
                    acceptableHandles = info.getValidTargetParticipants(battleState, user, list);
                    acceptablePositions = null;
                }
            }
        }
    }


}
