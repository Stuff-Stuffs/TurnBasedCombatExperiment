package io.github.stuff_stuffs.tbcexanimation.client.animation.keyframe;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import io.github.stuff_stuffs.tbcexutil.common.DoubleQuaternion;
import io.github.stuff_stuffs.tbcexutil.common.Easing;
import io.github.stuff_stuffs.tbcexutil.common.LoggerUtil;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class KeyframeDataLoader {
    private static final Type MAP_STRING_KEYFRAME_ANIMATE_DATA = new TypeToken<Map<String, KeyframeAnimationData>>() {
    }.getType();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(
            MAP_STRING_KEYFRAME_ANIMATE_DATA, new AnimationFileJson()
    ).registerTypeAdapter(
            KeyframeAnimationData.class, new KeyframeAnimationDataJson()
    ).create();

    public static @Nullable Map<Identifier, KeyframeAnimationData> getAnimations(final Identifier identifier, final Resource resource) {
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            final Map<String, KeyframeAnimationData> map = GSON.fromJson(bufferedReader, MAP_STRING_KEYFRAME_ANIMATE_DATA);
            final Map<Identifier, KeyframeAnimationData> out = new Object2ReferenceOpenHashMap<>(map.size());
            for (final Map.Entry<String, KeyframeAnimationData> entry : map.entrySet()) {
                out.put(new Identifier(identifier.getNamespace(), identifier.getPath() + "/" + entry.getKey().substring(entry.getKey().indexOf('.') + 1)), entry.getValue());
            }
            return out;
        } catch (final Exception e) {
            LoggerUtil.LOGGER.error("Error while loading animation keyframe data", e);
            return null;
        }
    }

    private static final class AnimationFileJson implements JsonDeserializer<Map<String, KeyframeAnimationData>> {
        @Override
        public Map<String, KeyframeAnimationData> deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject object = json.getAsJsonObject();
            final JsonElement format_version = object.get("format_version");
            if (!"1.8.0".equals(format_version.getAsString())) {
                throw new RuntimeException();
            }
            final JsonObject animations = object.getAsJsonObject("animations");
            final Map<String, KeyframeAnimationData> out = new Object2ReferenceOpenHashMap<>();
            for (final Map.Entry<String, JsonElement> entry : animations.entrySet()) {
                out.put(entry.getKey(), context.deserialize(entry.getValue(), KeyframeAnimationData.class));
            }
            return out;
        }
    }

    private static final class KeyframeAnimationDataJson implements JsonDeserializer<KeyframeAnimationData> {

        @Override
        public KeyframeAnimationData deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject o = json.getAsJsonObject();
            final double animation_length = o.get("animation_length").getAsDouble();
            final KeyframeAnimationData.Builder builder = KeyframeAnimationData.builder();
            final JsonObject bones = o.getAsJsonObject("bones");
            for (final Map.Entry<String, JsonElement> entry : bones.entrySet()) {
                final String bone = entry.getKey();
                final JsonObject keyframes = entry.getValue().getAsJsonObject();
                if (keyframes.has("rotation")) {
                    final JsonObject rotation = keyframes.getAsJsonObject("rotation");
                    for (final Map.Entry<String, JsonElement> rotationEntry : rotation.entrySet()) {
                        final double startTime = Double.parseDouble(rotationEntry.getKey());
                        final JsonObject rotationObject = rotation.getAsJsonObject(rotationEntry.getKey());
                        final JsonArray vector = rotationObject.getAsJsonArray("vector");
                        final DoubleQuaternion quaternion = new DoubleQuaternion(vector.get(0).getAsDouble(), vector.get(1).getAsDouble(), vector.get(2).getAsDouble(), true);
                        final Easing easing = Easing.valueOf(rotationObject.has("easing") ? rotationObject.get("easing").getAsString() : "easeInQuad");
                        builder.addRotationKeyframe(bone, quaternion, easing, startTime);
                    }
                }
                if (keyframes.has("position")) {
                    final JsonObject position = keyframes.getAsJsonObject("position");
                    for (final Map.Entry<String, JsonElement> positionEntry : position.entrySet()) {
                        final double startTime = Double.parseDouble(positionEntry.getKey());
                        final JsonObject rotationObject = position.getAsJsonObject(positionEntry.getKey());
                        final JsonArray vector = rotationObject.getAsJsonArray("vector");
                        final Vec3d vec = new Vec3d(vector.get(0).getAsDouble(), vector.get(1).getAsDouble(), vector.get(2).getAsDouble());
                        final Easing easing = Easing.valueOf(rotationObject.has("easing") ? rotationObject.get("easing").getAsString() : "easeInQuad");
                        builder.addPositionKeyframe(bone, vec, easing, startTime);
                    }
                }
                if (keyframes.has("scale")) {
                    final JsonObject scale = keyframes.getAsJsonObject("scale");
                    for (final Map.Entry<String, JsonElement> scaleEntry : scale.entrySet()) {
                        final double startTime = Double.parseDouble(scaleEntry.getKey());
                        final JsonObject rotationObject = scale.getAsJsonObject(scaleEntry.getKey());
                        final JsonArray vector = rotationObject.getAsJsonArray("vector");
                        final Vec3d vec = new Vec3d(vector.get(0).getAsDouble(), vector.get(1).getAsDouble(), vector.get(2).getAsDouble());
                        final Easing easing = Easing.valueOf(rotationObject.has("easing") ? rotationObject.get("easing").getAsString() : "easeInQuad");
                        builder.addScaleKeyframe(bone, vec, easing, startTime);
                    }
                }
            }
            return builder.build(animation_length, false);
        }
    }

    private KeyframeDataLoader() {
    }
}
