package io.github.stuff_stuffs.tbcexequipment.common.part.data;

import io.github.stuff_stuffs.tbcexequipment.common.material.Material;
import io.github.stuff_stuffs.tbcexequipment.common.part.Part;

public abstract class AbstractPartData implements PartData {
    private final Part<?> part;
    protected final Material material;
    protected final int level;

    protected AbstractPartData(final Part<?> part, final Material material, final int level) {
        this.part = part;
        this.material = material;
        this.level = level;
    }

    @Override
    public Part<?> getType() {
        return part;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public int getLevel() {
        return level;
    }
}
