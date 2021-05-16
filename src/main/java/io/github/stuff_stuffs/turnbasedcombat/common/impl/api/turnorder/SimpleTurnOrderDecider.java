package io.github.stuff_stuffs.turnbasedcombat.common.impl.api.turnorder;

import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleEntity;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

import java.util.Set;

public class SimpleTurnOrderDecider implements TurnOrderDecider {
    private final Set<BattleEntity> active;
    private final ObjectArrayFIFOQueue<BattleEntity> queue;
    private BattleEntity current;

    public SimpleTurnOrderDecider() {
        active = new ReferenceOpenHashSet<>();
        queue = new ObjectArrayFIFOQueue<>();
    }

    @Override
    public void addEntity(final BattleEntity entity) {
        if (active.add(entity)) {
            if (current == null) {
                current = entity;
            } else {
                queue.enqueue(entity);
            }
        }
    }

    @Override
    public void removeEntity(final BattleEntity entity) {
        active.remove(entity);
    }

    @Override
    public BattleEntity getCurrent() {
        if (current == null) {
            throw new RuntimeException("Tried to get turn with 0 entities");
        }
        return current;
    }

    @Override
    public void advance() {
        if(current==null) {
            throw new RuntimeException("Tried to advance turn with 0 entities");
        }
        queue.enqueue(current);
        current = queue.dequeue();
    }
}
