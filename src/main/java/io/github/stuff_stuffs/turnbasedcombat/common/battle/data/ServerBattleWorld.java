package io.github.stuff_stuffs.turnbasedcombat.common.battle.data;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipant;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Team;
import io.github.stuff_stuffs.turnbasedcombat.common.network.BattleUpdateSender;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

public final class ServerBattleWorld implements BattleWorld {
    private final MutableInt nextBattleId;
    private final MutableInt nextParticipantId;
    private final Int2ReferenceMap<Battle> battles;

    private ServerBattleWorld(final int nextBattleId, final int nextParticipantId, final Int2ReferenceMap<Battle> battles) {
        this.nextBattleId = new MutableInt(nextBattleId);
        this.nextParticipantId = new MutableInt(nextParticipantId);
        this.battles = battles;
    }

    public ServerBattleWorld() {
        nextBattleId = new MutableInt();
        nextParticipantId = new MutableInt();
        battles = new Int2ReferenceOpenHashMap<>();
    }

    @Override
    public @Nullable Battle getBattle(final BattleHandle handle) {
        return battles.get(handle.id);
    }

    @Override
    public BattleParticipant create(final Text name, final Team team) {
        return new BattleParticipant(name, nextParticipantId.getAndIncrement(), team);
    }

    public void updateClient(final ServerPlayerEntity entity, final BattleHandle handle, final int timelineSize, final boolean fresh) {
        final Battle battle = battles.get(handle.id);
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
        nbt.put("battles", battles);
        nbt.putInt("nextBattleId", nextBattleId.getValue());
        nbt.putInt("nextParticipantId", nextParticipantId.getValue());
        return null;
    }

    public static ServerBattleWorld fromNbt(final NbtCompound nbt) {
        if (nbt.contains("battles")) {
            try {
                final int nextBattleId = nbt.getInt("nextBattleId");
                final int nextParticipantId = nbt.getInt("nextParticipantId");
                final NbtList battles = nbt.getList("battles", 10);
                final Int2ReferenceMap<Battle> battlesDecoded = new Int2ReferenceOpenHashMap<>(battles.size());
                for (final NbtElement element : battles) {
                    final Battle battle = Battle.CODEC.decode(NbtOps.INSTANCE, element).getOrThrow(false, s -> {
                        throw new RuntimeException(s);
                    }).getFirst();
                    battlesDecoded.put(battle.getBattleId(), battle);
                }
                return new ServerBattleWorld(nextBattleId, nextParticipantId, battlesDecoded);
            } catch (final Exception e) {
                return new ServerBattleWorld();
            }
        }
        return new ServerBattleWorld();
    }
}
