package io.github.stuff_stuffs.tbcexanimation.client.model.part.simple;

import io.github.stuff_stuffs.tbcexanimation.client.model.part.ModelPart;
import io.github.stuff_stuffs.tbcexutil.client.RenderUtil;
import io.github.stuff_stuffs.tbcexutil.common.Vec2d;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
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

import java.util.*;

public class SimpleModelPart implements ModelPart {
    private static long LAST_LIGHT_QUERY = -1;
    private static int LAST_LIGHT = 0;
    private static final Vector4f LIGHT_VEC = new Vector4f();
    private static final BlockPos.Mutable MUTABLE = new BlockPos.Mutable();
    protected final Map<SimpleModelPartMaterial.RenderType, Map<Identifier, Face[]>> faces;

    protected SimpleModelPart(final Map<SimpleModelPartMaterial.RenderType, Map<Identifier, Face[]>> faces) {
        this.faces = faces;
    }

    @Override
    public void render(final MatrixStack matrices, final VertexConsumerProvider vertexConsumers, final World world, final Vec3d pos) {
        for (final Map.Entry<SimpleModelPartMaterial.RenderType, Map<Identifier, Face[]>> renderTypeEntry : faces.entrySet()) {
            for (final Map.Entry<Identifier, Face[]> faceEntry : renderTypeEntry.getValue().entrySet()) {
                final VertexConsumer vertexConsumer = renderTypeEntry.getKey().create(faceEntry.getKey(), vertexConsumers);
                for (final Face face : faceEntry.getValue()) {
                    renderFace(face, matrices, vertexConsumer, world, pos);
                }
            }
        }
    }

    public Set<Identifier> getTextures() {
        final Set<Identifier> textures = new ObjectOpenHashSet<>();
        for (final Map<Identifier, Face[]> map : faces.values()) {
            textures.addAll(map.keySet());
        }
        return textures;
    }

