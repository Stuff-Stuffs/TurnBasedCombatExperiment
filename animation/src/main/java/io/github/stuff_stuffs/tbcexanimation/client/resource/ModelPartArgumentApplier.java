package io.github.stuff_stuffs.tbcexanimation.client.resource;

import io.github.stuff_stuffs.tbcexanimation.client.model.part.ModelPart;

public interface ModelPartArgumentApplier {
    ModelPart apply(ModelPart modelPart, String argument);
}
