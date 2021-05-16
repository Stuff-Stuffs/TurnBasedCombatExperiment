package io.github.stuff_stuffs.turnbasedcombat.common.impl.component;

import com.mojang.datafixers.util.Either;
import io.github.stuff_stuffs.turnbasedcombat.common.api.*;
import io.github.stuff_stuffs.turnbasedcombat.common.component.BattleEntityComponent;
import io.github.stuff_stuffs.turnbasedcombat.common.component.BattleWorldComponent;
import io.github.stuff_stuffs.turnbasedcombat.common.component.Components;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.api.ClientBattleImpl;
import io.github.stuff_stuffs.turnbasedcombat.common.util.OptionalEither;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class ClientBattleWorldComponent implements BattleWorldComponent {
    private final Map<BattleHandle, ClientBattleImpl> activeBattles;
    private final Map<BattleHandle, PendingBattle> pendingBattles;
    private final ClientWorld clientWorld;
    private final Lock lock;

    public ClientBattleWorldComponent(final ClientWorld clientWorld) {
        lock = new ReentrantLock();
        this.clientWorld = clientWorld;
        activeBattles = new Object2ReferenceOpenHashMap<>();
        pendingBattles = new Object2ReferenceOpenHashMap<>();
    }

    @Override
    public @Nullable Battle fromHandle(final BattleHandle handle) {
        return activeBattles.get(handle);
    }

    public OptionalEither<ClientBattleImpl, PendingBattle> eitherFromHandle(final BattleHandle handle) {
        final ClientBattleImpl battle = activeBattles.get(handle);
        if (battle != null) {
            return OptionalEither.left(battle);
        }
        final PendingBattle pendingBattle = pendingBattles.get(handle);
        if (pendingBattle != null) {
            return OptionalEither.right(pendingBattle);
        }
        return OptionalEither.neither();
    }

    private void addBattle(final BattleHandle handle, final ClientBattleImpl battle) {
        if (activeBattles.put(handle, battle) != null) {
            throw new RuntimeException();
        }
    }

    public void addPendingBattle(final BattleHandle handle, final BattleBounds bounds) {
        lock.lock();
        if (pendingBattles.put(handle, new PendingBattle(bounds)) != null) {
            throw new RuntimeException("Battle already exists!");
        }
        lock.unlock();
    }

    public void removeBattle(final BattleHandle handle) {
        lock.lock();
        eitherFromHandle(handle).consume(battle -> {
            for (final BattleEntity battleEntity : battle.getBattleEntities()) {
                final BattleEntityComponent b = Components.BATTLE_ENTITY_COMPONENT_KEY.get(battleEntity);
                b.setBattleHandle(null);
            }
        }, battle -> {
            for (final Integer entityId : battle.entityIds) {
                final Entity entity = clientWorld.getEntityById(entityId);
                if (entity instanceof BattleEntity) {
                    final BattleEntityComponent battleEntity = Components.BATTLE_ENTITY_COMPONENT_KEY.get(entity);
                    battleEntity.setBattleHandle(null);
                }
            }
        }, () -> {
        });
        activeBattles.remove(handle);
        pendingBattles.remove(handle);
        lock.unlock();
    }

    @Override
    public void readFromNbt(final CompoundTag tag) {
    }

    @Override
    public void writeToNbt(final CompoundTag tag) {
    }

    private void promote(final BattleHandle handle, final PendingBattle battle) {
        final ClientBattleImpl battleImpl = new ClientBattleImpl(handle, battle.getBounds(), true, clientWorld);
        for (final BattleAction action : battle.getLog().actions) {
            battleImpl.pushAction(action);
        }
        addBattle(handle, battleImpl);
    }

    @Override
    public void tick() {
        lock.lock();
        final Iterator<Map.Entry<BattleHandle, PendingBattle>> iter = pendingBattles.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<BattleHandle, PendingBattle> entry = iter.next();
            final PendingBattle battle = entry.getValue();
            final Set<BattleEntity> entities = new ReferenceOpenHashSet<>();
            final IntIterator iterator = battle.entityIds.iterator();
            while (iterator.hasNext()) {
                final int id = iterator.nextInt();
                final Entity entity = clientWorld.getEntityById(id);
                if (entity != null && !entity.isRemoved() && entity instanceof BattleEntity) {
                    entities.add((BattleEntity) entity);
                } else {
                    break;
                }
            }
            if (entities.size() == battle.entityIds.size()) {
                promote(entry.getKey(), battle);
                iter.remove();
            }
        }
        for (final ClientBattleImpl battle : activeBattles.values()) {
            battle.tick();
        }
        lock.unlock();
    }

    public void demote(final BattleHandle battleHandle) {
        final ClientBattleImpl battle = activeBattles.remove(battleHandle);
        if (battle != null) {
            final PendingBattle pendingBattle = new PendingBattle(battle.getBounds());
            for (int i = 0; i < battle.getLog().size(); i++) {
                pendingBattle.pushAction(battle.getLog().getAction(i));
            }
            pendingBattles.put(battleHandle, pendingBattle);
        }
    }

    public void forEach(final Consumer<Either<ClientBattleImpl, PendingBattle>> consumer) {
        for (final ClientBattleImpl battle : activeBattles.values()) {
            consumer.accept(Either.left(battle));
        }
        for (final PendingBattle battle : pendingBattles.values()) {
            consumer.accept(Either.right(battle));
        }
    }

    public static class PendingBattle {
        private final IntSet entityIds;
        private final BattleBounds bounds;
        private final PendingBattleLog log;

        private PendingBattle(final BattleBounds bounds) {
            this.bounds = bounds;
            entityIds = new IntOpenHashSet();
            log = new PendingBattleLog(this);
        }

        private void apply(final BattleAction action) {
            entityIds.addAll(action.getAddedEntities());
            entityIds.removeAll(action.getRemovedEntities());
        }

        public void pushAction(final BattleAction action) {
            log.push(action);
        }

        public PendingBattleLog getLog() {
            return log;
        }

        public BattleBounds getBounds() {
            return bounds;
        }
    }

    private static class PendingBattleLog implements BattleLog {
        private final List<BattleAction> actions = new ObjectArrayList<>();
        private final PendingBattle battle;

        private PendingBattleLog(final PendingBattle battle) {
            this.battle = battle;
        }

        @Override
        public void push(final BattleAction action) {
            actions.add(action);
            battle.apply(action);
        }

        @Override
        public int size() {
            return actions.size();
        }

        @Override
        public BattleAction getAction(final int index) {
            return actions.get(index);
        }
    }
}
