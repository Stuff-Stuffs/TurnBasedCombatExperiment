package io.github.stuff_stuffs.tbcexequipment.common.part;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.tbcexequipment.common.creation.PartDataCreationContext;
import io.github.stuff_stuffs.tbcexequipment.common.material.Material;
import io.github.stuff_stuffs.tbcexequipment.common.part.data.PartData;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Part<T extends PartData> {
    private final Text name;
    private final List<Text> description;
    private final Predicate<Material> materialTester;
    private final Codec<T> dataCodec;
    private final Codec<PartData> uncheckedCodec;
    private final Function<PartDataCreationContext, T> initializer;

    public Part(final Text name, final List<Text> description, final Predicate<Material> materialTester, final Codec<T> dataCodec, final Function<PartDataCreationContext, T> initializer) {
        this.name = name;
        this.description = description;
        this.materialTester = materialTester;
        this.dataCodec = dataCodec;
        uncheckedCodec = dataCodec.xmap(Function.identity(), data -> (T) data);
        this.initializer = initializer;
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

    public Codec<T> getDataCodec() {
        return dataCodec;
    }

    public Codec<PartData> getUncheckedCodec() {
        return uncheckedCodec;
    }

    public T initialize(final PartDataCreationContext ctx) {
        return initializer.apply(ctx);
    }

    @Override
    public String toString() {
        return "Part{" + getName().asString() + "}";
    }
}
