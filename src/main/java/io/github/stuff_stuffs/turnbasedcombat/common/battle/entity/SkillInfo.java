package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;

public final class SkillInfo {
    public static final Codec<SkillInfo> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<SkillInfo, T>> decode(final DynamicOps<T> ops, final T input) {
            final MapLike<T> mapLike = ops.getMap(input).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final int maxHealth = ops.getNumberValue(mapLike.get("maxHealth"), 20).intValue();
            final int health = ops.getNumberValue(mapLike.get("health"), 20).intValue();
            final int level = ops.getNumberValue(mapLike.get("level"), 0).intValue();
            return DataResult.success(Pair.of(new SkillInfo(maxHealth, health, level), ops.empty()));
        }

        @Override
        public <T> DataResult<T> encode(final SkillInfo input, final DynamicOps<T> ops, final T prefix) {
            return ops.mapBuilder().add(
                    "maxHealth",
                    ops.createInt(input.maxHealth)
            ).add(
                    "health",
                    ops.createInt(input.health)
            ).add(
                    "level",
                    ops.createInt(input.level)
            ).build(ops.empty());
        }
    };
    public final int maxHealth;
    public final int health;
    public final int level;

    public SkillInfo(final int maxHealth, final int health, final int level) {
        this.maxHealth = maxHealth;
        this.health = health;
        this.level = level;
    }

    public EntityState createState() {
        return new EntityState(this);
    }
}
