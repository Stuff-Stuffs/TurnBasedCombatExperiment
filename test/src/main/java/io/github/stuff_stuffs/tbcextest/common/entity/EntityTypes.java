package io.github.stuff_stuffs.tbcextest.common.entity;

import io.github.stuff_stuffs.tbcexcore.common.TBCExCore;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.registry.Registry;

public final class EntityTypes {
    public static final EntityType<TestEntity> TEST_ENTITY_TYPE = FabricEntityTypeBuilder.createLiving().entityFactory(TestEntity::new).defaultAttributes(LivingEntity::createLivingAttributes).dimensions(EntityDimensions.fixed(1, 1)).build();

    public static void init() {
        Registry.register(Registry.ENTITY_TYPE, TBCExCore.createId("test"), TEST_ENTITY_TYPE);
    }

    private EntityTypes() {
    }
}