    protected static void renderFace(final Face face, final MatrixStack matrices, final VertexConsumer vertexConsumer, final World world, final Vec3d pos) {
        light(RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, face.topLeft, matrices), face.material.getColour()), face.topLeftUV).overlay(OverlayTexture.DEFAULT_UV), face.topLeft, pos, face.material.isEmissive(), matrices, world).normal(0, 1, 0).next();
        light(RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, face.topRight, matrices), face.material.getColour()), face.topRightUV).overlay(OverlayTexture.DEFAULT_UV), face.topRight, pos, face.material.isEmissive(), matrices, world).normal(0, 1, 0).next();
        light(RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, face.bottomRight, matrices), face.material.getColour()), face.bottomRightUV).overlay(OverlayTexture.DEFAULT_UV), face.bottomRight, pos, face.material.isEmissive(), matrices, world).normal(0, 1, 0).next();
        light(RenderUtil.uv(RenderUtil.colour(RenderUtil.position(vertexConsumer, face.bottomLeft, matrices), face.material.getColour()), face.bottomLeftUV).overlay(OverlayTexture.DEFAULT_UV), face.bottomLeft, pos, face.material.isEmissive(), matrices, world).normal(0, 1, 0).next();
    }

    protected static VertexConsumer light(final VertexConsumer vertexConsumer, final Vec3d pos, final Vec3d offset, final boolean emissive, final MatrixStack matrices, final World world) {
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

    public record Face(Vec3d topLeft, Vec3d topRight, Vec3d bottomRight, Vec3d bottomLeft, Vec2d topLeftUV,
                       Vec2d topRightUV, Vec2d bottomRightUV, Vec2d bottomLeftUV, SimpleModelPartMaterial material) {
    }

    public SimpleModelPart remapMaterials(final MaterialRemapper remapper) {
        final Map<SimpleModelPartMaterial, List<Face>> faceByMaterial = new Object2ReferenceOpenHashMap<>();
        for (final Map<Identifier, Face[]> map : faces.values()) {
            for (final Face[] faces : map.values()) {
                for (final Face face : faces) {
                    faceByMaterial.computeIfAbsent(face.material(), mat -> new ArrayList<>()).add(face);
                }
            }
        }
        final Map<SimpleModelPartMaterial, List<Face>> remapped = new Object2ReferenceOpenHashMap<>();
        for (final Map.Entry<SimpleModelPartMaterial, List<Face>> entry : faceByMaterial.entrySet()) {
            remapped.computeIfAbsent(remapper.remapMaterial(entry.getKey()), mat -> new ArrayList<>()).addAll(entry.getValue());
        }
        final Map<SimpleModelPartMaterial.RenderType, Map<Identifier, Face[]>> built = new EnumMap<>(SimpleModelPartMaterial.RenderType.class);
        for (final Map.Entry<SimpleModelPartMaterial, List<Face>> entry : remapped.entrySet()) {
            final SimpleModelPartMaterial material = entry.getKey();
            final List<Face> facesToRebuild = entry.getValue();
            final Face[] rebuilt = new Face[facesToRebuild.size()];
            for (int i = 0; i < facesToRebuild.size(); i++) {
                final Face face = facesToRebuild.get(i);
                rebuilt[i] = new Face(face.topLeft, face.topRight, face.bottomRight, face.bottomLeft, face.topLeftUV, face.topRightUV, face.bottomRightUV, face.bottomLeftUV, material);
            }
            final Map<Identifier, Face[]> facesByTexture = built.computeIfAbsent(material.getRenderType(), renderType -> new Object2ReferenceOpenHashMap<>());
            final Face[] existing = facesByTexture.put(material.getTexture(), rebuilt);
            if (existing != null && existing.length > 0) {
                final Face[] merged = new Face[rebuilt.length + existing.length];
                System.arraycopy(rebuilt, 0, merged, 0, rebuilt.length);
                System.arraycopy(existing, 0, merged, rebuilt.length, existing.length);
                facesByTexture.put(material.getTexture(), merged);
            }
        }
        return new SimpleModelPart(built);
    }

    public static MaterialRemapper createTextureRemapper(Map<Identifier, Identifier> textureSwapMap) {
        return material -> {
            if(textureSwapMap.containsKey(material.getTexture())) {
                return new SimpleModelPartMaterial(material.getName(), material.getRenderType(), textureSwapMap.get(material.getTexture()), material.getColour(), material.isEmissive());
            }
            return material;
        };
    }

    public interface MaterialRemapper {
        SimpleModelPartMaterial remapMaterial(SimpleModelPartMaterial material);
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
                    final Face[] built = entry.getValue().toArray(new Face[0]);
                    final Face[] existing = texturedFaces.put(entry.getKey(), built);
                    if (existing != null && existing.length > 0) {
                        final Face[] merged = new Face[built.length + existing.length];
                        System.arraycopy(built, 0, merged, 0, built.length);
                        System.arraycopy(existing, 0, merged, built.length, existing.length);
                        texturedFaces.put(entry.getKey(), merged);
                    }
                }
                builtFaces.put(renderTypeEntry.getKey(), texturedFaces);
            }
            return new SimpleModelPart(builtFaces);
        }

        public final class FaceEmitter {
            private Vec3d first;
            private Vec3d second;
            private Vec3d third;
            private Vec3d fourth;
            private Vec2d firstUV;
            private Vec2d secondUV;
            private Vec2d thirdUV;
            private Vec2d fourthUV;
            private SimpleModelPartMaterial material;

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
                material = null;
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

            public FaceEmitter material(final SimpleModelPartMaterial material) {
                this.material = material;
                return this;
            }

            public FaceEmitter emit() {
                if (material == null) {
                    throw new RuntimeException();
                }
                faces.computeIfAbsent(material.getRenderType(), r -> new Object2ReferenceOpenHashMap<>()).
                        computeIfAbsent(material.getTexture(), t -> new ReferenceOpenHashSet<>()).
                        add(
                                new Face(first, second, third, fourth, firstUV, secondUV, thirdUV, fourthUV, material)
                        );
                setup();
                return this;
            }
        }
    }
}
