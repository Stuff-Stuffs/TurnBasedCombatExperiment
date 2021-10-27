package io.github.stuff_stuffs.tbcexequipment.common.part;

import com.mojang.serialization.Lifecycle;
import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import io.github.stuff_stuffs.tbcexequipment.common.material.MaterialTags;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.text.LiteralText;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.List;

public final class Parts {
    public static final RegistryKey<Registry<Part>> REGISTRY_KEY = RegistryKey.ofRegistry(TBCExEquipment.createId("parts"));
    public static final Registry<Part> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable())).buildAndRegister();
    public static final Part HANDLE_PART = new Part(new LiteralText("Handle"), List.of(new LiteralText("A basic handle")), material -> true);
    public static final Part AXE_HEAD_PART = new Part(new LiteralText("Axe Head"), List.of(new LiteralText("A basic axe head")), MaterialTags.BLADE_MATERIALS::contains);
    public static final Part SWORD_BLADE_PART = new Part(new LiteralText("Sword Blade"), List.of(new LiteralText("A basic sword blade")), MaterialTags.BLADE_MATERIALS::contains);
    public static final Part SIMPLE_SWORD_GUARD_PART = new Part(new LiteralText("Simple Sword Guard"), List.of(new LiteralText("A simple sword guard")), material -> true);
    public static final Part FANCY_SWORD_GUARD = new Part(new LiteralText("Fancy Sword Guard"), List.of(new LiteralText("A fancy sword guard")), material -> true);

    public static void init() {
        Registry.register(REGISTRY, TBCExEquipment.createId("handle"), HANDLE_PART);
        Registry.register(REGISTRY, TBCExEquipment.createId("axe_head"), AXE_HEAD_PART);
        Registry.register(REGISTRY, TBCExEquipment.createId("sword_blade"), SWORD_BLADE_PART);
        Registry.register(REGISTRY, TBCExEquipment.createId("simple_sword_guard"), SIMPLE_SWORD_GUARD_PART);
        Registry.register(REGISTRY, TBCExEquipment.createId("fancy_sword_guard"), FANCY_SWORD_GUARD);
    }

    private Parts() {
    }
}
