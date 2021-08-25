package io.github.stuff_stuffs.tbcexanimation.client.model.part.simple;

import io.github.stuff_stuffs.tbcexutil.common.LoggerUtil;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Map;

public class TextureSwappedSimpleModelPart extends SimpleModelPart {
    private final Map<Identifier, Identifier> textureSwapMap;

    public TextureSwappedSimpleModelPart(final SimpleModelPart wrapped, final Map<Identifier, Identifier> textureSwapMap) {
        super(wrapped.faces);
        this.textureSwapMap = textureSwapMap;
        for (final Map<Identifier, Face[]> map : wrapped.faces.values()) {
            for (final Identifier texture : map.keySet()) {
                if (!textureSwapMap.containsKey(texture)) {
                    LoggerUtil.LOGGER.warn("Missing texture mapping {} in TextureSwappedSimpleModelPart using default", texture);
                }
            }
        }
    }

    @Override
    public void render(final MatrixStack matrices, final VertexConsumerProvider vertexConsumers, final World world, final Vec3d pos) {
        for (final Map.Entry<SimpleModelPartMaterial.RenderType, Map<Identifier, Face[]>> renderTypeEntry : faces.entrySet()) {
            for (final Map.Entry<Identifier, Face[]> faceEntry : renderTypeEntry.getValue().entrySet()) {
                final Identifier texture = textureSwapMap.getOrDefault(faceEntry.getKey(), faceEntry.getKey());
                final VertexConsumer vertexConsumer = renderTypeEntry.getKey().create(texture, vertexConsumers);
                for (final Face face : faceEntry.getValue()) {
                    renderFace(face, matrices, vertexConsumer, world, pos);
                }
            }
        }
    }
}
