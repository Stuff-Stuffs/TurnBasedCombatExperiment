package io.github.stuff_stuffs.tbcexcore.common.battle.state.component;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.tbcexcore.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleState;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Function;

public final class BattleComponents {
    public static final Registry<Type<?, ?>> REGISTRY = FabricRegistryBuilder.createSimple((Class<Type<?, ?>>) (Object) Type.class, TurnBasedCombatExperiment.createId("battle_component")).buildAndRegister();

    public static final class Type<Mut extends View, View extends BattleComponent> {
        public final Function<BattleState, @Nullable Mut> extractor;
        public final Codec<BattleComponent> codec;
        public final BattleComponentKey<Mut, View> key;
        public final Set<BattleComponentKey<?, ?>> requiredComponents;

        public Type(final Function<BattleState, @Nullable Mut> extractor, final Codec<Mut> codec, final BattleComponentKey<Mut, View> key, final Set<BattleComponentKey<?, ?>> requiredComponents) {
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
            if (!(o instanceof BattleComponents.Type<?, ?> type)) {
                return false;
            }

            return key.equals(type.key);
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }
    }

    public static void init() {
    }

    private BattleComponents() {
    }
}
