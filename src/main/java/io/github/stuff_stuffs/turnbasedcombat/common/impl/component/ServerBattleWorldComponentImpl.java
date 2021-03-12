package io.github.stuff_stuffs.turnbasedcombat.common.impl.component;

import io.github.stuff_stuffs.turnbasedcombat.common.api.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.component.BattleWorldComponent;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.api.ServerBattleImpl;
import io.github.stuff_stuffs.turnbasedcombat.common.network.AddBattleS2C;
import io.github.stuff_stuffs.turnbasedcombat.common.network.RemoveBattleS2C;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class ServerBattleWorldComponentImpl implements BattleWorldComponent {
    private final AtomicLong nextBattleId;
    private final Map<BattleHandle, ServerBattleImpl> activeBattles;
    private final Map<BattleHandle, CompoundTag> suspendedBattles;
    private final ServerWorld world;

    public ServerBattleWorldComponentImpl(final ServerWorld world) {
        nextBattleId = new AtomicLong();
        activeBattles = new Object2ReferenceOpenHashMap<>();
        suspendedBattles = new Object2ReferenceOpenHashMap<>();
        this.world = world;
    }

    @Override
    public @Nullable Battle fromHandle(final BattleHandle handle) {
        ServerBattleImpl battle = activeBattles.get(handle);
        if (battle != null) {
            return battle;
        } else {
            final CompoundTag compoundTag = suspendedBattles.remove(handle);
            if (compoundTag != null) {
                final Entity entity = world.getEntity(compoundTag.getUuid("playerUUID"));
                if (entity instanceof PlayerEntity) {
                    battle = Battle.fromNbt(handle, world, (PlayerEntity) entity, compoundTag.getCompound("data"));
                    activeBattles.put(handle, battle);
                    AddBattleS2C.sendBattle(battle, (ServerPlayerEntity) battle.getPlayer());
                    return battle;
                } else {
                    throw new RuntimeException("Tried to get handle of battle without player in-game");
                }
            }
        }
        return null;
    }

    public BattleHandle createBattle(final ServerPlayerEntity entity, final Set<BattleEntity> entities) {
        final BattleHandle handle = BattleHandle.create(world, nextBattleId.getAndIncrement());
        ServerBattleImpl battle = new ServerBattleImpl(handle, entity, entities, true);
        activeBattles.put(handle, battle);
        AddBattleS2C.sendBattle(battle, (ServerPlayerEntity) battle.getPlayer());
        return handle;
    }

    @Override
    public void readFromNbt(final CompoundTag tag) {
        suspendedBattles.clear();
        nextBattleId.set(tag.getLong("nextBattleId"));
        final ListTag taggedBattles = tag.getList("battleMap", NbtType.COMPOUND);
        for (int i = 0; i < taggedBattles.size(); i++) {
            final CompoundTag battleEntry = taggedBattles.getCompound(i);
            final BattleHandle handle = BattleHandle.fromTag(battleEntry.getCompound("key"));
            final CompoundTag value = battleEntry.getCompound("value");
            suspendedBattles.put(handle, value);
        }
        for (final BattleHandle handle : activeBattles.keySet()) {
            suspendedBattles.remove(handle);
        }
    }

    @Override
    public void writeToNbt(final CompoundTag tag) {
        final ListTag battleMap = new ListTag();
        for (final Map.Entry<BattleHandle, ServerBattleImpl> entry : activeBattles.entrySet()) {
            final CompoundTag entryTag = new CompoundTag();
            entryTag.put("key", entry.getKey().toNbt());
            final CompoundTag value = new CompoundTag();
            value.putUuid("playerUUID", entry.getValue().getPlayer().getUuid());
            value.put("data", entry.getValue().toNbt());
            entryTag.put("value", value);
            battleMap.add(entryTag);
        }
        for (final Map.Entry<BattleHandle, CompoundTag> entry : suspendedBattles.entrySet()) {
            final CompoundTag entryTag = new CompoundTag();
            entryTag.put("key", entry.getKey().toNbt());
            entryTag.put("value", entry.getValue());
            battleMap.add(entryTag);
        }
        tag.putLong("nextBattleId", nextBattleId.get());
        tag.put("battleMap", battleMap);
    }

    private void suspend(final BattleHandle handle) {
        final ServerBattleImpl battle = activeBattles.get(handle);
        if (battle == null) {
            throw new NullPointerException();
        }
        if (!battle.shouldSuspend()) {
            throw new IllegalArgumentException();
        }
        final CompoundTag tag = new CompoundTag();
        tag.put("key", handle.toNbt());
        final CompoundTag entry = new CompoundTag();
        entry.putUuid("playerUUID", battle.getPlayer().getUuid());
        entry.put("data", battle.toNbt());
        tag.put("value", entry);
        if (suspendedBattles.put(handle, tag) != null) {
            throw new IllegalStateException();
        }
        RemoveBattleS2C.removeBattle(handle, world);
    }

    @Override
    public void tick() {
        activeBattles.entrySet().removeIf(battleHandleBattleEntry -> !battleHandleBattleEntry.getValue().isActive());
        for (final ServerBattleImpl battle : activeBattles.values()) {
            battle.tick();
        }
        final Iterator<Map.Entry<BattleHandle, ServerBattleImpl>> iterator = activeBattles.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<BattleHandle, ServerBattleImpl> entry = iterator.next();
            final BattleHandle handle = entry.getKey();
            final ServerBattleImpl battle = entry.getValue();
            if (battle.shouldSuspend()) {
                iterator.remove();
                suspend(handle);
            }
        }
    }
}
