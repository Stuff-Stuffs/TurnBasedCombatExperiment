package io.github.stuff_stuffs.tbcexanimation.client.resource;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.ModelPart;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.RenderType;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;
import java.util.Locale;
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

        final Map<Identifier, Pair<Identifier, RenderType>> map = GSON.fromJson(argument, TYPE);
        for (final Map.Entry<Identifier, Pair<Identifier, RenderType>> entry : map.entrySet()) {
            modelPart = modelPart.remapTexture(entry.getKey(), entry.getValue().getFirst(), entry.getValue().getSecond());
        }
        return modelPart;
    }

    private static final class Deserializer implements JsonDeserializer<Map<Identifier, Pair<Identifier, RenderType>>> {

        @Override
        public Map<Identifier, Pair<Identifier, RenderType>> deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            final Map<Identifier, Pair<Identifier, RenderType>> identifierMap = new Object2ReferenceOpenHashMap<>();
            final JsonObject object = json.getAsJsonObject();
            for (final Map.Entry<String, JsonElement> entry : object.entrySet()) {
                final JsonObject o = entry.getValue().getAsJsonObject();
                identifierMap.put(new Identifier(entry.getKey()), Pair.of(new Identifier(o.get("texture").getAsString()), RenderType.valueOf(o.get("render_type").getAsString().toUpperCase(Locale.ROOT))));
            }
            return identifierMap;
        }
    }
}
