package io.github.stuff_stuffs.tbcexanimation.client.model.part.simple;

import io.github.stuff_stuffs.tbcexanimation.client.model.part.ModelPart;
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
    private final Map<SimpleModelPartMaterial.RenderType, Map<Identifier, Face[]>> faces;

    private SimpleModelPart(final Map<SimpleModelPartMaterial.RenderType, Map<Identifier, Face[]>> faces) {
        this.faces = faces;
    }

    @Override
    public void render(final MatrixStack matrices, final VertexConsumerProvider vertexConsumers, final World world, Vec3d pos) {
        for (final Map.Entry<SimpleModelPartMaterial.RenderType, Map<Identifier, Face[]>> renderTypeEntry : faces.entrySet()) {
            for (final Map.Entry<Identifier, Face[]> faceEntry : renderTypeEntry.getValue().entrySet()) {
                final VertexConsumer vertexConsumer = renderTypeEntry.getKey().create(faceEntry.getKey(), vertexConsumers);
                for (final Face face : faceEntry.getValue()) {
                    light(RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, face.topLeft, matrices), face.colour), face.topLeftUV).overlay(OverlayTexture.DEFAULT_UV), face.topLeft, pos, face.emissive, matrices, world).normal(0, 1, 0).next();
                    light(RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, face.topRight, matrices), face.colour), face.topRightUV).overlay(OverlayTexture.DEFAULT_UV), face.topRight, pos, face.emissive, matrices, world).normal(0, 1, 0).next();
                    light(RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, face.bottomRight, matrices), face.colour), face.bottomRightUV).overlay(OverlayTexture.DEFAULT_UV), face.bottomRight, pos, face.emissive, matrices, world).normal(0, 1, 0).next();
                    light(RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, face.bottomLeft, matrices), face.colour), face.bottomLeftUV).overlay(OverlayTexture.DEFAULT_UV), face.bottomLeft, pos, face.emissive, matrices, world).normal(0, 1, 0).next();
                }
            }
        }
    }

    private static VertexConsumer light(final VertexConsumer vertexConsumer, final Vec3d pos, Vec3d offset, final boolean emissive, final MatrixStack matrices, final World world) {
        final int packed;
        if (emissive) {
            packed = LightmapTextureManager.pack(15, 15);
        } else {
            packed = getLight(pos, offset, matrices, world);
        }
        return vertexConsumer.light(packed);
    }

    private static int getLight(final Vec3d pos, Vec3d offset, final MatrixStack matrices, final World world) {
        LIGHT_VEC.set((float) pos.x, (float) pos.y, (float) pos.z, 1);
        LIGHT_VEC.transform(matrices.peek().getModel());
        MUTABLE.set(LIGHT_VEC.getX() + offset.x, LIGHT_VEC.getY() + offset.y, LIGHT_VEC.getZ() + offset.z);
        final long l = MUTABLE.asLong();
        if (l == LAST_LIGHT_QUERY) {
            return LAST_LIGHT;
        }
        LAST_LIGHT_QUERY = l;
        LAST_LIGHT = LightmapTextureManager.pack(world.getLightLevel(LightType.BLOCK, MUTABLE), world.getLightLevel(LightType.SKY, MUTABLE));
        return LAST_LIGHT;
    }

    public record Face(Vec3d topLeft, Vec3d topRight, Vec3d bottomRight, Vec3d bottomLeft, int colour, Vec2d topLeftUV,
                       Vec2d topRightUV, Vec2d bottomRightUV, Vec2d bottomLeftUV, boolean emissive) {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Map<SimpleModelPartMaterial.RenderType, Map<Identifier, Set<Face>>> faces = new EnumMap<>(SimpleModelPartMaterial.RenderType.class);

        private Builder() {
        }

        public FaceEmitter getEmitter() {
            return new FaceEmitter();
        }

        public SimpleModelPart build() {
            final Map<SimpleModelPartMaterial.RenderType, Map<Identifier, Face[]>> builtFaces = new EnumMap<>(SimpleModelPartMaterial.RenderType.class);
            for (final Map.Entry<SimpleModelPartMaterial.RenderType, Map<Identifier, Set<Face>>> renderTypeEntry : faces.entrySet()) {
                final Map<Identifier, Face[]> texturedFaces = new Object2ReferenceOpenHashMap<>();
                for (final Map.Entry<Identifier, Set<Face>> entry : renderTypeEntry.getValue().entrySet()) {
                    texturedFaces.put(entry.getKey(), entry.getValue().toArray(new Face[0]));
                }
                builtFaces.put(renderTypeEntry.getKey(), texturedFaces);
            }
            return new SimpleModelPart(builtFaces);
        }

        public final class FaceEmitter {
            private static final Identifier DEFAULT_TEX = new Identifier("minecraft", "missing");
            private Vec3d first;
            private Vec3d second;
            private Vec3d third;
            private Vec3d fourth;
            private Vec2d firstUV;
            private Vec2d secondUV;
            private Vec2d thirdUV;
            private Vec2d fourthUV;
            private int colour;
            private Identifier texture;
            private SimpleModelPartMaterial.RenderType renderType;
            private boolean emissive;

            private FaceEmitter() {
                setup();
            }

            private void setup() {
                first = Vec3d.ZERO;
                second = Vec3d.ZERO;
                third = Vec3d.ZERO;
                fourth = Vec3d.ZERO;
                firstUV = Vec2d.ZERO;
                secondUV = Vec2d.ZERO;
                thirdUV = Vec2d.ZERO;
                fourthUV = Vec2d.ZERO;
                colour = -1;
                texture = DEFAULT_TEX;
                renderType = SimpleModelPartMaterial.RenderType.SOLID;
                emissive = false;
            }

            public FaceEmitter vertex(final int index, final double x, final double y, final double z) {
                return vertex(index, new Vec3d(x, y, z));
            }

            public FaceEmitter vertex(final int index, final Vec3d pos) {
                switch (index) {
                    case 0 -> first = pos;
                    case 1 -> second = pos;
                    case 2 -> third = pos;
                    case 3 -> fourth = pos;
                    default -> throw new IllegalArgumentException("Vertex index must be 0-3 inclusive, was: " + index);
                }
                return this;
            }

            public FaceEmitter uv(final int index, final double u, final double v) {
                return uv(index, new Vec2d(u, v));
            }

            public FaceEmitter uv(final int index, final Vec2d pos) {
                switch (index) {
                    case 0 -> firstUV = pos;
                    case 1 -> secondUV = pos;
                    case 2 -> thirdUV = pos;
                    case 3 -> fourthUV = pos;
                    default -> throw new IllegalArgumentException("Vertex index must be 0-3 inclusive, was: " + index);
                }
                return this;
            }

            public FaceEmitter colour(final int colour) {
                this.colour = colour;
                return this;
            }

            public FaceEmitter texture(final Identifier texture) {
                this.texture = texture;
                return this;
            }

            public FaceEmitter renderType(final SimpleModelPartMaterial.RenderType renderType) {
                this.renderType = renderType;
                return this;
            }

            public FaceEmitter emissive(boolean emissive) {
                this.emissive = emissive;
                return this;
            }

            public FaceEmitter emit() {
                faces.computeIfAbsent(renderType, r -> new Object2ReferenceOpenHashMap<>()).
                        computeIfAbsent(texture, t -> new ReferenceOpenHashSet<>()).
                        add(
                                new Face(first, second, third, fourth, colour, firstUV, secondUV, thirdUV, fourthUV, emissive)
                        );
                setup();
                return this;
            }
        }
    }
}
