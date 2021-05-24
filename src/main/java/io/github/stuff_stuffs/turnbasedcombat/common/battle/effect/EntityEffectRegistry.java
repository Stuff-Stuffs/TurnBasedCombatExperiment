package io.github.stuff_stuffs.turnbasedcombat.common.battle.effect;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.registry.Registry;

public class EntityEffectRegistry {
    public static final Registry<Type> REGISTRY = FabricRegistryBuilder.createSimple(Type.class, TurnBasedCombatExperiment.createId("entity_effect")).buildAndRegister();

    public static class Type {
        public final Codec<EntityEffect> codec;

        public <T> Type(Codec<T> codec, Class<T> clazz) {
            this.codec = (Codec<EntityEffect>) codec;
        }
    }
}
