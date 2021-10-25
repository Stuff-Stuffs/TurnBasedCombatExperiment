package io.github.stuff_stuffs.tbcexequipment.common.part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexequipment.common.material.Material;
import io.github.stuff_stuffs.tbcexequipment.common.material.Materials;

public final class PartInstance {
    public static final Codec<PartInstance> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Parts.REGISTRY.fieldOf("part").forGetter(partInstance -> partInstance.part),
                    Materials.REGISTRY.fieldOf("material").forGetter(partInstance -> partInstance.material)
            ).apply(instance, PartInstance::new)
    );
    private final Part part;
    private final Material material;

    public PartInstance(final Part part, final Material material) {
        this.part = part;
        this.material = material;
    }

    public Part getPart() {
        return part;
    }

    public Material getMaterial() {
        return material;
    }
}
