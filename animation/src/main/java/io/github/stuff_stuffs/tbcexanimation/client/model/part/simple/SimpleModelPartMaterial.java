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

    public SimpleModelPartMaterial(final String name, final RenderType renderType, final Identifier texture, final int colour, final boolean emissive) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleModelPartMaterial material)) return false;

        if (colour != material.colour) return false;
        if (emissive != material.emissive) return false;
        if (!name.equals(material.name)) return false;
        if (renderType != material.renderType) return false;
        return texture.equals(material.texture);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + renderType.hashCode();
        result = 31 * result + texture.hashCode();
        result = 31 * result + colour;
        result = 31 * result + (emissive ? 1 : 0);
        return result;
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
