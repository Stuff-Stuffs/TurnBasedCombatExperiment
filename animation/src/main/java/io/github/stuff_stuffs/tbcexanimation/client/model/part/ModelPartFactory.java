package io.github.stuff_stuffs.tbcexanimation.client.model.part;

import io.github.stuff_stuffs.tbcexanimation.client.model.Skeleton;
import net.minecraft.util.Identifier;

public interface ModelPartFactory {
    ModelPart create(Skeleton context);

    ModelPartFactory remapTexture(Identifier target, Identifier replace, RenderType replacedRenderType);
}
