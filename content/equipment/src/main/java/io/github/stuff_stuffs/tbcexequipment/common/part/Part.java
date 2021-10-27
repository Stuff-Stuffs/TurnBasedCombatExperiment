package io.github.stuff_stuffs.tbcexequipment.common.part;

import io.github.stuff_stuffs.tbcexequipment.common.material.Material;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Predicate;

public final class Part {
    private final Text name;
    private final List<Text> description;
    private final Predicate<Material> materialTester;

    public Part(final Text name, final List<Text> description, final Predicate<Material> materialTester) {
        this.name = name;
        this.description = description;
        this.materialTester = materialTester;
    }

    public Text getName() {
        return name;
    }

    public List<Text> getDescription() {
        return description;
    }

    public boolean isValidMaterial(final Material material) {
        return materialTester.test(material);
    }

    @Override
    public String toString() {
        return "Part{" + getName().asString() + "}";
    }
}
