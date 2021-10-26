package io.github.stuff_stuffs.tbcexequipment.common.part;

import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.util.List;

public final class Parts {
    public static final Registry<Part> REGISTRY = FabricRegistryBuilder.createSimple(Part.class, TBCExEquipment.createId("parts")).buildAndRegister();
    public static final Part HANDLE_PART = new Part() {
        @Override
        public Text getName() {
            return new LiteralText("Handle");
        }

        @Override
        public List<Text> getDescription() {
            return List.of(new LiteralText("A basic handle"));
        }
    };
    public static final Part AXE_HEAD_PART = new Part() {
        @Override
        public Text getName() {
            return new LiteralText("Axe Head");
        }

        @Override
        public List<Text> getDescription() {
            return List.of(new LiteralText("A basic axe head"));
        }
    };
    public static final Part SWORD_BLADE_PART = new Part() {
        @Override
        public Text getName() {
            return new LiteralText("Sword Blade");
        }

        @Override
        public List<Text> getDescription() {
            return List.of(new LiteralText("A basic sword blade"));
        }
    };
    public static final Part SIMPLE_SWORD_GUARD_PART = new Part() {
        @Override
        public Text getName() {
            return new LiteralText("Simple Sword Guard");
        }

        @Override
        public List<Text> getDescription() {
            return List.of(new LiteralText("A simple sword guard"));
        }
    };

    public static void init() {
        Registry.register(REGISTRY, TBCExEquipment.createId("handle"), HANDLE_PART);
        Registry.register(REGISTRY, TBCExEquipment.createId("axe_head"), AXE_HEAD_PART);
        Registry.register(REGISTRY, TBCExEquipment.createId("sword_blade"), SWORD_BLADE_PART);
        Registry.register(REGISTRY, TBCExEquipment.createId("simple_sword_guard"), SIMPLE_SWORD_GUARD_PART);
    }

    private Parts() {
    }
}
