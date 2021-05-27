package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.stat;

import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.damage.DamageResistances;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public record EntityStatType<T>(Text name, Supplier<T> defaultValue, Class<T> clazz) {
    public static final Registry<EntityStatType<?>> REGISTRY = FabricRegistryBuilder.createSimple((Class<EntityStatType<?>>) (Class) EntityStatType.class, TurnBasedCombatExperiment.createId("entity_stats")).buildAndRegister();
    public static final EntityStatType<Double> HEALTH_STAT = new EntityStatType<>(new LiteralText("health"), () -> 1.0, Double.class);
    public static final EntityStatType<DamageResistances> RESISTANCES_STAT = new EntityStatType<>(new LiteralText("resistance"), () -> DamageResistances.builder().build(), DamageResistances.class);

    static {
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("health"), HEALTH_STAT);
    }
}
