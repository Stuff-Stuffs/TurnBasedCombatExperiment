package io.github.stuff_stuffs.tbcexanimation.client.model.part.simple;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Identifier;

public final class SimpleModelPartMaterial {
    private final String name;
    private final RenderType renderType;
    private final Identifier texture;
    private final int colour;
    private final boolean emissive;

    public SimpleModelPartMaterial(String name, RenderType renderType, Identifier texture, int colour, boolean emissive) {
        this.name = name;
        this.renderType = renderType;
        this.texture = texture;
        this.colour = colour;
        this.emissive = emissive;
    }

    public String getName() {
        return name;
    }

    public RenderType getRenderType() {
        return renderType;
    }

    public Identifier getTexture() {
        return texture;
    }

    public int getColour() {
        return colour;
    }

    public boolean isEmissive() {
        return emissive;
    }

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
}
