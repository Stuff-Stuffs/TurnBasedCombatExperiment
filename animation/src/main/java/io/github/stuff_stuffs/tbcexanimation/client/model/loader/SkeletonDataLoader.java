package io.github.stuff_stuffs.tbcexanimation.client.model.loader;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.tbcexanimation.client.model.ModelBone;
import io.github.stuff_stuffs.tbcexanimation.client.model.SkeletonData;
import io.github.stuff_stuffs.tbcexutil.common.DoubleQuaternion;
import io.github.stuff_stuffs.tbcexutil.common.GsonUtil;
import io.github.stuff_stuffs.tbcexutil.common.LoggerUtil;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.resource.Resource;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//TODO maybe use an json object with key names as bones instead of array
public final class SkeletonDataLoader {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().
            registerTypeHierarchyAdapter(Vec3d.class, new GsonUtil.Vec3dJson()).
            registerTypeHierarchyAdapter(DoubleQuaternion.class, new GsonUtil.DoubleQuaternionJson(true)).
            registerTypeHierarchyAdapter(SkeletonData.class, new SkeletonJson()).create();

    public static SkeletonData fromResource(final Resource resource) {
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            return GSON.fromJson(bufferedReader, SkeletonData.class);
        } catch (final Exception e) {
            LoggerUtil.LOGGER.error("Error while loading skeleton data", e);
            return null;
        }
    }

    private static final class SkeletonJson implements JsonDeserializer<SkeletonData> {

        public static PartialModelBone deserializeBone(final JsonElement element, final JsonDeserializationContext context) {
            final JsonObject object = element.getAsJsonObject();
            final String name = object.get("name").getAsString();
            final Vec3d pos = context.deserialize(object.get("pos"), Vec3d.class);
            final DoubleQuaternion rotation = context.deserialize(object.get("rotation"), DoubleQuaternion.class);
            final Vec3d pivotPoint = context.deserialize(object.get("pivot"), Vec3d.class);
            final Vec3d defaultScale = context.deserialize(object.get("scale"), Vec3d.class);
            String parent = null;
            if (object.has("parent")) {
                parent = object.get("parent").getAsString();
            }
            final List<Pair<Vec3d, Vec3d>> boneLines = new ArrayList<>();
            final JsonArray lines = object.getAsJsonArray("lines");
            for (final JsonElement line : lines) {
                final JsonObject l = line.getAsJsonObject();
                boneLines.add(Pair.of(context.deserialize(l.get("start"), Vec3d.class), context.deserialize(l.get("end"), Vec3d.class)));
            }
            return new PartialModelBone(name, pos, pivotPoint, rotation, defaultScale, boneLines, parent);
        }

        @Override
        public SkeletonData deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject object = json.getAsJsonObject();
            final JsonArray boneArray = object.getAsJsonArray("bones");
            final Map<String, PartialModelBone> boneMap = new Object2ReferenceOpenHashMap<>();
            for (final JsonElement element : boneArray) {
                final PartialModelBone partialBone = deserializeBone(element, context);
                boneMap.put(partialBone.name, partialBone);
            }
            for (final PartialModelBone bone : boneMap.values()) {
                final String parent = bone.parent;
                if (parent != null && !boneMap.containsKey(parent)) {
                    throw new RuntimeException("Missing bone: " + parent + ", referenced in bone: " + bone.name);
                }
            }
            final Map<String, ModelBone> bones = new Object2ReferenceOpenHashMap<>();
            while (bones.size() != boneMap.size()) {
                for (final PartialModelBone bone : boneMap.values()) {
                    if (!bones.containsKey(bone.name) && (bone.parent == null || bones.containsKey(bone.parent))) {
                        final ModelBone parent;
                        if (bone.parent != null) {
                            parent = bones.get(bone.parent);
                        } else {
                            parent = null;
                        }
                        bones.put(bone.name, new ModelBone(bone.name, bone.defaultPos, bone.pivotPoint, bone.defaultScale, bone.defaultRotation, bone.boneLines, parent));
                    }
                }
            }
            return new SkeletonData(new ReferenceOpenHashSet<>(bones.values()));
        }
    }

    private static final class PartialModelBone {
        private final String name;
        private final Vec3d defaultPos;
        private final Vec3d pivotPoint;
        private final DoubleQuaternion defaultRotation;
        private final Vec3d defaultScale;
        private final List<Pair<Vec3d, Vec3d>> boneLines;
        private final @Nullable String parent;

        private PartialModelBone(final String name, final Vec3d defaultPos, final Vec3d pivotPoint, final DoubleQuaternion defaultRotation, final Vec3d defaultScale, final List<Pair<Vec3d, Vec3d>> boneLines, @Nullable final String parent) {
            this.name = name;
            this.defaultPos = defaultPos;
            this.pivotPoint = pivotPoint;
            this.defaultRotation = defaultRotation;
            this.defaultScale = defaultScale;
            this.boneLines = boneLines;
            this.parent = parent;
        }
    }
}
