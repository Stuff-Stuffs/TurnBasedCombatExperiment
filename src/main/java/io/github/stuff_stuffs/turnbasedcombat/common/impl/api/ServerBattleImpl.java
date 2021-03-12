package io.github.stuff_stuffs.turnbasedcombat.common.impl.api;

import io.github.stuff_stuffs.turnbasedcombat.common.api.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.component.BattlePlayerComponent;
import io.github.stuff_stuffs.turnbasedcombat.common.component.Components;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.component.BattlePlayerComponentImpl;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;

import java.util.Iterator;
import java.util.Set;

public class ServerBattleImpl extends AbstractBattleImpl implements Battle {
    public ServerBattleImpl(BattleHandle handle, final PlayerEntity playerEntity, final Set<BattleEntity> participants, final boolean active) {
        super(handle, playerEntity, participants, active);
        final BattlePlayerComponentImpl battlePlayer = (BattlePlayerComponentImpl) Components.BATTLE_PLAYER_COMPONENT_KEY.get(playerEntity);
        battlePlayer.setBattleHandle(handle);
    }

    @Override
    public CompoundTag toNbt() {
        final CompoundTag tag = new CompoundTag();
        tag.putBoolean("active", isActive());
        final ListTag entities = new ListTag();
        for (final BattleEntity participant : participants) {
            final Entity entity = (Entity) participant;
            final CompoundTag entityTag = new CompoundTag();
            if (entity.saveToTag(entityTag)) {
                entities.add(entityTag);
            }
        }
        tag.put("participants", entities);
        return tag;
    }

    @Override
    public PacketByteBuf toBuf() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(playerEntity.getId());
        buf.writeVarInt(participants.size());
        for (BattleEntity participant : participants) {
            buf.writeVarInt(((Entity)participant).getId());
        }
        handle.toBuf(buf);
        return buf;
    }

    @Override
    public void tick() {
        if (isEnded()) {
            end(EndingReason.COMPLETED);
        } else {
            final Iterator<BattleEntity> iterator = this.participants.iterator();
            while (iterator.hasNext()) {
                final BattleEntity next = iterator.next();
                if(((Entity)next).isRemoved()) {
                    iterator.remove();
                    final Set<BattleEntity> team = teamMap.get(next.getTeam());
                    if(team!=null) {
                        team.remove(next);
                    }
                    if(team!=null&&team.size()==0) {
                        teamMap.remove(next.getTeam());
                    }
                }
            }
        }
    }

    public boolean shouldSuspend() {
        return playerEntity.isRemoved();
    }
}
