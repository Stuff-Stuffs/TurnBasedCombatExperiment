package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;

public record SkillInfo(int maxHealth, int health, int strength, int dexterity, int vitality, int intelligence,
                        int level) {
    public static final Codec<SkillInfo> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<SkillInfo, T>> decode(final DynamicOps<T> ops, final T input) {
            final MapLike<T> mapLike = ops.getMap(input).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final int maxHealth = ops.getNumberValue(mapLike.get("maxHealth"), 20).intValue();
            final int health = ops.getNumberValue(mapLike.get("health"), 20).intValue();
            final int level = ops.getNumberValue(mapLike.get("level"), 0).intValue();
            final int strength = ops.getNumberValue(mapLike.get("strength"), 0).intValue();
            final int dexterity = ops.getNumberValue(mapLike.get("dexterity"), 0).intValue();
            final int vitality = ops.getNumberValue(mapLike.get("vitality"), 0).intValue();
            final int intelligence = ops.getNumberValue(mapLike.get("intelligence"), 0).intValue();
            return DataResult.success(Pair.of(new SkillInfo(maxHealth, health, strength, dexterity, vitality, intelligence, level), ops.empty()));
        }

        @Override
        public <T> DataResult<T> encode(final SkillInfo input, final DynamicOps<T> ops, final T prefix) {
            return ops.mapBuilder().add(
                    "maxHealth",
                    ops.createInt(input.maxHealth())
            ).add(
                    "health",
                    ops.createInt(input.health())
            ).add(
                    "level",
                    ops.createInt(input.level())
            ).add(
                    "strength",
                    ops.createDouble(input.strength())
            ).add(
                    "dexterity",
                    ops.createInt(input.dexterity())
            ).add(
                    "vitality",
                    ops.createInt(input.vitality())
            ).add(
                    "intelligence",
                    ops.createInt(input.intelligence())
            ).build(ops.empty());
        }
    };
}
