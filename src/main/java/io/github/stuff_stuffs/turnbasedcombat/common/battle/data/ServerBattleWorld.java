package io.github.stuff_stuffs.turnbasedcombat.common.battle.data;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.*;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.JoinBattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.BattleEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.turn.SimpleTurnChooser;
import io.github.stuff_stuffs.turnbasedcombat.common.network.BattleUpdateSender;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class ServerBattleWorld implements BattleWorld {
    private final MutableInt nextBattleId;
    private final Int2ReferenceMap<Battle> battles;
    //TODO move to on disk storage
    private final Int2ReferenceMap<Battle> endedBattles;

    private ServerBattleWorld(final int nextBattleId, final Int2ReferenceMap<Battle> battles) {
        this.nextBattleId = new MutableInt(nextBattleId);
        this.battles = new Int2ReferenceOpenHashMap<>();
        endedBattles = new Int2ReferenceOpenHashMap<>();
        for (final Battle battle : battles.values()) {
            if (battle.getStateView().isBattleEnded()) {
                endedBattles.put(battle.getBattleId(), battle);
            } else {
                this.battles.put(battle.getBattleId(), battle);
            }
        }
    }

    public ServerBattleWorld() {
        nextBattleId = new MutableInt();
        battles = new Int2ReferenceOpenHashMap<>();
        endedBattles = new Int2ReferenceOpenHashMap<>();
    }

    @Override
    public @Nullable Battle getBattle(final BattleHandle handle) {
        final Battle battle = battles.get(handle.id());
        if (battle == null) {
            return endedBattles.get(handle.id());
        }
        return battle;
    }

    @Override
    public @Nullable Battle getBattle(final BattleEntity entity) {
        final UUID id = ((Entity) entity).getUuid();
        for (final Battle battle : battles.values()) {
            if (battle.getStateView().contains(id)) {
                return battle;
            }
        }
        return null;
    }

    @Override
    public void join(final BattleEntity entity, final BattleHandle handle) {
        if (getBattle(entity) != null) {
            throw new RuntimeException("Entity already in battle");
        } else {
            final BattleParticipant participant = new BattleParticipant(entity.getBattleName(), ((Entity) entity).getUuid(), entity.getTeam(), entity.getSkillInfo());
            Battle battle = getBattle(handle);
            if (battle == null) {
                battle = new Battle(nextBattleId.getAndIncrement(), new SimpleTurnChooser(), new BattleTimeline());
                battles.put(battle.getBattleId(), battle);
            } else if (battle.getStateView().isBattleEnded()) {
                throw new RuntimeException();
            }
            battle.push(new JoinBattleAction(BattleParticipantHandle.UNIVERSAL.apply(battle.getBattleId()), participant));
        }
    }

    @Override
    public BattleHandle create() {
        final Battle battle = new Battle(nextBattleId.getAndIncrement(), new SimpleTurnChooser(), new BattleTimeline());
        battles.put(battle.getBattleId(), battle);
        return new BattleHandle(battle.getBattleId());
    }

    public void updateClient(final ServerPlayerEntity entity, final BattleHandle handle, final int timelineSize, final boolean fresh) {
        final Battle battle = getBattle(handle);
        if (battle != null) {
            BattleUpdateSender.send(entity, handle, battle.getTurnChooser(), timelineSize, battle.getTimeline(), fresh);
        }
    }

    public NbtCompound writeNbt(final NbtCompound nbt) {
        final NbtList battles = new NbtList();
        for (final Int2ReferenceMap.Entry<Battle> entry : this.battles.int2ReferenceEntrySet()) {
            battles.add(Battle.CODEC.encode(entry.getValue(), NbtOps.INSTANCE, NbtOps.INSTANCE.empty()).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            }));
        }
        for (final Int2ReferenceMap.Entry<Battle> entry : endedBattles.int2ReferenceEntrySet()) {
            battles.add(Battle.CODEC.encode(entry.getValue(), NbtOps.INSTANCE, NbtOps.INSTANCE.empty()).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            }));
        }
        nbt.put("battles", battles);
        nbt.putInt("nextBattleId", nextBattleId.getValue());
        return nbt;
    }

    public static ServerBattleWorld fromNbt(final NbtCompound nbt) {
        if (nbt.contains("battles")) {
            try {
                final int nextBattleId = nbt.getInt("nextBattleId");
                final NbtList battles = nbt.getList("battles", 10);
                final Int2ReferenceMap<Battle> battlesDecoded = new Int2ReferenceOpenHashMap<>(battles.size());
                for (final NbtElement element : battles) {
                    final Battle battle = Battle.CODEC.decode(NbtOps.INSTANCE, element).getOrThrow(false, s -> {
                        throw new RuntimeException(s);
                    }).getFirst();
                    battlesDecoded.put(battle.getBattleId(), battle);
                }
                return new ServerBattleWorld(nextBattleId, battlesDecoded);
            } catch (final Exception e) {
                e.printStackTrace();
                return new ServerBattleWorld();
            }
        }
        return new ServerBattleWorld();
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
