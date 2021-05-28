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
    private final BiFunction<SkillInfo, EntityStateView, T> valueOrDefault;

    public EntityStatType(final BiFunction<SkillInfo, EntityStateView, T> valueOrDefault) {
        this.valueOrDefault = valueOrDefault;
    }

    public T getValueOrDefault(final SkillInfo skillInfo, final EntityStateView entityState) {
        return valueOrDefault.apply(skillInfo, entityState);
    }
}
