package io.github.stuff_stuffs.tbcexanimation.client.model.bundle;

import com.google.gson.*;
import io.github.stuff_stuffs.tbcexanimation.client.TBCExAnimationClient;
import io.github.stuff_stuffs.tbcexanimation.client.resource.ModelPartIdentifier;
import io.github.stuff_stuffs.tbcexutil.common.LoggerUtil;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class ModelPartBundleLoader {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().
            registerTypeHierarchyAdapter(Identifier.class, new Identifier.Serializer()).
            registerTypeHierarchyAdapter(ModelPartIdentifier.class, ModelPartIdentifier.SERIALIZER).
            registerTypeHierarchyAdapter(ModelPartBundle.class, new ModelPartBundleJson()).
            create();

    public static @Nullable ModelPartBundle fromResource(final Resource resource) {
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            return GSON.fromJson(bufferedReader, ModelPartBundle.class);
        } catch (final Exception e) {
            LoggerUtil.LOGGER.error("Error while loading model part bundle", e);
            return null;
        }
    }

    private static final class ModelPartBundleJson implements JsonDeserializer<ModelPartBundle> {
        @Override
        public ModelPartBundle deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject object = json.getAsJsonObject();
            final JsonObject parts = object.getAsJsonObject("parts");
            final ModelPartBundle.Builder builder = ModelPartBundle.builder();
            for (final Map.Entry<String, JsonElement> boneEntry : parts.entrySet()) {
                for (final Map.Entry<String, JsonElement> partEntry : boneEntry.getValue().getAsJsonObject().entrySet()) {
                    builder.addPart(boneEntry.getKey(), partEntry.getKey(), TBCExAnimationClient.MODEL_MANAGER.getModelPart((ModelPartIdentifier) context.deserialize(partEntry.getValue(), ModelPartIdentifier.class)));
                }
            }
            return builder.build();
        }
    }
}
