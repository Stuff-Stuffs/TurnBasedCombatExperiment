package io.github.stuff_stuffs.turnbasedcombat.common.impl.api;

import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleBounds;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleHandle;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.Set;

public class ClientBattleImpl extends AbstractBattleImpl {
    private final World world;
    private BattleEntity currentTurn;

    public ClientBattleImpl(final BattleHandle handle, final BattleBounds bounds, final boolean active, final World world) {
        super(handle, bounds, new ClientBattleLog(), active);
        this.world = world;
        ((ClientBattleLog) getLog()).setBattle(this);
    }

    public void pushAction(final BattleAction action) {
        log.push(action);
    }

    public boolean remove(final int id) {
        boolean found = false;
        final Iterator<BattleEntity> iterator = participants.iterator();
        while (iterator.hasNext()) {
            final BattleEntity entity = iterator.next();
            if (((Entity) entity).getId() == id) {
                iterator.remove();
                final Set<BattleEntity> team = teamMap.get(entity.getTeam());
                if (team != null) {
                    team.remove(entity);
                }
                activeParticipants.remove(entity);
                found = true;
                break;
            }
        }
        return found;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void tick() {

    }

    public void setCurrentTurn(int id) {
        for (BattleEntity participant : participants) {
            if(((Entity)participant).getId()==id) {
                currentTurn = participant;
                return;
            }
        }
        throw new RuntimeException("Tried to set turn to entity not in battle");
    }

    @Override
    public BattleEntity getCurrentTurnEntity() {
        return currentTurn;
    }
}
