package io.github.stuff_stuffs.tbcexcore.common.battle.participant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleState;
import io.github.stuff_stuffs.tbcexcore.common.battle.Team;
import io.github.stuff_stuffs.tbcexcore.common.battle.damage.BattleDamagePacket;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.EventHolder;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.EventKey;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.EventMap;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.MutableEventHolder;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.participant.PostDamageEvent;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.participant.PostEquipmentChangeEvent;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.participant.PreDamageEvent;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.participant.PreEquipmentChangeEvent;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantInventory;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantInventoryHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemStack;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipment;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStatModifier;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStatModifiers;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStats;
import io.github.stuff_stuffs.tbcexcore.common.entity.BattleEntity;
import io.github.stuff_stuffs.tbcexutil.common.HorizontalDirection;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.stream.StreamSupport;

public final class BattleParticipantState implements BattleParticipantStateView {
    public static final Codec<BattleParticipantState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BattleParticipantHandle.CODEC.fieldOf("handle").forGetter(state -> state.handle),
            Team.CODEC.fieldOf("team").forGetter(state -> state.team),
            BattleParticipantInventory.CODEC.fieldOf("inventory").forGetter(state -> state.inventory),
            BattleParticipantStats.CODEC.fieldOf("stats").forGetter(state -> state.stats),
            BattleParticipantBounds.CODEC.fieldOf("bounds").forGetter(state -> state.bounds),
            Codec.DOUBLE.fieldOf("health").forGetter(state -> state.health),
            BlockPos.CODEC.fieldOf("pos").forGetter(state -> state.pos),
            HorizontalDirection.CODEC.fieldOf("facing").forGetter(state -> state.facing)
    ).apply(instance, BattleParticipantState::new));
    private final EventMap eventMap;
    private final BattleParticipantHandle handle;
    private final Team team;
    private final BattleParticipantInventory inventory;
    private final BattleParticipantStats stats;
    private BattleParticipantBounds bounds;
    private boolean valid = false;
    private double health;
    private HorizontalDirection facing;
    private BlockPos pos;
    private BattleState battleState;

    private BattleParticipantState(final BattleParticipantHandle handle, final Team team, final BattleParticipantInventory inventory, final BattleParticipantStats stats, final BattleParticipantBounds bounds, final double health, final BlockPos pos, final HorizontalDirection facing) {
        this.handle = handle;
        this.team = team;
        this.bounds = bounds;
        eventMap = new EventMap();
        registerEvents();
        this.inventory = inventory;
        this.stats = stats;
        this.health = health;
        this.pos = pos;
        this.facing = facing;
    }


    public BattleParticipantState(final BattleParticipantHandle handle, final BattleEntity entity) {
        this.handle = handle;
        pos = ((Entity) entity).getBlockPos();
        team = entity.getTeam();
        eventMap = new EventMap();
        registerEvents();
        inventory = new BattleParticipantInventory(entity);
        stats = new BattleParticipantStats(entity);
        bounds = entity.getBounds();
        health = entity.tbcex_getCurrentHealth();
        Direction bestDir = Direction.NORTH;
        double best = Double.NEGATIVE_INFINITY;
        final Vec3d facingVec = ((Entity) entity).getRotationVec(1);
        for (final Direction direction : Direction.values()) {
            if (direction.getAxis() != Direction.Axis.Y) {
                final double cur = new Vec3d(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ()).dotProduct(facingVec);
                if (cur > best) {
                    bestDir = direction;
                    best = cur;
                }
            }
        }
        facing = HorizontalDirection.fromDirection(bestDir);
    }

    private void registerEvents() {
        eventMap.register(PRE_EQUIPMENT_CHANGE_EVENT, new MutableEventHolder.BasicEventHolder<>(PRE_EQUIPMENT_CHANGE_EVENT, view -> (state, slot, oldEquipment, newEquipment) -> {
            view.onEquipmentChange(state, slot, oldEquipment, newEquipment);
            return false;
        }, events -> (state, slot, oldEquipment, newEquipment) -> {
            boolean canceled = false;
            for (final PreEquipmentChangeEvent.Mut event : events) {
                canceled |= event.onEquipmentChange(state, slot, oldEquipment, newEquipment);
            }
            return canceled;
        }));
        eventMap.register(POST_EQUIPMENT_CHANGE_EVENT, new MutableEventHolder.BasicEventHolder<>(POST_EQUIPMENT_CHANGE_EVENT, view -> view::onEquipmentChange, events -> (state, slot, oldEquipment, newEquipment) -> {
            for (final PostEquipmentChangeEvent.Mut event : events) {
                event.onEquipmentChange(state, slot, oldEquipment, newEquipment);
            }
        }));
        eventMap.register(PRE_DAMAGE_EVENT, new MutableEventHolder.BasicEventHolder<>(PRE_DAMAGE_EVENT, view -> (state, damagePacket) -> {
            view.onDamage(state, damagePacket);
            return damagePacket;
        }, events -> (state, damagePacket) -> {
            for (final PreDamageEvent.Mut event : events) {
                damagePacket = event.onDamage(state, damagePacket);
            }
            return damagePacket;
        }));
        eventMap.register(POST_DAMAGE_EVENT, new MutableEventHolder.BasicEventHolder<>(POST_DAMAGE_EVENT, view -> view::onDamage, events -> (state, damagePacket) -> {
            for (final PostDamageEvent.Mut event : events) {
                event.onDamage(state, damagePacket);
            }
        }));
    }

    public void setBattleState(final BattleState battleState) {
        if (this.battleState != null) {
            throw new RuntimeException("Tried to set battle of participant already in battle");
        }
        this.battleState = battleState;
        inventory.initEvents(this);
        valid = true;
    }

    @Override
    public BattleState getBattleState() {
        if (!valid) {
            throw new RuntimeException();
        }
        return battleState;
    }

    public <T, V> MutableEventHolder<T, V> getEventMut(final EventKey<T, V> key) {
        if (!valid) {
            throw new RuntimeException();
        }
        return eventMap.getMut(key);
    }

    @Override
    public <T, V> EventHolder<T, V> getEvent(final EventKey<T, V> key) {
        if (!valid) {
            throw new RuntimeException();
        }
        return eventMap.get(key);
    }

    public boolean equip(final BattleEquipmentSlot slot, final BattleParticipantItemStack equipment) {
        if (!valid) {
            throw new RuntimeException();
        }
        return inventory.equip(this, slot, equipment);
    }

    @Override
    public Team getTeam() {
        if (!valid) {
            throw new RuntimeException();
        }
        return team;
    }

    @Override
    public BattleParticipantHandle getHandle() {
        if (!valid) {
            throw new RuntimeException();
        }
        return handle;
    }

    @Override
    public @Nullable BattleParticipantItemStack getItemStack(final BattleParticipantInventoryHandle handle) {
        if (!valid) {
            throw new RuntimeException();
        }
        if (handle.handle().equals(this.handle)) {
            return inventory.get(handle.id());
        } else {
            throw new RuntimeException();
        }
    }

    public BattleParticipantInventoryHandle giveItems(final BattleParticipantItemStack stack) {
        if (!valid) {
            throw new RuntimeException();
        }
        return new BattleParticipantInventoryHandle(handle, inventory.give(stack));
    }

    public @Nullable BattleParticipantItemStack takeItems(final BattleParticipantInventoryHandle handle, final int amount) {
        if (!valid) {
            throw new RuntimeException();
        }
        if (handle.handle().equals(this.handle)) {
            return inventory.take(handle.id(), amount);
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public @Nullable BattleEquipment getEquipment(final BattleEquipmentSlot slot) {
        return inventory.getEquipment(slot);
    }

    @Override
    public @Nullable BattleParticipantItemStack getEquipmentStack(final BattleEquipmentSlot slot) {
        return inventory.getEquipmentStack(slot);
    }

    @Override
    public Iterator<BattleParticipantInventoryHandle> getInventoryIterator() {
        if (!valid) {
            throw new RuntimeException();
        }
        return StreamSupport.stream(inventory.spliterator(), false).map(entry -> new BattleParticipantInventoryHandle(handle, entry.getIntKey())).iterator();
    }

    public BattleParticipantStatModifiers.Handle addStatModifier(final BattleParticipantStat stat, final BattleParticipantStatModifier modifier) {
        if (!valid) {
            throw new RuntimeException();
        }
        final BattleParticipantStatModifiers.Handle handle = stats.modify(stat, modifier);
        final double maxHealth = stats.calculate(BattleParticipantStat.MAX_HEALTH_STAT, battleState, this);
        if (maxHealth < 0) {
            health = 0;
        } else {
            health = Math.min(health, maxHealth);
        }
        return handle;
    }

    @Override
    public double getStat(final BattleParticipantStat stat) {
        if (!valid) {
            throw new RuntimeException();
        }
        return stats.calculate(stat, battleState, this);
    }

    public void setPos(final BlockPos pos) {
        if (!valid) {
            throw new RuntimeException();
        }
        final BlockPos newPos = battleState.getBounds().getNearest(pos);
        bounds = bounds.offset(newPos.getX() - pos.getX(), newPos.getY() - pos.getY(), newPos.getZ() - pos.getZ());
        this.pos = newPos;
    }

    public @Nullable BattleDamagePacket damage(final BattleDamagePacket packet) {
        if (!valid) {
            throw new RuntimeException();
        }
        final BattleDamagePacket processed = getEvent(PRE_DAMAGE_EVENT).invoker().onDamage(this, packet);
        if (processed.getTotalDamage() > 0.0001) {
            health -= processed.getTotalDamage();
            health = Math.max(health, 0);
            getEvent(POST_DAMAGE_EVENT).invoker().onDamage(this, packet);
            return processed;
        }
        return null;
    }

    @Override
    public double getHealth() {
        return health;
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public HorizontalDirection getFacing() {
        return facing;
    }

    public void setFacing(final HorizontalDirection facing) {
        this.facing = facing;
    }

    @Override
    public BattleParticipantBounds getBounds() {
        return getBounds(facing);
    }

    @Override
    public BattleParticipantBounds getBounds(final HorizontalDirection facing) {
        return bounds.withRotation(facing);
    }

    public void leave() {
        if (!valid) {
            throw new RuntimeException();
        }
        valid = false;
        inventory.uninitEvents();
    }
}
