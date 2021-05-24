package io.github.stuff_stuffs.turnbasedcombat.common.battle.action.attack;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;
import org.jetbrains.annotations.Nullable;

public final class AttackInfo {
    public static final Codec<AttackInfo> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<AttackInfo, T>> decode(final DynamicOps<T> ops, final T input) {
            final MapLike<T> map = ops.getMap(input).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final int damage = ops.getNumberValue(map.get("damage")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            }).intValue();
            return DataResult.success(Pair.of(new AttackInfo(damage), ops.empty()));
        }

        @Override
        public <T> DataResult<T> encode(final AttackInfo input, final DynamicOps<T> ops, final T prefix) {
            if (prefix != null && !prefix.equals(ops.empty())) {
                throw new RuntimeException();
            }
            return ops.mapBuilder().
                    add(
                            "damage",
                            ops.createInt(input.damage)
                    ).build(ops.empty());
        }
    };
    private final int damage;

    public AttackInfo(final int damage) {
        this.damage = damage;
    }

    public void applyAttacker(EntityState attacker, BattleState battleState) {

    }

    public void applyTarget(@Nullable EntityState attacker, EntityState target, BattleState battleState) {
        target.damage(damage);
    }
}
