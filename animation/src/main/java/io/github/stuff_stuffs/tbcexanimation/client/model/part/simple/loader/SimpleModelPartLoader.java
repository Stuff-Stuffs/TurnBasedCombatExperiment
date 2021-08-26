package io.github.stuff_stuffs.tbcexanimation.client.model.part.simple.loader;

import de.javagl.obj.*;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.simple.SimpleModelPart;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.simple.SimpleModelPartMaterial;
import io.github.stuff_stuffs.tbcexutil.common.Vec2d;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

//Based on HavenKing's Myron library
public final class SimpleModelPartLoader {
    public static SimpleModelPart load(final Identifier identifier, final ResourceManager resourceManager) throws IOException {
        final Obj obj = ObjReader.read(resourceManager.getResource(identifier).getInputStream());
        final Map<String, SimpleModelPartMaterial> materials = getMaterials(resourceManager, identifier, obj);
        final SimpleModelPart.Builder builder = SimpleModelPart.builder();
        final SimpleModelPart.Builder.FaceEmitter emitter = builder.getEmitter();
        for (final Map.Entry<String, Obj> entry : ObjSplitting.splitByMaterialGroups(obj).entrySet()) {
            final Obj group = entry.getValue();
            final SimpleModelPartMaterial material = materials.get(entry.getKey());
            for (int i = 0; i < entry.getValue().getNumFaces(); i++) {
                final ObjFace objFace = entry.getValue().getFace(i);
                emitter.material(material);
                if (objFace.getNumVertices() == 3) {
                    Vec3d p0 = null;
                    Vec3d p2 = null;
                    Vec2d t0 = null;
                    Vec2d t2 = null;
                    for (int j = 0; j < 3; j++) {
                        final int vertexIndex = objFace.getVertexIndex(j);
                        final int texCoordIndex = objFace.getTexCoordIndex(j);
                        final FloatTuple vertex = group.getVertex(vertexIndex);
                        final FloatTuple texCoord = group.getTexCoord(texCoordIndex);
                        emitter.vertex(j, vertex.getX(), vertex.getY(), vertex.getZ());
                        emitter.uv(j, texCoord.getX(), texCoord.getY());
                        if (j == 0) {
                            p0 = new Vec3d(vertex.getX(), vertex.getY(), vertex.getZ());
                            t0 = new Vec2d(texCoord.getX(), texCoord.getY());
                        } else if (j == 2) {
                            p2 = new Vec3d(vertex.getX(), vertex.getY(), vertex.getZ());
                            t2 = new Vec2d(texCoord.getX(), texCoord.getY());
                        }
                    }
                    final Vec3d p3 = p0.add(p2).multiply(0.5);
                    final Vec2d t3 = t0.add(t2).scale(0.5);
                    emitter.vertex(3, p3);
                    emitter.uv(3, t3);
                } else if (objFace.getNumVertices() == 4) {
                    for (int j = 0; j < 4; j++) {
                        final int vertexIndex = objFace.getVertexIndex(j);
                        final int texCoordIndex = objFace.getTexCoordIndex(j);
                        final FloatTuple vertex = group.getVertex(vertexIndex);
                        final FloatTuple texCoord = group.getTexCoord(texCoordIndex);
                        emitter.vertex(j, vertex.getX(), vertex.getY(), vertex.getZ());
                        emitter.uv(j, texCoord.getX(), texCoord.getY());
                    }
                } else {
                    throw new IOException("Illegal number of vertices, count: " + objFace.getNumVertices());
                }
                emitter.emit();
            }
        }
        return builder.build();
    }

    private static Map<String, SimpleModelPartMaterial> getMaterials(final ResourceManager resourceManager, final Identifier identifier, final Obj obj) throws IOException {
        final Map<String, SimpleModelPartMaterial> materials = new Object2ReferenceOpenHashMap<>();
        for (final String s : obj.getMtlFileNames()) {
            String path = identifier.getPath();
            path = path.substring(0, path.lastIndexOf('/') + 1) + s;
            final Identifier resource = new Identifier(identifier.getNamespace(), path);
            if (resourceManager.containsResource(resource)) {
                SimpleModelPartMaterialReader.read(new BufferedReader(new InputStreamReader(resourceManager.getResource(resource).getInputStream()))).forEach(material -> materials.put(material.getName(), material));
            }
        }
        return materials;
    }

    private SimpleModelPartLoader() {
    }
}
