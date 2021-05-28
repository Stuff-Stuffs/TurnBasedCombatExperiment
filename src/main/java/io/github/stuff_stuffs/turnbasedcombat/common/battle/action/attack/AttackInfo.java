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
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.effect.EntityEffectFactory;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.effect.EntityEffectFactoryType;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class AttackInfo {
    private static final Codec<List<EntityEffectFactory>> LIST_CODEC = Codec.list(EntityEffectFactoryType.CODEC);
    public static final Codec<AttackInfo> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<AttackInfo, T>> decode(final DynamicOps<T> ops, final T input) {
            final MapLike<T> map = ops.getMap(input).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final DamagePacket damage = DamagePacket.CODEC.parse(ops, map.get("damage")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final List<EntityEffectFactory> effects = LIST_CODEC.parse(ops, map.get("effects")).getOrThrow(false, s -> {
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
                            LIST_CODEC.encodeStart(ops, input.effects)
                    ).build(ops.empty());
        }
    };
    private final DamagePacket damage;
    private final List<EntityEffectFactory> effects;

    public AttackInfo(final DamagePacket damage) {
        this(damage, new ReferenceArrayList<>());
    }

    public AttackInfo(final DamagePacket damage, final List<EntityEffectFactory> effects) {
        this.damage = damage;
        this.effects = effects;
    }

    public void applyTarget(final EntityState target) {
        target.addAllEffects(effects);
        target.damage(damage);
    }
}
