package io.github.stuff_stuffs.turnbasedcombat.common.battle.world;

import io.github.stuff_stuffs.turnbasedcombat.client.TurnBasedCombatExperimentClient;
import io.github.stuff_stuffs.turnbasedcombat.client.network.RequestBattleSender;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ClientBattleWorld implements BattleWorld, Iterable<Battle> {
    private static final Logger LOGGER = TurnBasedCombatExperimentClient.LOGGER;
    private final Map<BattleHandle, Battle> battleMap;
    private final Set<BattleHandle> requestedUpdates;

    public ClientBattleWorld() {
        battleMap = new Object2ObjectOpenHashMap<>();
        requestedUpdates = new ObjectOpenHashSet<>();
    }

    @Override
    public @Nullable Battle getBattle(final BattleHandle handle) {
        requestUpdate(handle);
        return battleMap.get(handle);
    }

    public void requestUpdate(final BattleHandle handle) {
        requestedUpdates.add(handle);
    }

    public void tick() {
        for (final BattleHandle handle : requestedUpdates) {
            final Battle battle = battleMap.get(handle);
            RequestBattleSender.send(handle, battle == null ? 0 : battle.getTimeline().getSize(), battle != null);
        }
        requestedUpdates.clear();
    }

    public void addBattle(final BattleHandle handle, final BattleBounds bounds) {
        final Battle battle = battleMap.get(handle);
        if (battle != null) {
            LOGGER.error("Attempt to add already existing battle: {}", handle);
        } else {
            battleMap.put(handle, new Battle(handle, bounds));
        }
    }

    public void update(final BattleHandle handle, final int beforeSize, final List<BattleAction<?>> actions) {
        final Battle battle = battleMap.get(handle);
        if (battle == null) {
            LOGGER.error("Battle: {}, with size: {}, not found", handle, beforeSize);
        } else {
            if (battle.getTimeline().getSize() < beforeSize) {
                LOGGER.error("Battle {}, has smaller size {}, than expected {}", handle, battle.getTimeline().getSize(), beforeSize);
            } else {
                battle.trimAndAppend(beforeSize, actions);
            }
        }
    }

    @NotNull
    @Override
    public Iterator<Battle> iterator() {
        return battleMap.values().iterator();
    }
}
