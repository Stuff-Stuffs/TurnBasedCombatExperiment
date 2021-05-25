package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Team;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.effect.EntityEffectCollection;
import io.github.stuff_stuffs.turnbasedcombat.common.util.CodecUtil;

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
                            ops.createInt(input.health)
                    ).add(
                            "effects",
                            EntityEffectCollection.CODEC.encodeStart(ops, input.effects)
                    ).build(ops.empty());
        }

        @Override
        public <T> DataResult<Pair<EntityState, T>> decode(final DynamicOps<T> ops, final T input) {
            final MapLike<T> map = ops.getMap(input).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final SkillInfo info = SkillInfo.CODEC.decode(ops, map.get("info")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            }).getFirst();
            final UUID uuid = CodecUtil.UUID_CODEC.decode(ops, map.get("uuid")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            }).getFirst();
            final Team team = Team.CODEC.decode(ops, map.get("team")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            }).getFirst();
            final int health = ops.getNumberValue(map.get("health")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            }).intValue();
            final EntityEffectCollection entityEffects = EntityEffectCollection.CODEC.decode(ops, map.get("effects")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            }).getFirst();
            return DataResult.success(Pair.of(new EntityState(info, uuid, team, health, entityEffects), ops.empty()));
        }
    };
    private final SkillInfo info;
    private final UUID uuid;
    private final Team team;
    private final EntityEffectCollection effects;
    private int health;

    private EntityState(final SkillInfo info, final UUID uuid, final Team team, final int health, final EntityEffectCollection effects) {
        this.info = info;
        this.health = health;
        this.uuid = uuid;
        this.team = team;
        this.effects = effects;
    }

    public EntityState(final SkillInfo info, final UUID uuid, final Team team) {
        this.info = info;
        health = info.health();
        effects = new EntityEffectCollection();
        this.uuid = uuid;
        this.team = team;
    }

    public void heal(final int amount) {
        health = Math.min(health + amount, getMaxHealth());
    }

    public void damage(final int amount) {
        health = Math.max(health - amount, 0);
    }

    @Override
    public int getHealth() {
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

    public void tick(final BattleStateView battleState) {
        effects.tick(this, battleState);
    }
}
