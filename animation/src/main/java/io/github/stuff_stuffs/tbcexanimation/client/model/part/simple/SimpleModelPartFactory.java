package io.github.stuff_stuffs.tbcexanimation.client.model.part.simple;

import io.github.stuff_stuffs.tbcexanimation.client.model.Skeleton;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.ModelPart;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.ModelPartFactory;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.RenderType;
import io.github.stuff_stuffs.tbcexutil.common.Vec2d;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public final class SimpleModelPartFactory implements ModelPartFactory {
    private final Map<RenderType, Map<Identifier, Face[]>> faces;
    private SimpleModelPart cached = null;

    public SimpleModelPartFactory(Map<RenderType, Map<Identifier, Face[]>>  faces) {
        this.faces = faces;
    }

    @Override
    public ModelPart create(Skeleton context) {
        if(cached==null) {
            cached = new SimpleModelPart(faces);
        }
        return cached;
    }

    @Override
    public ModelPartFactory remapTexture(Identifier target, Identifier replace, RenderType replacedRenderType) {
        final Map<RenderType, Map<Identifier, Face[]>> remapped = new EnumMap<>(RenderType.class);
        for (Map.Entry<RenderType, Map<Identifier, Face[]>> renderTypeEntry : faces.entrySet()) {
            for (Map.Entry<Identifier, Face[]> entry : renderTypeEntry.getValue().entrySet()) {
                if(entry.getKey().equals(target)) {
                    Face[] faces = entry.getValue();
                    if (faces != null) {
                        final Face[] prev = remapped.computeIfAbsent(replacedRenderType, r -> new Object2ReferenceOpenHashMap<>()).put(replace, faces);
                        if (prev != null) {
                            Face[] merged = new Face[faces.length + prev.length];
                            System.arraycopy(faces, 0, merged, 0, faces.length);
                            System.arraycopy(prev, 0, merged, faces.length, prev.length);
                            remapped.get(replacedRenderType).put(replace, merged);
                        }
                    }
                } else {
                    remapped.computeIfAbsent(renderTypeEntry.getKey(), r -> new Object2ReferenceOpenHashMap<>()).put(entry.getKey(), entry.getValue());
                }
            }
        }
        return new SimpleModelPartFactory(remapped);
    }

    public record Face(Vec3d topLeft, Vec3d topRight, Vec3d bottomRight, Vec3d bottomLeft, Vec2d topLeftUV,
                       Vec2d topRightUV, Vec2d bottomRightUV, Vec2d bottomLeftUV, SimpleModelPartMaterial material) {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Map<RenderType, Map<Identifier, Set<Face>>> faces = new EnumMap<>(RenderType.class);

        private Builder() {
        }

        public FaceEmitter getEmitter() {
            return new FaceEmitter();
        }

        public SimpleModelPartFactory build() {
            final Map<RenderType, Map<Identifier, Face[]>> builtFaces = new EnumMap<>(RenderType.class);
            for (final Map.Entry<RenderType, Map<Identifier, Set<Face>>> renderTypeEntry : faces.entrySet()) {
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
            return new SimpleModelPartFactory(builtFaces);
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

            public FaceEmitter copy(final Face face) {
                first = face.topLeft;
                second = face.topRight;
                third = face.bottomRight;
                fourth = face.bottomLeft;

                firstUV = face.topLeftUV;
                secondUV = face.topRightUV;
                thirdUV = face.bottomRightUV;
                fourthUV = face.bottomLeftUV;

                material = face.material;

                return this;
            }

            public FaceEmitter emitCopy(final Face face) {
                faces.computeIfAbsent(material.getRenderType(), r -> new Object2ReferenceOpenHashMap<>()).
                        computeIfAbsent(material.getTexture(), t -> new ReferenceOpenHashSet<>()).
                        add(face);
                setup();
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
