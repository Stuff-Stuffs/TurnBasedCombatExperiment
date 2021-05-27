package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Team;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.damage.DamagePacket;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.effect.EntityEffect;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.effect.EntityEffectCollection;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.effect.EntityEffectRegistry;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment.BattleEquipment;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment.BattleEquipmentState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment.BattleEquipmentType;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.stat.EntityStatType;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.stat.EntityStats;
import io.github.stuff_stuffs.turnbasedcombat.common.util.CodecUtil;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

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
                            "stats",
                            EntityStats.CODEC.encodeStart(ops, input.stats)
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
            EntityStats stats = EntityStats.CODEC.parse(ops, map.get("stats")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            return DataResult.success(Pair.of(new EntityState(info, uuid, team, health, entityEffects, equipmentState, stats), ops.empty()));
        }
    };
    private final SkillInfo info;
    private final UUID uuid;
    private final Team team;
    private final EntityEffectCollection effects;
    private final BattleEquipmentState equipmentState;
    private final EntityStats stats;
    private double health;

    private EntityState(final SkillInfo info, final UUID uuid, final Team team, final double health, final EntityEffectCollection effects, final BattleEquipmentState equipmentState, EntityStats stats) {
        this.info = info;
        this.health = health;
        this.uuid = uuid;
        this.team = team;
        this.effects = effects;
        this.equipmentState = equipmentState;
        this.stats = stats;
    }

    public EntityState(final BattleEntity entity) {
        info = entity.getSkillInfo();
        health = info.health();
        effects = new EntityEffectCollection();
        uuid = ((Entity) entity).getUuid();
        team = entity.getTeam();
        stats = new EntityStats();
        equipmentState = new BattleEquipmentState(entity, this);
    }

    public void heal(final int amount) {
        health = Math.min(health + amount, getMaxHealth());
    }

    public void damage(final DamagePacket packet) {
        final DamagePacket screenedDamage = packet.screen(stats.get(EntityStatType.RESISTANCES_STAT));
        health = (int) Math.round(Math.max(health - screenedDamage.amount(), 0));
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
    public int getMaxHealth() {
        return info.maxHealth();
    }

    @Override
    public UUID getId() {
        return uuid;
    }

    @Override
    public Team getTeam() {
        return team;
    }

    public void addEffect(final EntityEffect entityEffect) {
        effects.add(entityEffect);
    }

    public void addAllEffects(EntityEffectCollection effects) {
        this.effects.addAll(effects);
    }

    public void clearEffect(final EntityEffectRegistry.Type<?> type) {
        effects.clear(type);
    }

    public void equip(final BattleEquipment equipment) {
        equipmentState.put(equipment, this);
    }

    public void unEquip(final BattleEquipmentType type) {
        equipmentState.remove(type, this);
    }

    public @Nullable BattleEquipment getEquiped(final BattleEquipmentType type) {
        return equipmentState.get(type);
    }

    public void tick(final BattleStateView view) {
        effects.tick(this, view);
    }

    public void tickStats(BattleStateView view) {
        health = Math.min(health, stats.get(EntityStatType.MAX_HEALTH_STAT));
    }
}
