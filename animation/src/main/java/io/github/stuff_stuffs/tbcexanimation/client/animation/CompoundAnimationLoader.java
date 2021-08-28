package io.github.stuff_stuffs.tbcexanimation.client.animation;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import io.github.stuff_stuffs.tbcexutil.common.LoggerUtil;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class CompoundAnimationLoader {
    private static final Type TYPE = new TypeToken<List<Identifier>>() {
    }.getType();
    public static final Gson GSON = new GsonBuilder().registerTypeHierarchyAdapter(Identifier.class, new Identifier.Serializer()).registerTypeAdapter(TYPE, new AnimationListJson()).create();

    public static @Nullable CompoundAnimationData fromResource(final Resource resource) {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return new CompoundAnimationData(GSON.fromJson(reader, TYPE));
        } catch (final Exception e) {
            LoggerUtil.LOGGER.error("Error loading compound animation", e);
            return null;
        }
    }

    private static final class AnimationListJson implements JsonDeserializer<List<Identifier>> {
        @Override
        public List<Identifier> deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject object = json.getAsJsonObject();
            final JsonArray array = object.getAsJsonArray("animations");
            final List<Identifier> animations = new ArrayList<>();
            for (final JsonElement jsonElement : array) {
                animations.add(context.deserialize(jsonElement, Identifier.class));
            }
            return animations;
        }
    }

    private CompoundAnimationLoader() {
    }
}
