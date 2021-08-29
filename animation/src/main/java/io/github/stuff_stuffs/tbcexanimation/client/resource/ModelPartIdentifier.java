package io.github.stuff_stuffs.tbcexanimation.client.resource;

import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;
import java.util.Map;

public final class ModelPartIdentifier {
    public static final Serializer SERIALIZER = new Serializer();
    private final Identifier identifier;
    private final Object2ObjectLinkedOpenHashMap<String, String> arguments;

    private ModelPartIdentifier(final Identifier identifier, final Object2ObjectLinkedOpenHashMap<String, String> arguments) {
        this.identifier = identifier;
        this.arguments = arguments;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Map<String, String> getArguments() {
        return arguments;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "ModelPartIdentifier{" +
                "identifier=" + identifier +
                ", arguments=" + arguments +
                '}';
    }

    public static final class Builder {
        private final Map<String, String> arguments = new Object2ObjectLinkedOpenHashMap<>();

        private Builder() {
        }

        public Builder addArgument(final String name, final String argument) {
            if (arguments.put(name, argument) != null) {
                throw new RuntimeException();
            }
            return this;
        }

        public ModelPartIdentifier build(final Identifier identifier) {
            return new ModelPartIdentifier(identifier, new Object2ObjectLinkedOpenHashMap<>(arguments));
        }
    }

    public static class Serializer implements JsonSerializer<ModelPartIdentifier>, JsonDeserializer<ModelPartIdentifier> {
        private Serializer() {
        }

        @Override
        public ModelPartIdentifier deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject object = json.getAsJsonObject();
            final Identifier identifier = context.deserialize(object.get("identifier"), Identifier.class);
            final Builder builder = builder();
            if (object.has("args")) {
                final JsonObject args = object.getAsJsonObject("args");
                for (final Map.Entry<String, JsonElement> entry : args.entrySet()) {
                    builder.addArgument(entry.getKey(), entry.getValue().toString());
                }
            }
            return builder.build(identifier);
        }

        @Override
        public JsonElement serialize(final ModelPartIdentifier src, final Type typeOfSrc, final JsonSerializationContext context) {
            final JsonObject object = new JsonObject();
            object.add("identifier", context.serialize(src.getIdentifier(), Identifier.class));
            if (!src.getArguments().isEmpty()) {
                final JsonObject args = new JsonObject();
                for (final Map.Entry<String, String> entry : src.getArguments().entrySet()) {
                    args.addProperty(entry.getKey(), entry.getValue());
                }
                object.add("args", args);
            }
            return object;
        }
    }
}
