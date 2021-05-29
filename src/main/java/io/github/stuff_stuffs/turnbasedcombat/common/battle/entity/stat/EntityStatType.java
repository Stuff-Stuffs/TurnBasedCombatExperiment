package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.stat;

import com.mojang.serialization.Lifecycle;
import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.SkillInfo;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.function.BiFunction;

public final class EntityStatType<T> {
    public static final Registry<EntityStatType<?>> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<EntityStatType<?>>(RegistryKey.ofRegistry(TurnBasedCombatExperiment.createId("entity_stats")), Lifecycle.stable())).buildAndRegister();
    public static final EntityStatType<Double> MAX_HEALTH_STAT = new EntityStatType<>((skillInfo, view) -> (double) skillInfo.maxHealth());
    public static final EntityStatType<Double> STRENGTH_STAT = new EntityStatType<>(((skillInfo, view) -> (double) skillInfo.strength()));
    public static final EntityStatType<Double> DEXTERITY_STAT = new EntityStatType<>((skillInfo, view) -> (double) skillInfo.dexterity());
    public static final EntityStatType<Double> VITALITY_STAT = new EntityStatType<>(((skillInfo, view) -> (double) skillInfo.vitality()));
    public static final EntityStatType<Double> INTELLIGENCE_STAT = new EntityStatType<>((skillInfo, view) -> (double) skillInfo.intelligence());

    private final BiFunction<SkillInfo, EntityStateView, T> valueOrDefault;

    public EntityStatType(final BiFunction<SkillInfo, EntityStateView, T> valueOrDefault) {
        this.valueOrDefault = valueOrDefault;
    }

    public T getValueOrDefault(final SkillInfo skillInfo, final EntityStateView entityState) {
        return valueOrDefault.apply(skillInfo, entityState);
    }

    public static void init() {
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("max_health"), MAX_HEALTH_STAT);
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("strength"), STRENGTH_STAT);
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("dexterity"), DEXTERITY_STAT);
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("vitality"), VITALITY_STAT);
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("intelligence"), INTELLIGENCE_STAT);
    }
}
