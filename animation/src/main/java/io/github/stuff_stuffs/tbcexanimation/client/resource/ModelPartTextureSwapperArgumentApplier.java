package io.github.stuff_stuffs.tbcexanimation.client.resource;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.ModelPart;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;
import java.util.Map;

public final class ModelPartTextureSwapperArgumentApplier implements ModelPartArgumentApplier {
    private static final Type TYPE = new TypeToken<Map<Identifier, Identifier>>() {
    }.getType();
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(TYPE, new Deserializer()).setPrettyPrinting().create();

    @Override
    public ModelPart apply(ModelPart modelPart, final String argument) {
        if (argument.isEmpty()) {
            return modelPart;
        }

        final Map<Identifier, Identifier> map = GSON.fromJson(argument, TYPE);
        for (final Map.Entry<Identifier, Identifier> entry : map.entrySet()) {
            modelPart = modelPart.remapTexture(entry.getKey(), entry.getValue());
        }
        return modelPart;
    }

    private static final class Deserializer implements JsonDeserializer<Map<Identifier, Identifier>> {

        @Override
        public Map<Identifier, Identifier> deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            final Map<Identifier, Identifier> identifierMap = new Object2ReferenceOpenHashMap<>();
            final JsonObject object = json.getAsJsonObject();
            for (final Map.Entry<String, JsonElement> entry : object.entrySet()) {
                identifierMap.put(new Identifier(entry.getKey()), new Identifier(entry.getValue().getAsString()));
            }
            return identifierMap;
        }
    }
}
