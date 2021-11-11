package io.github.stuff_stuffs.tbcexequipment.common.part.stats;

import net.minecraft.util.Identifier;

public interface PartStat<K> {
    Identifier getId();

    Class<K> getType();
}
