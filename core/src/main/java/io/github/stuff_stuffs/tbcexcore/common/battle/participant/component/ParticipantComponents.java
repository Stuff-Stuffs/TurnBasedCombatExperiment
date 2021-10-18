package io.github.stuff_stuffs.tbcexcore.common.battle.participant.component;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.tbcexcore.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.status.ParticipantStatusEffect;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.status.ParticipantStatusEffectComponent;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.status.ParticipantStatusEffectComponentView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.status.ParticipantStatusEffects;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantInventory;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStats;
import io.github.stuff_stuffs.tbcexcore.common.entity.BattleEntity;
import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import io.github.stuff_stuffs.tbcexutil.common.HorizontalDirection;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class ParticipantComponents {
    public static final Registry<Type<?, ?>> REGISTRY = FabricRegistryBuilder.createSimple((Class<Type<?, ?>>) (Object) Type.class, TurnBasedCombatExperiment.createId("participant_component")).buildAndRegister();

    public static final Type<ParticipantPosComponent, ParticipantPosComponentView> POS_COMPONENT_TYPE = new Type<>((entity, battleStateView) -> {
        final BlockPos pos = ((Entity) entity).getBlockPos();
        final BattleParticipantBounds bounds = entity.getBounds().withCenter(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        Direction bestDir = Direction.NORTH;
        double best = Double.NEGATIVE_INFINITY;
        final Vec3d facingVec = ((Entity) entity).getRotationVec(1);
        for (final Direction direction : Direction.values()) {
            if (direction.getAxis() != Direction.Axis.Y) {
                final double cur = new Vec3d(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ()).dotProduct(facingVec);
                if (cur > best) {
                    bestDir = direction;
                    best = cur;
                }
            }
        }
        final HorizontalDirection facing = HorizontalDirection.fromDirection(bestDir);
        return new ParticipantPosComponent(pos, bounds, facing);
    }, ParticipantPosComponent.CODEC, ParticipantComponentKey.get(ParticipantPosComponent.class, ParticipantPosComponentView.class), Set.of());

    public static final Type<ParticipantInfoComponent, ParticipantInfoComponentView> INFO_COMPONENT_TYPE = new Type<>((entity, battleParticipantStateView) -> {
        final Text name = ((Entity) entity).getDisplayName();
        final BattleParticipantInventory inventory = new BattleParticipantInventory(entity);
        final BattleParticipantStats stats = new BattleParticipantStats(entity);
        final double health = entity.tbcex_getCurrentHealth();
        return new ParticipantInfoComponent(name, inventory, stats, health);
    }, ParticipantInfoComponent.CODEC, ParticipantComponentKey.get(ParticipantInfoComponent.class, ParticipantInfoComponentView.class), Set.of());

    public static final Type<ParticipantRestoreComponent, ParticipantRestoreComponent> RESTORE_COMPONENT_TYPE = new Type<>((entity, battleParticipantStateView) -> {
        if (entity.tbcex_shouldSaveToTag()) {
            NbtCompound compound = new NbtCompound();
            ((Entity) entity).saveNbt(compound);
            ((Entity)entity).remove(Entity.RemovalReason.DISCARDED);
            return new ParticipantRestoreComponent(Optional.of(compound), ((Entity) entity).getPos());
        }
        entity.onBattleJoin(battleParticipantStateView.getBattleState().getHandle());
        return new ParticipantRestoreComponent(Optional.empty(), ((Entity) entity).getPos());
    }, ParticipantRestoreComponent.CODEC, ParticipantComponentKey.get(ParticipantRestoreComponent.class, ParticipantRestoreComponent.class), Set.of());

    public static final Type<ParticipantStatusEffectComponent, ParticipantStatusEffectComponentView> STATUS_EFFECT_COMPONENT_TYPE = new Type<>((entity, battleParticipantStateView) -> {
        Map<ParticipantStatusEffects.Type, ParticipantStatusEffect> map = new Reference2ReferenceOpenHashMap<>();
        for (ParticipantStatusEffects.Type type : ParticipantStatusEffects.REGISTRY) {
            final ParticipantStatusEffect effect = type.extractor.apply(battleParticipantStateView, entity);
            if (effect != null) {
                map.put(type, effect);
            }
        }
        return new ParticipantStatusEffectComponent(map);
    }, ParticipantStatusEffectComponent.CODEC, ParticipantComponentKey.get(ParticipantStatusEffectComponent.class, ParticipantStatusEffectComponentView.class), Set.of());

    public static final class Type<Mut extends View, View extends ParticipantComponent> {
        public final BiFunction<BattleEntity, BattleParticipantStateView, @Nullable Mut> extractor;
        public final Codec<ParticipantComponent> codec;
        public final ParticipantComponentKey<Mut, View> key;
        public final Set<ParticipantComponentKey<?, ?>> requiredComponents;

        public Type(final BiFunction<BattleEntity, BattleParticipantStateView, @Nullable Mut> extractor, final Codec<Mut> codec, final ParticipantComponentKey<Mut, View> key, final Set<ParticipantComponentKey<?, ?>> requiredComponents) {
            this.extractor = extractor;
            this.codec = codec.xmap(Function.identity(), component -> (Mut) component);
            this.key = key;
            this.requiredComponents = requiredComponents;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Type<?, ?> type)) {
                return false;
            }

            return key.equals(type.key);
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }
    }

    private ParticipantComponents() {
    }

    public static void init() {
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("info"), INFO_COMPONENT_TYPE);
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("pos"), POS_COMPONENT_TYPE);
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("restore"), RESTORE_COMPONENT_TYPE);
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("status_effect"), STATUS_EFFECT_COMPONENT_TYPE);
    }
}
