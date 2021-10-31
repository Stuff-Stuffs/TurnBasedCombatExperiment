package io.github.stuff_stuffs.tbcexequipment.common.part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import io.github.stuff_stuffs.tbcexequipment.common.creation.PartDataCreationContext;
import io.github.stuff_stuffs.tbcexequipment.common.material.MaterialTags;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.text.LiteralText;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Parts {
    public static final RegistryKey<Registry<Part<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(TBCExEquipment.createId("parts"));
    public static final Registry<Part<?>> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable())).buildAndRegister();
    public static final Part<PartData> HANDLE_PART = new Part<>(new LiteralText("Handle"), List.of(new LiteralText("A basic handle")), material -> true, createSimpleCodec(() -> Parts.HANDLE_PART), createSimpleInitializer(() -> Parts.HANDLE_PART));
    public static final Part<PartData> AXE_HEAD_PART = new Part<>(new LiteralText("Axe Head"), List.of(new LiteralText("A basic axe head")), MaterialTags.BLADE_MATERIALS::contains, createSimpleCodec(() -> Parts.AXE_HEAD_PART), createSimpleInitializer(() -> Parts.AXE_HEAD_PART));
    public static final Part<PartData> SWORD_BLADE_PART = new Part<>(new LiteralText("Sword Blade"), List.of(new LiteralText("A basic sword blade")), MaterialTags.BLADE_MATERIALS::contains, createSimpleCodec(() -> Parts.SWORD_BLADE_PART), createSimpleInitializer(() -> Parts.SWORD_BLADE_PART));
    public static final Part<PartData> SIMPLE_SWORD_GUARD_PART = new Part<>(new LiteralText("Simple Sword Guard"), List.of(new LiteralText("A simple sword guard")), material -> true, createSimpleCodec(() -> Parts.SIMPLE_SWORD_GUARD_PART), createSimpleInitializer(() -> Parts.SIMPLE_SWORD_GUARD_PART));
    public static final Part<PartData> FANCY_SWORD_GUARD = new Part<>(new LiteralText("Fancy Sword Guard"), List.of(new LiteralText("A fancy sword guard")), material -> true, createSimpleCodec(() -> Parts.FANCY_SWORD_GUARD), createSimpleInitializer(() -> Parts.FANCY_SWORD_GUARD));
    public static final Part<PartData> SIMPLE_POMMEL = new Part<>(new LiteralText("Simple Pommel"), List.of(new LiteralText("A simple pommel")), material -> true, createSimpleCodec(() -> Parts.SIMPLE_POMMEL), createSimpleInitializer(() -> Parts.SIMPLE_POMMEL));
    public static final Part<PartData> FANCY_POMMEL = new Part<>(new LiteralText("Fancy Pommel"), List.of(new LiteralText("A fancy pommel")), material -> true, createSimpleCodec(() -> Parts.FANCY_POMMEL), createSimpleInitializer(() -> Parts.FANCY_POMMEL));

    private static Codec<PartData> createSimpleCodec(final Supplier<Part<?>> part) {
        return Codec.unit(() -> part::get);
    }

    private static Function<PartDataCreationContext, PartData> createSimpleInitializer(final Supplier<Part<?>> part) {
        return ctx -> part::get;
    }

    public static void init() {
        Registry.register(REGISTRY, TBCExEquipment.createId("handle"), HANDLE_PART);
        Registry.register(REGISTRY, TBCExEquipment.createId("axe_head"), AXE_HEAD_PART);
        Registry.register(REGISTRY, TBCExEquipment.createId("sword_blade"), SWORD_BLADE_PART);
        Registry.register(REGISTRY, TBCExEquipment.createId("simple_sword_guard"), SIMPLE_SWORD_GUARD_PART);
        Registry.register(REGISTRY, TBCExEquipment.createId("fancy_sword_guard"), FANCY_SWORD_GUARD);
        Registry.register(REGISTRY, TBCExEquipment.createId("simple_pommel"), SIMPLE_POMMEL);
        Registry.register(REGISTRY, TBCExEquipment.createId("fancy_pommel"), FANCY_POMMEL);
    }

    private Parts() {
    }
}
