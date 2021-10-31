package io.github.stuff_stuffs.tbcexequipment.client.render.model.part;

import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMaps;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;
import java.util.Map;

public final class PartPlacementInfoContainer {
    public static final PartPlacementInfoContainer DEFAULT = new PartPlacementInfoContainer(Object2ReferenceMaps.emptyMap());
    private final Map<Identifier, PartPlacementInfo> infos;

    private PartPlacementInfoContainer(final Map<Identifier, PartPlacementInfo> infos) {
        this.infos = infos;
    }

    public PartPlacementInfo get(final Identifier part) {
        return infos.getOrDefault(part, PartPlacementInfo.DEFAULT);
    }

    public PartPlacementInfoContainer merge(final PartPlacementInfoContainer other) {
        final Map<Identifier, PartPlacementInfo> map = new Object2ReferenceOpenHashMap<>(other.infos);
        map.putAll(infos);
        return new PartPlacementInfoContainer(map);
    }

    public static final class Deserializer implements JsonDeserializer<PartPlacementInfoContainer> {
        @Override
        public PartPlacementInfoContainer deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject o = json.getAsJsonObject();
            final Map<Identifier, PartPlacementInfo> infos = new Object2ReferenceOpenHashMap<>();
            for (final Map.Entry<String, JsonElement> entry : o.entrySet()) {
                infos.put(new Identifier(entry.getKey()), context.deserialize(entry.getValue(), PartPlacementInfo.class));
            }
            return new PartPlacementInfoContainer(infos);
        }
    }
}
