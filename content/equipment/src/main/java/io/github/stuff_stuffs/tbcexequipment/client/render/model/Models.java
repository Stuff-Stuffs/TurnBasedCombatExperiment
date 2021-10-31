package io.github.stuff_stuffs.tbcexequipment.client.render.model;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.tbcexequipment.client.render.model.equipment.UnbakedEquipmentItemModel;
import io.github.stuff_stuffs.tbcexequipment.client.render.model.part.PartItemModel;
import io.github.stuff_stuffs.tbcexequipment.client.render.model.part.PartPlacementInfo;
import io.github.stuff_stuffs.tbcexequipment.client.render.model.part.PartPlacementInfoContainer;
import io.github.stuff_stuffs.tbcexequipment.client.render.model.part.UnbakedPartItemModel;
import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public final class Models {
    private static final Type POSE_PAIR_TYPE = new TypeToken<Pair<Identifier, PartPlacementInfoContainer>>() {
    }.getType();
    private static final Map<Identifier, PartPlacementInfoContainer> POSE_TO_PLACEMENT_CONTAINER_MAP = new Object2ReferenceOpenHashMap<>();
    private static final Gson GSON = new GsonBuilder().registerTypeHierarchyAdapter(PartPlacementInfo.class, new PartPlacementInfo.Deserializer()).registerTypeHierarchyAdapter(PartPlacementInfoContainer.class, new PartPlacementInfoContainer.Deserializer()).registerTypeAdapter(POSE_PAIR_TYPE, new PoseDeserializer()).create();

    public static void init() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return TBCExEquipment.createId("part_placement");
            }

            @Override
            public void reload(final ResourceManager manager) {
                final Collection<Identifier> resourceIds = manager.findResources("part_placement", s -> s.endsWith(".json"));
                POSE_TO_PLACEMENT_CONTAINER_MAP.clear();
                for (final Identifier resourceId : resourceIds) {
                    try {
                        final Resource resource = manager.getResource(resourceId);
                        final Pair<Identifier, PartPlacementInfoContainer> container = GSON.fromJson(new BufferedReader(new InputStreamReader(resource.getInputStream())), POSE_PAIR_TYPE);
                        POSE_TO_PLACEMENT_CONTAINER_MAP.compute(container.getFirst(), (id, curContainer) -> {
                            if (curContainer == null) {
                                return container.getSecond();
                            } else {
                                return curContainer.merge(container.getSecond());
                            }
                        });
                    } catch (final IOException ignored) {

                    }
                }
            }
        });
        final Identifier partItemModel = TBCExEquipment.createId("item/part_instance");
        final Identifier equipmentItemModel = TBCExEquipment.createId("item/equipment_instance");
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(resourceManager -> (resourceId, context) -> {
            if (resourceId.equals(partItemModel)) {
                return new UnbakedPartItemModel();
            }
            if (resourceId.equals(equipmentItemModel)) {
                return new UnbakedEquipmentItemModel();
            }
            return null;
        });
    }

    public static PartPlacementInfo getPlacementInfo(Identifier pose, Identifier part) {
        return POSE_TO_PLACEMENT_CONTAINER_MAP.getOrDefault(pose, PartPlacementInfoContainer.DEFAULT).get(part);
    }

    public static final class PoseDeserializer implements JsonDeserializer<Pair<Identifier, PartPlacementInfoContainer>> {
        @Override
        public Pair<Identifier, PartPlacementInfoContainer> deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject o = json.getAsJsonObject();
            final Identifier id = new Identifier(o.get("pose").getAsString());
            final JsonElement containerJson = o.get("container");
            final PartPlacementInfoContainer container = context.deserialize(containerJson, PartPlacementInfoContainer.class);
            return Pair.of(id, container);
        }
    }

    private Models() {
    }
}
