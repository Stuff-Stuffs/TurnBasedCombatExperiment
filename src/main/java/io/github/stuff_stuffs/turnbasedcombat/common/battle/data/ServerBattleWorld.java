package io.github.stuff_stuffs.turnbasedcombat.common.battle.data;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.*;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.JoinBattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.AdvanceTurnEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.BattleEndedEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.EntityLeaveEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.turn.SimpleTurnChooser;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.BattleEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.network.BattleUpdateSender;
import io.github.stuff_stuffs.turnbasedcombat.common.network.CurrentTurnSender;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public final class ServerBattleWorld implements BattleWorld {
    private final MutableInt nextBattleId;
    private final ServerWorld world;
    private final Object2ReferenceMap<BattleHandle, Battle> battles;
    //TODO move to on disk storage
    private final Object2ReferenceMap<BattleHandle, Battle> endedBattles;

    private ServerBattleWorld(final int nextBattleId, final Object2ReferenceMap<BattleHandle, Battle> battles, final ServerWorld world) {
        this.nextBattleId = new MutableInt(nextBattleId);
        this.world = world;
        this.battles = new Object2ReferenceOpenHashMap<>();
        endedBattles = new Object2ReferenceOpenHashMap<>();
        for (final Battle battle : battles.values()) {
            if (battle.getStateView().isBattleEnded()) {
                endedBattles.put(battle.getBattleId(), battle);
            } else {
                this.battles.put(battle.getBattleId(), battle);
                ((BattleState) battle.getStateView()).getEvent(EntityLeaveEvent.class).register((battleState, entityState) -> entityState.getWorldEntityInfo().spawnIntoWorld(world, entityState));
                ((BattleState) battle.getStateView()).getEvent(BattleEndedEvent.class).register(battleState -> {
                    for (final BattleParticipantHandle participant : battleState.getParticipants()) {
                        final EntityStateView entityStateView = battleState.getParticipant(participant);
                        if (entityStateView != null) {
                            entityStateView.getWorldEntityInfo().spawnIntoWorld(world, entityStateView);
                        } else {
                            throw new RuntimeException();
                        }
                    }
                });
                final UUID uuid = battle.getStateView().getCurrentTurn().getHandle().participantId();
                final Entity possiblePlayer = world.getEntity(uuid);
                if (possiblePlayer instanceof ServerPlayerEntity playerEntity) {
                    CurrentTurnSender.send(playerEntity, true);
                }
                ((BattleState) battle.getStateView()).getEvent(AdvanceTurnEvent.class).register((battleState, prev, current) -> {
                    Entity entity = world.getEntity(prev.participantId());
                    if (entity != null) {
                        if (entity instanceof ServerPlayerEntity playerEntity) {
                            CurrentTurnSender.send(playerEntity, false);
                        }
                    }
                    entity = world.getEntity(current.participantId());
                    if (entity != null) {
                        if (entity instanceof ServerPlayerEntity playerEntity) {
                            CurrentTurnSender.send(playerEntity, true);
                        }
                    }
                });
            }
        }
    }

    public ServerBattleWorld(final ServerWorld world) {
        this.world = world;
        nextBattleId = new MutableInt();
        battles = new Object2ReferenceOpenHashMap<>();
        endedBattles = new Object2ReferenceOpenHashMap<>();
    }

    @Override
    public @Nullable Battle getBattle(final BattleHandle handle) {
        final Battle battle = battles.get(handle);
        if (battle == null) {
            return endedBattles.get(handle);
        }
        return battle;
    }

    @Override
    public @Nullable Battle getBattle(final BattleEntity entity) {
        final UUID id = ((Entity) entity).getUuid();
        return getBattle(id);
    }

    public @Nullable Battle getBattle(final UUID entity) {
        for (final Battle battle : battles.values()) {
            if (battle.getStateView().contains(entity)) {
                return battle;
            }
        }
        return null;
    }

    @Override
    public void join(final BattleEntity battleEntity, final BattleHandle handle) {
        if (!(battleEntity instanceof Entity entity)) {
            throw new RuntimeException();
        }
        if (getBattle(battleEntity) != null) {
            throw new RuntimeException("Entity already in battle");
        } else {
            final EntityState participant = new EntityState(battleEntity);
            final Battle battle = getBattle(handle);
            if (battle == null || battle.getStateView().isBattleEnded()) {
                throw new RuntimeException();
            }
            battle.push(new JoinBattleAction(BattleParticipantHandle.UNIVERSAL.apply(battle.getBattleId()), participant));
            entity.discard();
        }
    }

    @Override
    public BattleHandle create() {
        final Battle battle = new Battle(new BattleHandle(nextBattleId.getAndIncrement()), new SimpleTurnChooser(), new BattleTimeline());
        battles.put(battle.getBattleId(), battle);
        ((BattleState) battle.getStateView()).getEvent(EntityLeaveEvent.class).register((battleState, entityState) -> entityState.getWorldEntityInfo().spawnIntoWorld(world, entityState));
        ((BattleState) battle.getStateView()).getEvent(BattleEndedEvent.class).register(battleState -> {
            for (final BattleParticipantHandle participant : battleState.getParticipants()) {
                final EntityStateView entityStateView = battleState.getParticipant(participant);
                if (entityStateView != null) {
                    entityStateView.getWorldEntityInfo().spawnIntoWorld(world, entityStateView);
                } else {
                    throw new RuntimeException();
                }
            }
        });
        ((BattleState) battle.getStateView()).getEvent(AdvanceTurnEvent.class).register((battleState, prev, current) -> {
            Entity entity = world.getEntity(prev.participantId());
            if (entity != null) {
                if (entity instanceof ServerPlayerEntity playerEntity) {
                    CurrentTurnSender.send(playerEntity, false);
                }
            }
            entity = world.getEntity(current.participantId());
            if (entity != null) {
                if (entity instanceof ServerPlayerEntity playerEntity) {
                    CurrentTurnSender.send(playerEntity, true);
                }
            }
        });
        return battle.getBattleId();
    }

    public void updateClient(final ServerPlayerEntity entity, final BattleHandle handle, final int timelineSize, final boolean fresh) {
        final Battle battle = getBattle(handle);
        if (battle != null) {
            BattleUpdateSender.send(entity, handle, battle.getTurnChooser(), timelineSize, battle.getTimeline(), fresh);
        }
    }

    public NbtCompound writeNbt(final NbtCompound nbt) {
        final NbtList battles = new NbtList();
        for (final Map.Entry<BattleHandle, Battle> entry : this.battles.entrySet()) {
            battles.add(Battle.CODEC.encode(entry.getValue(), NbtOps.INSTANCE, NbtOps.INSTANCE.empty()).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            }));
        }
        for (final Map.Entry<BattleHandle, Battle> entry : endedBattles.entrySet()) {
            battles.add(Battle.CODEC.encode(entry.getValue(), NbtOps.INSTANCE, NbtOps.INSTANCE.empty()).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            }));
        }
        nbt.put("battles", battles);
        nbt.putInt("nextBattleId", nextBattleId.getValue());
        return nbt;
    }

    public static ServerBattleWorld fromNbt(final NbtCompound nbt, final ServerWorld world) {
        if (nbt.contains("battles")) {
            try {
                final int nextBattleId = nbt.getInt("nextBattleId");
                final NbtList battles = nbt.getList("battles", 10);
                final Object2ReferenceMap<BattleHandle, Battle> battlesDecoded = new Object2ReferenceOpenHashMap<>(battles.size());
                for (final NbtElement element : battles) {
                    final Battle battle = Battle.CODEC.decode(NbtOps.INSTANCE, element).getOrThrow(false, s -> {
                        throw new RuntimeException(s);
                    }).getFirst();
                    battlesDecoded.put(battle.getBattleId(), battle);
                }
                return new ServerBattleWorld(nextBattleId, battlesDecoded, world);
            } catch (final Exception e) {
                e.printStackTrace();
                return new ServerBattleWorld(world);
            }
        }
        return new ServerBattleWorld(world);
    }

    public void tick() {
        for (final ObjectIterator<Battle> iterator = battles.values().iterator(); iterator.hasNext(); ) {
            final Battle battle = iterator.next();
            if (battle.getStateView().isBattleEnded()) {
                iterator.remove();
                endedBattles.put(battle.getBattleId(), battle);
            } else {
                battle.tick();
            }
        }
    }
}
