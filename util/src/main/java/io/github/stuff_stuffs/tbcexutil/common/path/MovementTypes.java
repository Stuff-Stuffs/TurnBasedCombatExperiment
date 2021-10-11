package io.github.stuff_stuffs.tbcexutil.common.path;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Locale;

public final class MovementTypes {
    public static final Registry<MovementType> REGISTRY = FabricRegistryBuilder.createSimple(MovementType.class, new Identifier("tbcexutil", "movement_types")).buildAndRegister();

    public static void init() {
        for (final BasicMovements movement : BasicMovements.values()) {
            Registry.register(REGISTRY, new Identifier("tbcexutil", "basic_" + movement.name().toLowerCase(Locale.ROOT)), movement);
        }
        for (final DiagonalMovements movement : DiagonalMovements.values()) {
            Registry.register(REGISTRY, new Identifier("tbcexutil", "diagonal_" + movement.name().toLowerCase(Locale.ROOT)), movement);
        }
        for (final SimpleJumpMovements movement : SimpleJumpMovements.values()) {
            Registry.register(REGISTRY, new Identifier("tbcexutil", "simple_jump_" + movement.name().toLowerCase(Locale.ROOT)), movement);
        }
    }

    private MovementTypes() {
    }
}
