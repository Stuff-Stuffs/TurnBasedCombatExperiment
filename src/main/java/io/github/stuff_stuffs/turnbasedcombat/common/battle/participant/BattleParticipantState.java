package io.github.stuff_stuffs.turnbasedcombat.common.battle.participant;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Team;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.damage.BattleDamagePacket;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.EventHolder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.EventKey;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.EventMap;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.MutableEventHolder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.participant.PostDamageEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.participant.PostEquipmentChangeEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.participant.PreDamageEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.participant.PreEquipmentChangeEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.equipment.BattleEquipment;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.equipment.BattleEquipmentState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.inventory.BattleParticipantInventory;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.inventory.BattleParticipantInventoryHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.inventory.BattleParticipantItemStack;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.stats.BattleParticipantStat;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.stats.BattleParticipantStatModifier;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.stats.BattleParticipantStatModifiers;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.stats.BattleParticipantStats;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.BattleEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.stream.StreamSupport;

public final class BattleParticipantState implements BattleParticipantStateView {
    public static final Codec<BattleParticipantState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BattleParticipantHandle.CODEC.fieldOf("handle").forGetter(state -> state.handle),
            Team.CODEC.fieldOf("team").forGetter(state -> state.team),
            BattleEquipmentState.CODEC.fieldOf("equipment").forGetter(state -> state.equipmentState),
            BattleParticipantInventory.CODEC.fieldOf("inventory").forGetter(state -> state.inventory),
            BattleParticipantStats.CODEC.fieldOf("stats").forGetter(state -> state.stats),
            Codec.DOUBLE.fieldOf("health").forGetter(state -> state.health),
            BlockPos.CODEC.fieldOf("pos").forGetter(state -> state.pos)
    ).apply(instance, BattleParticipantState::new));
    private final EventMap eventMap;
    private final BattleParticipantHandle handle;
    private final Team team;
    private final BattleEquipmentState equipmentState;
    private final BattleParticipantInventory inventory;
    private final BattleParticipantStats stats;
    private double health;
    private BlockPos pos;
    private BattleState battleState;

    private BattleParticipantState(final BattleParticipantHandle handle, final Team team, final BattleEquipmentState equipmentState, final BattleParticipantInventory inventory, final BattleParticipantStats stats, final double health, final BlockPos pos) {
        this.handle = handle;
        this.team = team;
        eventMap = new EventMap();
        registerEvents();
        this.equipmentState = equipmentState;
        this.inventory = inventory;
        this.stats = stats;
        this.health = health;
        this.pos = pos;
    }


    public BattleParticipantState(final BattleParticipantHandle handle, final BattleEntity entity) {
        this.handle = handle;
        pos = ((Entity) entity).getBlockPos();
        team = entity.getTeam();
        eventMap = new EventMap();
        registerEvents();
        equipmentState = new BattleEquipmentState(entity);
        inventory = new BattleParticipantInventory(entity);
        stats = new BattleParticipantStats(entity);
        health = entity.tbcex_getCurrentHealth();
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
        equipmentState.initEvents(this);
    }

    @Override
    public BattleState getBattleState() {
        return battleState;
    }

    public <T, V> MutableEventHolder<T, V> getEventMut(final EventKey<T, V> key) {
        return eventMap.getMut(key);
    }

    @Override
    public <T, V> EventHolder<T, V> getEvent(final EventKey<T, V> key) {
        return eventMap.get(key);
    }

    public boolean equip(final BattleEquipmentSlot slot, final BattleEquipment equipment) {
        return equipmentState.equip(this, slot, equipment);
    }

    @Override
    public Team getTeam() {
        return team;
    }

    @Override
    public BattleParticipantHandle getHandle() {
        return handle;
    }

    @Override
    public @Nullable BattleParticipantItemStack getItemStack(final BattleParticipantInventoryHandle handle) {
        if (handle.handle().equals(this.handle)) {
            return inventory.get(handle.id());
        } else {
            throw new RuntimeException();
        }
    }

    public BattleParticipantInventoryHandle giveItems(final BattleParticipantItemStack stack) {
        return new BattleParticipantInventoryHandle(handle, inventory.give(stack));
    }

    public int takeItems(final BattleParticipantInventoryHandle handle, final int amount) {
        if (handle.handle().equals(this.handle)) {
            return inventory.take(handle.id(), amount);
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public Iterator<Pair<BattleParticipantItemStack, BattleParticipantInventoryHandle>> getInventoryIterator() {
        return StreamSupport.stream(inventory.spliterator(), false).map(entry -> Pair.of(entry.getValue(), new BattleParticipantInventoryHandle(handle, entry.getIntKey()))).iterator();
    }

    public BattleParticipantStatModifiers.Handle addStatModifier(final BattleParticipantStat stat, final BattleParticipantStatModifier modifier) {
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
        return stats.calculate(stat, battleState, this);
    }

    public void setPos(final BlockPos pos) {
        this.pos = battleState.getBounds().getNearest(pos);
    }

    public @Nullable BattleDamagePacket damage(final BattleDamagePacket packet) {
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
}
