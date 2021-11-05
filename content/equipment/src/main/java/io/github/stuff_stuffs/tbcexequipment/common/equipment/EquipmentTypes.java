package io.github.stuff_stuffs.tbcexequipment.common.equipment;

import com.mojang.serialization.Lifecycle;
import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.data.LongSwordEquipmentData;
import io.github.stuff_stuffs.tbcexequipment.common.part.PartTags;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.text.LiteralText;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.List;

public final class EquipmentTypes {
    public static final RegistryKey<Registry<EquipmentType<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(TBCExEquipment.createId("equipment_type"));
    public static final Registry<EquipmentType<?>> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable())).buildAndRegister();

    public static final EquipmentType<LongSwordEquipmentData> LONG_SWORD_EQUIPMENT = EquipmentType.builder().addOptional(LongSwordEquipmentData.POMMEL_PART, PartTags.SWORD_POMMELS).add(LongSwordEquipmentData.HANDLE_PART, PartTags.HANDLES).addOptional(LongSwordEquipmentData.GUARD_PART, PartTags.SWORD_GUARDS).add(LongSwordEquipmentData.BLADE_PART, PartTags.SWORD_BLADES).build(new LiteralText("Long sword"), List.of(), LongSwordEquipmentData.CODEC, LongSwordEquipmentData.INITIALIZER);

    public static void init() {
        Registry.register(REGISTRY, TBCExEquipment.createId("long_sword"), LONG_SWORD_EQUIPMENT);
    }

    private EquipmentTypes() {
    }
}
