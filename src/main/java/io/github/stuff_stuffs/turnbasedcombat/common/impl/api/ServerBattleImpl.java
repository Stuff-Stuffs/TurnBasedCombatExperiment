package io.github.stuff_stuffs.turnbasedcombat.common.impl.api;

import io.github.stuff_stuffs.turnbasedcombat.common.api.*;
import io.github.stuff_stuffs.turnbasedcombat.common.component.Components;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.api.turnorder.SimpleTurnOrderDecider;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.api.turnorder.TurnOrderDecider;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.component.BattleEntityComponentImpl;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Iterator;
import java.util.Set;

public class ServerBattleImpl extends AbstractBattleImpl implements Battle {
    private final TurnOrderDecider decider;

    public ServerBattleImpl(final BattleHandle handle, final BattleBounds bounds, final Set<BattleEntity> participants, final boolean active) {
        super(handle, bounds, new ServerBattleLog(), active);
        decider = new SimpleTurnOrderDecider();
        for (final BattleEntity participant : participants) {
            final BattleEntityComponentImpl impl = (BattleEntityComponentImpl) Components.BATTLE_ENTITY_COMPONENT_KEY.get(participant);
            log.push(BattleAction.createEntityJoinAction((Entity) participant));
            addParticipant(participant);
            impl.setBattleHandle(handle);
            decider.addEntity(participant);
        }
    }

    @Override
    public void tick() {
        if (shouldEnd()) {
            end(EndingReason.COMPLETED);
        } else {
            final Iterator<BattleEntity> iterator = participants.iterator();
            while (iterator.hasNext()) {
                final BattleEntity next = iterator.next();
                if (((Entity) next).isRemoved()) {
                    iterator.remove();
                    final Set<BattleEntity> team = teamMap.get(next.getTeam());
                    if (team != null) {
                        team.remove(next);
                    }
                    if (team != null && team.size() == 0) {
                        teamMap.remove(next.getTeam());
                    }
                } else if (next instanceof ServerPlayerEntity) {
                    ((ServerBattleLog) log).updatePlayer((ServerPlayerEntity) next, handle);
                }
            }
        }
    }

    @Override
    public boolean remove(final BattleEntity battleEntity) {
        final boolean success = super.remove(battleEntity);
        if (success) {
            decider.removeEntity(battleEntity);
            return true;
        }
        return false;
    }

    @Override
    public BattleEntity getCurrentTurnEntity() {
        return decider.getCurrent();
    }

    @Override
    public BattleLog getLog() {
        return log;
    }
}
