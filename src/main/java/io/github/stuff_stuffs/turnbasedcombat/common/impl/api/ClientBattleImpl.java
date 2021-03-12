package io.github.stuff_stuffs.turnbasedcombat.common.impl.api;

import io.github.stuff_stuffs.turnbasedcombat.common.api.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleHandle;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.Set;

public class ClientBattleImpl implements Battle {
    private final BattleHandle handle;
    private final int playerId;
    private final IntSet ids;
    private final World world;
    private ClientBattleDelegate delegate;

    public ClientBattleImpl(final BattleHandle handle, final int playerId, final IntSet ids, final World world) {
        this.handle = handle;
        this.playerId = playerId;
        this.ids = ids;
        this.world = world;
    }

    @Override
    public boolean remove(BattleEntity battleEntity) {
        check();
        return delegate.remove(battleEntity);
    }

    @Override
    public Set<BattleEntity> getAllies(BattleEntity battleEntity) {
        check();
        return delegate.getAllies(battleEntity);
    }

    @Override
    public Set<BattleEntity> getEnemies(BattleEntity battleEntity) {
        check();
        return delegate.getEnemies(battleEntity);
    }

    @Override
    public boolean contains(BattleEntity battleEntity) {
        check();
        return delegate.contains(battleEntity);
    }

    @Override
    public void addParticipant(BattleEntity battleEntity) {
        check();
        delegate.addParticipant(battleEntity);
    }

    @Override
    public boolean isActive() {
        check();
        return delegate.isActive();
    }

    @Override
    public CompoundTag toNbt() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlayerEntity getPlayer() {
        check();
        return delegate.getPlayer();
    }

    @Override
    public void end(EndingReason reason) {
        check();
        delegate.end(reason);
    }

    @Override
    public EndingReason getEndingReason() {
        check();
        return delegate.getEndingReason();
    }

    @Override
    public PacketByteBuf toBuf() {
        throw new UnsupportedOperationException();
    }

    //TODO replace this with something not horrible
    private void check() {
        if(delegate==null||delegate.isInvalid()) {
            throw new NullPointerException();
        }
    }

    @Override
    public void tick() {
        if (delegate == null || delegate.isInvalid()) {
            delegate = null;
            final Set<BattleEntity> entities = new ReferenceOpenHashSet<>(ids.size());
            boolean valid = true;
            final IntIterator iterator = ids.iterator();
            while (iterator.hasNext()) {
                final Entity entity = world.getEntityById(iterator.nextInt());
                if (entity != null) {
                    entities.add((BattleEntity) entity);
                } else {
                    valid = false;
                    break;
                }
            }
            final Entity maybePlayer = world.getEntityById(playerId);
            final PlayerEntity playerEntity;
            if (maybePlayer instanceof PlayerEntity) {
                playerEntity = (PlayerEntity) maybePlayer;
            } else {
                playerEntity = null;
            }
            valid &= (playerEntity != null);
            if (valid) {
                delegate = new ClientBattleDelegate(handle, playerEntity, entities, true, world);
            }
        }
    }

    private static class ClientBattleDelegate extends AbstractBattleImpl {
        private final World world;
        private boolean invalid = false;

        public ClientBattleDelegate(final BattleHandle handle, final PlayerEntity playerEntity, final Set<BattleEntity> participants, final boolean active, final World world) {
            super(handle, playerEntity, participants, active);
            this.world = world;
        }

        @Override
        public CompoundTag toNbt() {
            throw new UnsupportedOperationException();
        }

        @Override
        public PacketByteBuf toBuf() {
            throw new UnsupportedOperationException();
        }

        public boolean isInvalid() {
            return invalid;
        }

        @Override
        public void tick() {
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
            for (final BattleEntity participant : participants) {
                final Entity entity = world.getEntityById(((Entity) participant).getId());
                if (entity == null) {
                    invalid = true;
                    break;
                }
            }
        }
    }
}
