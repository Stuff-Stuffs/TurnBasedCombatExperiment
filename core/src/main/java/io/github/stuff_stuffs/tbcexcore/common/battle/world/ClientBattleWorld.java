package io.github.stuff_stuffs.tbcexcore.common.battle.world;

import io.github.stuff_stuffs.tbcexcore.client.TBCExCoreClient;
import io.github.stuff_stuffs.tbcexcore.client.network.RequestBattleSender;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.BattleAction;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.world.World;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ClientBattleWorld implements BattleWorld, Iterable<Battle> {
    private static final Logger LOGGER = TBCExCoreClient.LOGGER;
    private final Map<BattleHandle, Battle> battleMap;
    private final Set<BattleHandle> requestedUpdates;
    private final World world;

    public ClientBattleWorld(World world) {
        this.world = world;
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
        for (final Battle battle : battleMap.values()) {
            battle.tick();
        }
    }

    public void addBattle(final BattleHandle handle, final BattleBounds bounds, final int turnTimerRemaining, final int turnTimerMax) {
        final Battle battle = battleMap.get(handle);
        if (battle != null) {
            LOGGER.error("Attempt to add already existing battle: {}", handle);
        } else {
            final Battle b = new Battle(handle, bounds, turnTimerRemaining, turnTimerMax);
            b.setWorld(world);
            battleMap.put(handle, b);
        }
    }

    public void update(final BattleHandle handle, final int beforeSize, final List<BattleAction<?>> actions, int turnTimerRemaining, int turnTimerMax) {
        final Battle battle = battleMap.get(handle);
        if (battle == null) {
            LOGGER.error("Battle: {}, with size: {}, not found", handle, beforeSize);
        } else {
            if (battle.getTimeline().getSize() < beforeSize) {
                LOGGER.error("Battle {}, has smaller size {}, than expected {}", handle, battle.getTimeline().getSize(), beforeSize);
            } else {
                battle.trimAndAppend(beforeSize, actions, turnTimerRemaining, turnTimerMax);
            }
        }
    }

    @NotNull
    @Override
    public Iterator<Battle> iterator() {
        return battleMap.values().iterator();
    }
}
