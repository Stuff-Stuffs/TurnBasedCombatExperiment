package io.github.stuff_stuffs.tbcexutil.common.path;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class MovementFlag {
    public static final Registry<MovementFlag> REGISTRY = FabricRegistryBuilder.createSimple(MovementFlag.class, new Identifier("tbcexutil", "movement_flags")).buildAndRegister();
    public static final MovementFlag FALL_RESET = Registry.register(REGISTRY, new Identifier("tbcexutil", "fall_reset"), new MovementFlag());
    public static final MovementFlag FALL = Registry.register(REGISTRY, new Identifier("tbcex_util", "fall"), new MovementFlag());
    public static final MovementFlag FALL_RESET_TAKE_DAMAGE = Registry.register(REGISTRY, new Identifier("tbcexutil", "fall_reset_hard"), new MovementFlag());

    public MovementFlag() {
    }
}
