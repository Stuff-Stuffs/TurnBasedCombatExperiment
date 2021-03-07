package io.github.stuff_stuffs.turnbasedcombat.common.entity;

import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;

public final class EntityTypes {
    public static final EntityType<AbstractBattleCameraEntity> BATTLE_CAMERA_ENTITY_TYPE = FabricEntityTypeBuilder.<AbstractBattleCameraEntity>create(SpawnGroup.MISC).dimensions(EntityDimensions.fixed(0, 0)).disableSaving().disableSummon().fireImmune().build();
    public static final EntityType<PlayerMarkerEntity> PLAYER_MARKER_ENTITY_TYPE = FabricEntityTypeBuilder.<PlayerMarkerEntity>create(SpawnGroup.MISC).dimensions(EntityDimensions.fixed(0,0)).disableSaving().disableSummon().fireImmune().build();

    public static void init() {
        Registry.register(Registry.ENTITY_TYPE, TurnBasedCombatExperiment.createId("battle_camera"), BATTLE_CAMERA_ENTITY_TYPE);
    }

    private EntityTypes() {
    }
}
