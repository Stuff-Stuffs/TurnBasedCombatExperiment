package io.github.stuff_stuffs.tbcexanimation.client.resource;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import io.github.stuff_stuffs.tbcexanimation.client.model.bundle.ModelPartBundle;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.ModelPart;
import io.github.stuff_stuffs.tbcexanimation.client.model.part.simple.SimpleModelPart;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;
import java.util.Map;

public final class ModelPartTextureSwapperArgumentApplier implements ModelPartArgumentApplier {
    private static final Type TYPE = new TypeToken<Map<Identifier, Identifier>>() {}.getType();
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(TYPE, new Deserializer()).setPrettyPrinting().create();
    @Override
    public ModelPart apply(ModelPart modelPart, String argument) {
        if(argument.isEmpty()) {
            return modelPart;
        }

        Map<Identifier, Identifier> map = GSON.fromJson(argument, TYPE);
        if(modelPart instanceof SimpleModelPart simpleModelPart) {
            return simpleModelPart.remapMaterials(SimpleModelPart.createTextureRemapper(map));
        }
        if(modelPart instanceof ModelPartBundle.WrappedModelPart wrapped) {
            if(wrapped.wrapped instanceof SimpleModelPart simpleModelPart)
            return simpleModelPart.remapMaterials(SimpleModelPart.createTextureRemapper(map));
        }
        throw new UnsupportedOperationException();
    }

    private static final class Deserializer implements JsonDeserializer<Map<Identifier, Identifier>> {

        @Override
        public Map<Identifier, Identifier> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Map<Identifier, Identifier> identifierMap = new Object2ReferenceOpenHashMap<>();
            JsonObject object = json.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                identifierMap.put(new Identifier(entry.getKey()), new Identifier(entry.getValue().getAsString()));
            }
            return identifierMap;
        }
    }
}
