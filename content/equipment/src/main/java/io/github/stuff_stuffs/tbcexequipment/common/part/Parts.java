package io.github.stuff_stuffs.tbcexequipment.common.part;

import com.mojang.serialization.Lifecycle;
import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import io.github.stuff_stuffs.tbcexequipment.common.material.MaterialTags;
import io.github.stuff_stuffs.tbcexequipment.common.part.data.*;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.text.LiteralText;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.List;

public final class Parts {
    public static final RegistryKey<Registry<Part<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(TBCExEquipment.createId("parts"));
    public static final Registry<Part<?>> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable())).buildAndRegister();
    public static final Part<HandlePartData> HANDLE_PART = new Part<>(new LiteralText("Handle"), List.of(new LiteralText("A basic handle")), material -> true, HandlePartData.CODEC, HandlePartData::new);
    public static final Part<SwordBladePartData> SWORD_BLADE_PART = new Part<>(new LiteralText("Sword Blade"), List.of(new LiteralText("A basic sword blade")), MaterialTags.BLADE_MATERIALS::contains, SwordBladePartData.CODEC, SwordBladePartData::new);
    public static final Part<SimpleSwordGuardPartData> SIMPLE_SWORD_GUARD_PART = new Part<>(new LiteralText("Simple Sword Guard"), List.of(new LiteralText("A simple sword guard")), material -> true, SimpleSwordGuardPartData.CODEC, SimpleSwordGuardPartData::new);
    public static final Part<FancySwordGuardPartData> FANCY_SWORD_GUARD = new Part<>(new LiteralText("Fancy Sword Guard"), List.of(new LiteralText("A fancy sword guard")), material -> true, FancySwordGuardPartData.CODEC, FancySwordGuardPartData::new);
    public static final Part<SimplePommelPartData> SIMPLE_POMMEL = new Part<>(new LiteralText("Simple Pommel"), List.of(new LiteralText("A simple pommel")), material -> true, SimplePommelPartData.CODEC, SimplePommelPartData::new);

    public static void init() {
        Registry.register(REGISTRY, TBCExEquipment.createId("handle"), HANDLE_PART);
        Registry.register(REGISTRY, TBCExEquipment.createId("sword_blade"), SWORD_BLADE_PART);
        Registry.register(REGISTRY, TBCExEquipment.createId("simple_sword_guard"), SIMPLE_SWORD_GUARD_PART);
        Registry.register(REGISTRY, TBCExEquipment.createId("fancy_sword_guard"), FANCY_SWORD_GUARD);
        Registry.register(REGISTRY, TBCExEquipment.createId("simple_pommel"), SIMPLE_POMMEL);
    }

    private Parts() {
    }
}
