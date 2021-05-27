package io.github.stuff_stuffs.turnbasedcombat.common.battle.action.attack;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.damage.DamagePacket;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.effect.EntityEffectCollection;
import org.jetbrains.annotations.Nullable;

public final class AttackInfo {
    public static final Codec<AttackInfo> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<AttackInfo, T>> decode(final DynamicOps<T> ops, final T input) {
            final MapLike<T> map = ops.getMap(input).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final DamagePacket damage = DamagePacket.CODEC.parse(ops, map.get("damage")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final EntityEffectCollection effects = EntityEffectCollection.CODEC.parse(ops, map.get("effects")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            return DataResult.success(Pair.of(new AttackInfo(damage, effects), ops.empty()));
        }

        @Override
        public <T> DataResult<T> encode(final AttackInfo input, final DynamicOps<T> ops, final T prefix) {
            if (prefix != null && !prefix.equals(ops.empty())) {
                throw new RuntimeException();
            }
            return ops.mapBuilder().
                    add(
                            "damage",
                            DamagePacket.CODEC.encodeStart(ops, input.damage)
                    ).add(
                            "effects",
                            EntityEffectCollection.CODEC.encodeStart(ops, input.effects)
                    ).build(ops.empty());
        }
    };
    private final DamagePacket damage;
    private final EntityEffectCollection effects;

    public AttackInfo(final DamagePacket damage) {
        this(damage, new EntityEffectCollection());
    }

    public AttackInfo(final DamagePacket damage, final EntityEffectCollection effects) {
        this.damage = damage;
        this.effects = effects;
    }

    public void applyTarget(@Nullable final EntityState attacker, final EntityState target, final BattleState battleState) {
        target.addAllEffects(effects);
        target.damage(damage);
    }
}
