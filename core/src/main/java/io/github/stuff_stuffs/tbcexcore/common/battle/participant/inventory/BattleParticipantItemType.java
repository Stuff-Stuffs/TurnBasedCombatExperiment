package io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.tbcexcore.common.TurnBasedCombatExperiment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;

public final class BattleParticipantItemType {
    public static final Registry<BattleParticipantItemType> REGISTRY = FabricRegistryBuilder.createSimple(BattleParticipantItemType.class, TurnBasedCombatExperiment.createId("items")).buildAndRegister();
    public static final Codec<BattleParticipantItem> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<BattleParticipantItem, T>> decode(final DynamicOps<T> ops, final T input) {
            final MapLike<T> map = ops.getMap(input).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final BattleParticipantItemType type = REGISTRY.parse(ops, map.get("type")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            return type.codec.decode(ops, map.get("data"));
        }

        @Override
        public <T> DataResult<T> encode(final BattleParticipantItem input, final DynamicOps<T> ops, final T prefix) {
            return ops.mapBuilder().add(
                    "type",
                    REGISTRY.encodeStart(ops, input.getType())
            ).add(
                    "data",
                    input.getType().codec.encodeStart(ops, input)
            ).build(prefix);
        }
    };
    private final Codec<BattleParticipantItem> codec;
    private final BiPredicate<BattleParticipantItemStack, BattleParticipantItemStack> canMerge;
    private final BinaryOperator<BattleParticipantItemStack> merger;

    public BattleParticipantItemType(final Codec<BattleParticipantItem> codec, final BiPredicate<BattleParticipantItemStack, BattleParticipantItemStack> canMerge, final BinaryOperator<BattleParticipantItemStack> merger) {
        this.codec = codec;
        this.canMerge = canMerge;
        this.merger = merger;
    }

    private boolean canMerge0(final BattleParticipantItemStack first, final BattleParticipantItemStack second) {
        return canMerge.test(first, second);
    }

    private BattleParticipantItemStack merge0(final BattleParticipantItemStack first, final BattleParticipantItemStack second) {
        return merger.apply(first, second);
    }

    public static boolean canMerge(final BattleParticipantItemStack first, final BattleParticipantItemStack second) {
        if (first.getItem().getType() == second.getItem().getType()) {
            return first.getItem().getType().canMerge0(first, second);
        }
        return false;
    }

    public static @Nullable BattleParticipantItemStack merge(final BattleParticipantItemStack first, final BattleParticipantItemStack second) {
        if (canMerge(first, second)) {
            return first.getItem().getType().merge0(first, second);
        }
        return null;
    }
}
