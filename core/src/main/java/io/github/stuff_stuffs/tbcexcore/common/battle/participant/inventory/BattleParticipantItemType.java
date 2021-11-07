package io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.tbcexcore.common.TBCExCore;
import io.github.stuff_stuffs.tbcexutil.common.CodecUtil;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public final class BattleParticipantItemType {
    public static final Registry<BattleParticipantItemType> REGISTRY = FabricRegistryBuilder.createSimple(BattleParticipantItemType.class, TBCExCore.createId("items")).buildAndRegister();
    public static final Codec<BattleParticipantItem> CODEC = CodecUtil.createDependentPairCodecFirst(REGISTRY, type -> type.codec, BattleParticipantItem::getType);
    private final Codec<BattleParticipantItem> codec;
    private final BiPredicate<BattleParticipantItemStack, BattleParticipantItemStack> canMerge;
    private final BinaryOperator<BattleParticipantItemStack> merger;
    private final Function<BattleParticipantItemStack, Collection<ItemStack>> toItemStacks;

    public BattleParticipantItemType(final Codec<BattleParticipantItem> codec, final BiPredicate<BattleParticipantItemStack, BattleParticipantItemStack> canMerge, final BinaryOperator<BattleParticipantItemStack> merger, Function<BattleParticipantItemStack, Collection<ItemStack>> toItemStacks) {
        this.codec = codec;
        this.canMerge = canMerge;
        this.merger = merger;
        this.toItemStacks = toItemStacks;
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

    public static Collection<ItemStack> toItemStack(BattleParticipantItemStack stack) {
        return stack.getItem().getType().toItemStacks.apply(stack);
    }
}
