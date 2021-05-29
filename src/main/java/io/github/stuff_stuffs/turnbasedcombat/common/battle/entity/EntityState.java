package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Team;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.damage.DamagePacket;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.effect.EntityEffectCollection;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.effect.EntityEffectFactory;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.effect.EntityEffectType;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment.BattleEquipment;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment.BattleEquipmentState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment.BattleEquipmentType;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.inventory.EntityInventory;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.stat.EntityStatModifier;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.stat.EntityStatType;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.stat.EntityStats;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.EventHolder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.entity.PostEntityDamageEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.entity.PreEntityDamageEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.entity.effect.*;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.entity.equipment.PostEquipmentEquipEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.entity.equipment.PostEquipmentUnEquipEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.entity.equipment.PreEquipmentEquipEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.entity.equipment.PreEquipmentUnEquipEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.util.CodecUtil;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class EntityState implements EntityStateView {
    public static final Codec<EntityState> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<T> encode(final EntityState input, final DynamicOps<T> ops, final T prefix) {
            return ops.mapBuilder()
                    .add(
                            "info",
                            SkillInfo.CODEC.encodeStart(ops, input.info)
                    ).add(
                            "uuid",
                            CodecUtil.UUID_CODEC.encodeStart(ops, input.uuid)
                    ).add(
                            "team",
                            Team.CODEC.encodeStart(ops, input.team)
                    ).add(
                            "health",
                            ops.createDouble(input.health)
                    ).add(
                            "effects",
                            EntityEffectCollection.CODEC.encodeStart(ops, input.effects)
                    ).add(
                            "equipment",
                            BattleEquipmentState.CODEC.encodeStart(ops, input.equipmentState)
                    ).add(
                            "inventory",
                            EntityInventory.CODEC.encodeStart(ops, input.inventory)
                    ).build(ops.empty());
        }

        @Override
        public <T> DataResult<Pair<EntityState, T>> decode(final DynamicOps<T> ops, final T input) {
            final MapLike<T> map = ops.getMap(input).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final SkillInfo info = SkillInfo.CODEC.parse(ops, map.get("info")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final UUID uuid = CodecUtil.UUID_CODEC.parse(ops, map.get("uuid")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final Team team = Team.CODEC.parse(ops, map.get("team")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final double health = ops.getNumberValue(map.get("health")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            }).doubleValue();
            final EntityEffectCollection entityEffects = EntityEffectCollection.CODEC.parse(ops, map.get("effects")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final BattleEquipmentState equipmentState = BattleEquipmentState.CODEC.parse(ops, map.get("equipment")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final EntityInventory inventory = EntityInventory.CODEC.parse(ops, map.get("inventory")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            return DataResult.success(Pair.of(new EntityState(info, uuid, team, health, entityEffects, equipmentState, inventory), ops.empty()));
        }
    };
    private final SkillInfo info;
    private final UUID uuid;
    private final Team team;
    private final Map<Class<?>, EventHolder<?>> eventHolders;
    private final BattleEquipmentState equipmentState;
    private final EntityEffectCollection effects;
    private final EntityStats stats;
    private final EntityInventory inventory;
    private BattleState battle;
    private BattleParticipantHandle handle;
    private double health;

    private EntityState(final SkillInfo info, final UUID uuid, final Team team, final double health, final EntityEffectCollection effects, final BattleEquipmentState equipmentState, final EntityInventory inventory) {
        this.info = info;
        this.uuid = uuid;
        this.team = team;
        eventHolders = new Reference2ObjectOpenHashMap<>();
        this.equipmentState = equipmentState;
        this.effects = effects;
        stats = new EntityStats();
        this.inventory = inventory;
        this.health = health;
        populateEventHolders();
    }

    public EntityState(final BattleEntity entity) {
        info = entity.getSkillInfo();
        //TODO extract effects from BattleEntity
        effects = new EntityEffectCollection();
        uuid = ((Entity) entity).getUuid();
        team = entity.getTeam();
        eventHolders = new Reference2ObjectOpenHashMap<>();
        equipmentState = new BattleEquipmentState(entity, this);
        stats = new EntityStats();
        inventory = entity.getBattleInventory();
        health = info.health();
        populateEventHolders();
    }

    private void populateEventHolders() {
        putEvent(PreEquipmentEquipEvent.class, new EventHolder.BasicEventHolder<>(events -> (state, equipment) -> {
            boolean ret = false;
            for (final PreEquipmentEquipEvent event : events) {
                ret |= event.onEquip(state, equipment);
            }
            return ret;
        }));
        putEvent(PostEquipmentEquipEvent.class, new EventHolder.BasicEventHolder<>(events -> (state, equipment) -> {
            for (final PostEquipmentEquipEvent event : events) {
                event.onEquip(state, equipment);
            }
        }));
        putEvent(PreEquipmentUnEquipEvent.class, new EventHolder.BasicEventHolder<>(events -> (state, equipment) -> {
            boolean ret = false;
            for (final PreEquipmentUnEquipEvent event : events) {
                ret |= event.onUnEquip(state, equipment);
            }
            return ret;
        }));
        putEvent(PostEquipmentUnEquipEvent.class, new EventHolder.BasicEventHolder<>(events -> (state, equipment) -> {
            for (final PostEquipmentUnEquipEvent event : events) {
                event.onUnEquip(state, equipment);
            }
        }));
        putEvent(PreEntityAddEffect.class, new EventHolder.BasicEventHolder<>(events -> (state, effect) -> {
            boolean ret = false;
            for (final PreEntityAddEffect event : events) {
                ret |= event.onEntityAddEffect(state, effect);
            }
            return ret;
        }));
        putEvent(PostEntityAddEffect.class, new EventHolder.BasicEventHolder<>(events -> (state, effect) -> {
            for (final PostEntityAddEffect event : events) {
                event.onEntityAddEffect(state, effect);
            }
        }));
        putEvent(PreEntityRemoveEffect.class, new EventHolder.BasicEventHolder<>(events -> (state, effect) -> {
            boolean ret = false;
            for (final PreEntityRemoveEffect event : events) {
                ret |= event.onRemoveEffect(state, effect);
            }
            return ret;
        }));
        putEvent(PostEntityRemoveEffect.class, new EventHolder.BasicEventHolder<>(events -> (state, effect) -> {
            for (final PostEntityRemoveEffect event : events) {
                event.onRemoveEffect(state, effect);
            }
        }));
        putEvent(PreEntityCombineEffect.class, new EventHolder.BasicEventHolder<>(events -> (state, first, second) -> {
            boolean ret = false;
            for (final PreEntityCombineEffect event : events) {
                ret |= event.onCombineEffect(state, first, second);
            }
            return ret;
        }));
        putEvent(PostEntityCombineEffect.class, new EventHolder.BasicEventHolder<>(events -> (state, first, second, combined) -> {
            for (final PostEntityCombineEffect event : events) {
                event.onCombineEffect(state, first, second, combined);
            }
        }));
        putEvent(PreEntityDamageEvent.class, new EventHolder.SortedEventHolder<>(events -> new PreEntityDamageEvent() {
            @Override
            public @Nullable DamagePacket onEntityDamage(final EntityState state, DamagePacket damagePacket) {
                for (final PreEntityDamageEvent event : events) {
                    damagePacket = event.onEntityDamage(state, damagePacket);
                    if (damagePacket == null) {
                        return null;
                    }
                }
                return damagePacket;
            }

            @Override
            public int getPriority() {
                return 0;
            }
        }, Comparator.comparingInt(PreEntityDamageEvent::getPriority)));
        putEvent(PostEntityDamageEvent.class, new EventHolder.BasicEventHolder<>(events -> (state, damagePacket) -> {
            for (final PostEntityDamageEvent event : events) {
                event.onEntityDamage(state, damagePacket);
            }
        }));
    }

    private <T> void putEvent(final Class<T> clazz, final EventHolder<T> eventHolder) {
        eventHolders.put(clazz, eventHolder);
    }

    //TODO event holder view?
    public <T> EventHolder<T> getEvent(final Class<T> clazz) {
        return (EventHolder<T>) eventHolders.get(clazz);
    }

    public <T> T getStat(final EntityStatType<T> type) {
        return stats.modify(type, type.getValueOrDefault(info, this), this);
    }

    public <T> EntityStats.Handle addStatModifier(final EntityStatType<T> type, final EntityStatModifier<T> modifier) {
        return stats.addModifier(type, modifier);
    }

    @Override
    public BattleState getBattle() {
        return battle;
    }

    public void setBattle(final BattleState battle) {
        if (this.battle != null) {
            throw new RuntimeException();
        }
        this.battle = battle;
    }

    @Override
    public double getHealth() {
        return health;
    }

    @Override
    public int getLevel() {
        return info.level();
    }

    @Override
    public UUID getId() {
        return uuid;
    }

    @Override
    public Team getTeam() {
        return team;
    }

    @Override
    public BattleParticipantHandle getHandle() {
        return handle;
    }

    public void setHandle(final BattleParticipantHandle handle) {
        if (this.handle != null) {
            throw new RuntimeException();
        }
        this.handle = handle;
    }

    public void damage(final DamagePacket damage) {
        final DamagePacket packet = getEvent(PreEntityDamageEvent.class).invoker().onEntityDamage(this, damage);
        if (packet != null) {
            health = (health - packet.amount());
            getEvent(PostEntityDamageEvent.class).invoker().onEntityDamage(this, packet);
        }
    }

    @Override
    public EntityInventory getInventory() {
        return inventory;
    }

    public boolean addEffect(final EntityEffectFactory factory) {
        return effects.add(factory, this);
    }

    public void addAllEffects(final List<EntityEffectFactory> effects) {
        this.effects.addAll(effects, this);
    }

    public boolean clearEffect(final EntityEffectType type) {
        return effects.clear(type, this);
    }

    public boolean equip(final BattleEquipment equipment) {
        return equipmentState.put(equipment, this);
    }

    public boolean unEquip(final BattleEquipmentType type) {
        return equipmentState.remove(type, this);
    }

    public @Nullable BattleEquipment getEquiped(final BattleEquipmentType type) {
        return equipmentState.get(type);
    }

    public void initEvents() {
        for (final BattleEquipmentType type : BattleEquipmentType.REGISTRY) {
            final BattleEquipment equipment = equipmentState.get(type);
            if (equipment != null) {
                equipment.initEvents(this);
            }
        }
    }
}
