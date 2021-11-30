package io.github.stuff_stuffs.tbcexanimation.client.model.part.simple;

import io.github.stuff_stuffs.tbcexanimation.client.model.part.ModelPart;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.RenderType;
import io.github.stuff_stuffs.tbcexutil.client.RenderUtil;
import io.github.stuff_stuffs.tbcexutil.common.Vec2d;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vector4f;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public final class SimpleModelPart implements ModelPart {
    private static long LAST_LIGHT_QUERY = -1;
    private static int LAST_LIGHT = 0;
    private static final Vector4f LIGHT_VEC = new Vector4f();
    private static final BlockPos.Mutable MUTABLE = new BlockPos.Mutable();
    private final Map<RenderType, Map<Identifier, SimpleModelPartFactory.Face[]>> faces;

    SimpleModelPart(final Map<RenderType, Map<Identifier, SimpleModelPartFactory.Face[]>> faces) {
        this.faces = faces;
    }

    @Override
    public void render(final MatrixStack matrices, final VertexConsumerProvider vertexConsumers, final World world, final Vec3d pos) {
        for (final Map.Entry<RenderType, Map<Identifier, SimpleModelPartFactory.Face[]>> renderTypeEntry : faces.entrySet()) {
            for (final Map.Entry<Identifier, SimpleModelPartFactory.Face[]> faceEntry : renderTypeEntry.getValue().entrySet()) {
                final VertexConsumer vertexConsumer = renderTypeEntry.getKey().create(faceEntry.getKey(), vertexConsumers);
                for (final SimpleModelPartFactory.Face face : faceEntry.getValue()) {
                    renderFace(face, matrices, vertexConsumer, world, pos);
                }
            }
        }
    }

    @Override
    public void tick(final double timeSinceLast) {

    }

    private static void renderFace(final SimpleModelPartFactory.Face face, final MatrixStack matrices, final VertexConsumer vertexConsumer, final World world, final Vec3d pos) {
        light(RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, face.topLeft(), matrices), face.material().getColour()), face.topLeftUV()).overlay(OverlayTexture.DEFAULT_UV), face.topLeft(), pos, face.material().isEmissive(), matrices, world).normal(0, 1, 0).next();
        light(RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, face.topRight(), matrices), face.material().getColour()), face.topRightUV()).overlay(OverlayTexture.DEFAULT_UV), face.topRight(), pos, face.material().isEmissive(), matrices, world).normal(0, 1, 0).next();
        light(RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, face.bottomRight(), matrices), face.material().getColour()), face.bottomRightUV()).overlay(OverlayTexture.DEFAULT_UV), face.bottomRight(), pos, face.material().isEmissive(), matrices, world).normal(0, 1, 0).next();
        light(RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, face.bottomLeft(), matrices), face.material().getColour()), face.bottomLeftUV()).overlay(OverlayTexture.DEFAULT_UV), face.bottomLeft(), pos, face.material().isEmissive(), matrices, world).normal(0, 1, 0).next();
    }

    private static VertexConsumer light(final VertexConsumer vertexConsumer, final Vec3d pos, final Vec3d offset, final boolean emissive, final MatrixStack matrices, final World world) {
        final int packed;
        if (emissive) {
            packed = LightmapTextureManager.pack(15, 15);
        } else {
            packed = getLight(pos, offset, matrices, world);
        }
        return vertexConsumer.light(packed);
    }

    private static int getLight(final Vec3d pos, final Vec3d offset, final MatrixStack matrices, final World world) {
        LIGHT_VEC.set((float) pos.x, (float) pos.y, (float) pos.z, 0);
        LIGHT_VEC.transform(matrices.peek().getPositionMatrix());
        MUTABLE.set(LIGHT_VEC.getX() + offset.x, LIGHT_VEC.getY() + offset.y, LIGHT_VEC.getZ() + offset.z);
        final long l = MUTABLE.asLong();
        if (l == LAST_LIGHT_QUERY) {
            return LAST_LIGHT;
        }
        LAST_LIGHT_QUERY = l;
        LAST_LIGHT = LightmapTextureManager.pack(world.getLightLevel(LightType.BLOCK, MUTABLE), world.getLightLevel(LightType.SKY, MUTABLE));
        return LAST_LIGHT;
    }
}
