package io.github.stuff_stuffs.tbcexanimation.client.resource;

import io.github.stuff_stuffs.tbcexanimation.client.model.part.ModelPartFactory;

public interface ModelPartArgumentApplier {
    ModelPartFactory apply(ModelPartFactory modelPartFactory, String argument);
}
