package io.github.stuff_stuffs.turnbasedcombat.common.api;

import io.github.stuff_stuffs.turnbasedcombat.common.impl.api.ServerBattleImpl;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;

import java.util.Set;

public interface Battle {
    boolean remove(BattleEntity battleEntity);

    Set<BattleEntity> getAllies(BattleEntity battleEntity);

    Set<BattleEntity> getEnemies(BattleEntity battleEntity);

    boolean contains(BattleEntity battleEntity);

    void addParticipant(BattleEntity battleEntity);

    boolean isActive();

    CompoundTag toNbt();

    PlayerEntity getPlayer();

    void end(EndingReason reason);

    EndingReason getEndingReason();

    PacketByteBuf toBuf();

    static ServerBattleImpl fromNbt(BattleHandle handle, final World world, final PlayerEntity playerEntity, final CompoundTag tag) {
        final ListTag participants = tag.getList("participants", NbtType.COMPOUND);
        final Set<BattleEntity> battleEntities = new ObjectOpenHashSet<>(participants.size());
        for (final Tag participant : participants) {
            final CompoundTag participantTag = (CompoundTag) participant;
            final Entity entity = EntityType.loadEntityWithPassengers(participantTag, world, passenger -> {
                battleEntities.add((BattleEntity) passenger);
                return passenger;
            });
            if (entity != null && world.spawnEntity(entity)) {
                battleEntities.add((BattleEntity) entity);
            }
        }
        return new ServerBattleImpl(handle, playerEntity, battleEntities, tag.getBoolean("active"));
    }

    void tick();

    enum EndingReason {
        COMPLETED,
        PLAYER_LEFT
    }
}
