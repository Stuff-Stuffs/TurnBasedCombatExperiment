package io.github.stuff_stuffs.tbcexequipment.common.material.stats;

import com.google.gson.*;
import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import io.github.stuff_stuffs.tbcexequipment.common.material.Material;
import io.github.stuff_stuffs.tbcexequipment.common.material.Materials;
import io.github.stuff_stuffs.tbcexutil.common.LoggerUtil;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import it.unimi.dsi.fastutil.objects.*;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class MaterialStatManager implements SimpleSynchronousResourceReloadListener {
    public static final Identifier CHANNEL_ID = TBCExEquipment.createId("material_stat_sync");
    private static final Identifier ID = TBCExEquipment.createId("material_stats");
    private static final Gson GSON = new GsonBuilder().registerTypeHierarchyAdapter(Wrapper.class, new WrapperDeserializer()).registerTypeHierarchyAdapter(UnbakedContainer.class, new UnbakedDeserializer()).create();
    private final Map<Material, Container> containers = new Reference2ObjectOpenHashMap<>();
    private final Map<Identifier, MaterialStat> stats = new Object2ReferenceOpenHashMap<>();
    private final Map<Identifier, Object2DoubleMap<Material>> compressible = new Object2ReferenceOpenHashMap<>();

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    public void receive(final PacketByteBuf buf) {
        final int statCount = buf.readVarInt();
        if (statCount != stats.size()) {
            throw new TBCExException("Mismatch stat count");
        }
        final int materialCount = buf.readVarInt();
        if (materialCount != Materials.REGISTRY.getIds().size()) {
            throw new TBCExException("Mismatch of material count");
        }
        compressible.clear();
        for (int i = 0; i < statCount; i++) {
            final Identifier statId = new Identifier(buf.readString());
            final Object2DoubleMap<Material> map = compressible.computeIfAbsent(statId, k -> new Object2DoubleOpenHashMap<>());
            for (int j = 0; j < materialCount; j++) {
                map.put(Materials.REGISTRY.get(buf.readVarInt()), buf.readDouble());
            }
        }
        final Map<Material, Reference2DoubleMap<MaterialStat>> stats = new Object2ReferenceOpenHashMap<>();
        for (final Map.Entry<Identifier, Object2DoubleMap<Material>> statEntry : compressible.entrySet()) {
            final MaterialStat stat = this.stats.get(statEntry.getKey());
            for (final Object2DoubleMap.Entry<Material> materialEntry : statEntry.getValue().object2DoubleEntrySet()) {
                final Reference2DoubleMap<MaterialStat> map = stats.computeIfAbsent(materialEntry.getKey(), k -> new Reference2DoubleOpenHashMap<>());
                map.put(stat, materialEntry.getDoubleValue());
            }
        }
        containers.clear();
        for (final Map.Entry<Material, Reference2DoubleMap<MaterialStat>> entry : stats.entrySet()) {
            containers.put(entry.getKey(), new Container(entry.getValue()));
        }
    }

    public void sync(final PacketSender sender) {
        final PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(compressible.size());
        buf.writeVarInt(Materials.REGISTRY.getIds().size());
        for (final Map.Entry<Identifier, Object2DoubleMap<Material>> statEntry : compressible.entrySet()) {
            buf.writeString(statEntry.getKey().toString());
            for (final Object2DoubleMap.Entry<Material> entry : statEntry.getValue().object2DoubleEntrySet()) {
                buf.writeVarInt(Materials.REGISTRY.getRawId(entry.getKey()));
                buf.writeDouble(entry.getDoubleValue());
            }
        }
        sender.sendPacket(CHANNEL_ID, buf);
    }

    @Override
    public void reload(final ResourceManager manager) {
        final Collection<Identifier> resourceIds = manager.findResources("tbcex/material/stats", s -> s.endsWith(".json"));
        final Map<Identifier, UnbakedContainer> unbakedContainers = new Object2ReferenceOpenHashMap<>();
        for (Material material : Materials.REGISTRY) {
            unbakedContainers.put(Materials.REGISTRY.getId(material), new UnbakedContainer(new Object2DoubleOpenHashMap<>()));
        }
        for (final Identifier resourceId : resourceIds) {
            try {
                final Wrapper wrapper = GSON.fromJson(new BufferedReader(new InputStreamReader(manager.getResource(resourceId).getInputStream())), Wrapper.class);
                unbakedContainers.compute(wrapper.getMaterial(), (id, container) -> {
                    if (container == null) {
                        return wrapper.getUnbakedContainer();
                    } else {
                        return container.merge(wrapper.getUnbakedContainer());
                    }
                });
            } catch (final IOException e) {
                LoggerUtil.LOGGER.error("Cannot access resource {}", resourceId);
            }
        }
        containers.clear();
        for (final Map.Entry<Identifier, UnbakedContainer> entry : unbakedContainers.entrySet()) {
            final Material material = Materials.REGISTRY.get(entry.getKey());
            if (material == null) {
                LoggerUtil.LOGGER.error("Unknown material {}", entry.getKey());
            } else {
                entry.getValue().check(entry.getKey(), stats.keySet());
                containers.put(material, entry.getValue().bake(stats::get));
            }
        }
        compressible.clear();
        for (final Map.Entry<Material, Container> containerEntry : containers.entrySet()) {
            for (final Map.Entry<Identifier, MaterialStat> statEntry : stats.entrySet()) {
                final Object2DoubleMap<Material> map = compressible.computeIfAbsent(statEntry.getKey(), k -> new Object2DoubleOpenHashMap<>());
                map.put(containerEntry.getKey(), containerEntry.getValue().get(statEntry.getValue()));
            }
        }
    }

    private static final class Container {
        private final Reference2DoubleMap<MaterialStat> stats;

        private Container(final Reference2DoubleMap<MaterialStat> stats) {
            this.stats = stats;
        }

        public double get(final MaterialStat stat) {
            return stats.getDouble(stat);
        }
    }

    private static final class UnbakedContainer {
        private final Object2DoubleMap<Identifier> stats;

        private UnbakedContainer(final Object2DoubleMap<Identifier> stats) {
            this.stats = stats;
        }

        public UnbakedContainer merge(final UnbakedContainer container) {
            final Object2DoubleMap<Identifier> stats = new Object2DoubleOpenHashMap<>(container.stats);
            stats.putAll(this.stats);
            return new UnbakedContainer(stats);
        }

        public void check(final Identifier material, final Set<Identifier> statSet) {
            for (final Identifier id : statSet) {
                if (!stats.containsKey(id)) {
                    throw new TBCExException("Missing stat " + id + " on material " + material);
                }
            }
        }

        public Container bake(final Function<Identifier, MaterialStat> statGetter) {
            final Reference2DoubleMap<MaterialStat> stats = new Reference2DoubleOpenHashMap<>();
            for (final Object2DoubleMap.Entry<Identifier> entry : this.stats.object2DoubleEntrySet()) {
                final MaterialStat stat = statGetter.apply(entry.getKey());
                if (stat == null) {
                    LoggerUtil.LOGGER.error("Tried to add unknown stat {} to material", entry.getKey());
                } else {
                    stats.put(stat, entry.getDoubleValue());
                }
            }
            return new Container(stats);
        }
    }


    public double get(final Material material, final MaterialStat stat) {
        return containers.get(material).get(stat);
    }

    public MaterialStat getStat(final Identifier identifier) {
        return stats.computeIfAbsent(identifier, id -> new MaterialStat() {
            @Override
            public Identifier getIdentifier() {
                return id;
            }

            @Override
            public String toString() {
                return "Stat: " + id;
            }
        });
    }

    private static final class Wrapper {
        private final Identifier material;
        private final UnbakedContainer unbakedContainer;

        private Wrapper(final Identifier material, final UnbakedContainer unbakedContainer) {
            this.material = material;
            this.unbakedContainer = unbakedContainer;
        }

        public Identifier getMaterial() {
            return material;
        }

        public UnbakedContainer getUnbakedContainer() {
            return unbakedContainer;
        }
    }

    private static final class WrapperDeserializer implements JsonDeserializer<Wrapper> {
        @Override
        public Wrapper deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject o = json.getAsJsonObject();
            final Identifier material = new Identifier(o.get("material").getAsString());
            final UnbakedContainer unbakedContainer = context.deserialize(o.get("stats"), UnbakedContainer.class);
            return new Wrapper(material, unbakedContainer);
        }
    }

    private static final class UnbakedDeserializer implements JsonDeserializer<UnbakedContainer> {
        @Override
        public UnbakedContainer deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject o = json.getAsJsonObject();
            final Object2DoubleMap<Identifier> stats = new Object2DoubleOpenHashMap<>();
            for (final Map.Entry<String, JsonElement> entry : o.entrySet()) {
                stats.put(new Identifier(entry.getKey()), entry.getValue().getAsDouble());
            }
            return new UnbakedContainer(stats);
        }
    }
}
