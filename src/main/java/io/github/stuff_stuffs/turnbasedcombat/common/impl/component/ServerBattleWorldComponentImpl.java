package io.github.stuff_stuffs.turnbasedcombat.common.impl.component;

import io.github.stuff_stuffs.turnbasedcombat.common.api.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleBounds;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.component.BattleWorldComponent;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.api.ServerBattleImpl;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.api.ServerBattleLog;
import io.github.stuff_stuffs.turnbasedcombat.common.network.sender.RemoveBattle;
import io.github.stuff_stuffs.turnbasedcombat.common.network.sender.TryAddBattle;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class ServerBattleWorldComponentImpl implements BattleWorldComponent {
    private final AtomicLong nextBattleId;
    private final Map<BattleHandle, ServerBattleImpl> activeBattles;
    private final ServerWorld world;

    public ServerBattleWorldComponentImpl(final ServerWorld world) {
        nextBattleId = new AtomicLong();
        activeBattles = new Object2ReferenceOpenHashMap<>();
        this.world = world;
    }

    @Override
    public @Nullable Battle fromHandle(final BattleHandle handle) {
        return activeBattles.get(handle);
    }

    public BattleHandle createBattle(final Set<BattleEntity> entities, BattleBounds bounds) {
        final BattleHandle handle = BattleHandle.create(world, nextBattleId.getAndIncrement());
        final ServerBattleImpl battle = new ServerBattleImpl(handle, bounds, entities, true);
        activeBattles.put(handle, battle);
        TryAddBattle.send(world, battle, (ServerBattleLog) battle.getLog());
        return handle;
    }

    @Override
    public void readFromNbt(final CompoundTag tag) {
        nextBattleId.set(tag.getLong("nextBattleId"));
    }

    @Override
    public void writeToNbt(final CompoundTag tag) {
        tag.putLong("nextBattleId", nextBattleId.get());
    }

    @Override
    public void tick() {
        for (final ServerBattleImpl battle : activeBattles.values()) {
            battle.tick();
        }
        final Iterator<Map.Entry<BattleHandle, ServerBattleImpl>> iterator = activeBattles.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<BattleHandle, ServerBattleImpl> entry = iterator.next();
            final ServerBattleImpl battle = entry.getValue();
            if (!battle.isActive()) {
                RemoveBattle.send(world, entry.getKey());
                iterator.remove();
            }
        }
    }
}
