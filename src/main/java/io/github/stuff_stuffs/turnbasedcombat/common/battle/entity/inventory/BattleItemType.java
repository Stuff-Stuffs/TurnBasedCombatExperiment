package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.inventory;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.registry.Registry;

public final class BattleItemType {
    public static final Registry<BattleItemType> REGISTRY = FabricRegistryBuilder.createSimple(BattleItemType.class, TurnBasedCombatExperiment.createId("battle_items")).buildAndRegister();
    public static final Codec<BattleItem> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<BattleItem, T>> decode(final DynamicOps<T> ops, final T input) {
            final MapLike<T> map = ops.getMap(input).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final BattleItemType type = REGISTRY.parse(ops, map.get("type")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            return type.codec.decode(ops, map.get("data"));
        }

        @Override
        public <T> DataResult<T> encode(final BattleItem input, final DynamicOps<T> ops, final T prefix) {
            return ops.mapBuilder().add(
                    "type",
                    REGISTRY.encodeStart(ops, input.getType())
            ).add(
                    "data",
                    input.getType().codec.encodeStart(ops, input)
            ).build(prefix);
        }
    };

    private final Codec<BattleItem> codec;

    public BattleItemType(final Codec<BattleItem> codec) {
        this.codec = codec;
    }
}
