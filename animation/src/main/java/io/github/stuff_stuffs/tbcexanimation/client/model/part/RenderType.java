package io.github.stuff_stuffs.tbcexanimation.client.model.part;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Identifier;

public enum RenderType {
    SOLID {
        @Override
        public VertexConsumer create(final Identifier texture, final VertexConsumerProvider vertexConsumers) {
            return vertexConsumers.getBuffer(RenderLayer.getEntitySolid(texture));
        }
    },
    CUTOUT {
        @Override
        public VertexConsumer create(final Identifier texture, final VertexConsumerProvider vertexConsumers) {
            return vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(texture, false));
        }
    },
    TRANSLUCENT {
        @Override
        public VertexConsumer create(final Identifier texture, final VertexConsumerProvider vertexConsumers) {
            return vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(texture));
        }
    };

    public abstract VertexConsumer create(Identifier texture, VertexConsumerProvider vertexConsumers);
}
