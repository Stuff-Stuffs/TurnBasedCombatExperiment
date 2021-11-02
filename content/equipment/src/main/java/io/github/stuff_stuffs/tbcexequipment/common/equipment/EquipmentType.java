package io.github.stuff_stuffs.tbcexequipment.common.equipment;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.tbcexequipment.common.creation.EquipmentDataCreationContext;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.data.EquipmentData;
import io.github.stuff_stuffs.tbcexequipment.common.part.Part;
import io.github.stuff_stuffs.tbcexequipment.common.part.PartInstance;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import it.unimi.dsi.fastutil.objects.Object2ReferenceLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public final class EquipmentType<T extends EquipmentData> {
    private final Text name;
    private final List<Text> description;
    //Must have consistent iteration order
    private final Map<Identifier, Predicate<@Nullable PartInstance>> partPredicates;
    private final Codec<T> dataCodec;
    private final Codec<EquipmentData> uncheckedCodec;
    private final Function<EquipmentDataCreationContext, T> initializer;

    public EquipmentType(final Text name, final List<Text> description, final Map<Identifier, Predicate<@Nullable PartInstance>> partPredicate, final Codec<T> dataCodec, final Function<EquipmentDataCreationContext, T> initializer) {
        this.name = name;
        this.description = description;
        partPredicates = partPredicate;
        this.dataCodec = dataCodec;
        uncheckedCodec = dataCodec.xmap(Function.identity(), data -> (T) data);
        this.initializer = initializer;
    }

    public Text getName() {
        return name;
    }

    public List<Text> getDescription() {
        return description;
    }

    public Codec<T> getDataCodec() {
        return dataCodec;
    }

    public Codec<EquipmentData> getUncheckedCodec() {
        return uncheckedCodec;
    }

    public T initialize(final EquipmentDataCreationContext ctx) {
        return initializer.apply(ctx);
    }

    public boolean check(final Identifier partId, final PartInstance partInstance) {
        final Predicate<PartInstance> predicate = partPredicates.get(partId);
        if (predicate == null) {
            throw new TBCExException("Unknown part: " + partId);
        }
        return predicate.test(partInstance);
    }

    public Set<Identifier> getParts() {
        return partPredicates.keySet();
    }

    public @Nullable Map<Identifier, PartInstance> fromList(final List<PartInstance> parts) {
        final Iterator<Map.Entry<Identifier, Predicate<@Nullable PartInstance>>> iterator = partPredicates.entrySet().iterator();
        final Map<Identifier, PartInstance> mapped = new Object2ReferenceOpenHashMap<>();
        int listIndex = 0;
        while (iterator.hasNext()) {
            if (listIndex == parts.size()) {
                while (iterator.hasNext()) {
                    if (!iterator.next().getValue().test(null)) {
                        return null;
                    }
                }
                return mapped;
            }
            final Map.Entry<Identifier, Predicate<@Nullable PartInstance>> entry = iterator.next();
            if (entry.getValue().test(parts.get(listIndex))) {
                mapped.put(entry.getKey(), parts.get(listIndex));
                listIndex++;
            } else {
                if (!entry.getValue().test(null)) {
                    return null;
                }
            }
        }
        if (listIndex == parts.size()) {
            return mapped;
        }
        return null;
    }

    public boolean check(final Map<Identifier, PartInstance> partInstances) {
        int checked = 0;
        for (final Map.Entry<Identifier, Predicate<PartInstance>> entry : partPredicates.entrySet()) {
            final PartInstance partInstance = partInstances.get(entry.getKey());
            if(partInstance!=null) {
                checked++;
            }
            final boolean b = entry.getValue().test(partInstance);
            if (!b) {
                return false;
            }
        }
        return checked == partInstances.size();
    }

    public static EquipmentTypeBuilder builder() {
        return new EquipmentTypeBuilder();
    }

    public static final class EquipmentTypeBuilder {
        private final Map<Identifier, Predicate<@Nullable PartInstance>> predicates;

        private EquipmentTypeBuilder() {
            predicates = new Object2ReferenceLinkedOpenHashMap<>();
        }

        public EquipmentTypeBuilder add(final Identifier partId, final Predicate<PartInstance> predicate) {
            predicates.put(partId, predicate);
            return this;
        }

        public EquipmentTypeBuilder add(final Identifier partId, final Part<?> part) {
            return add(partId, partInstance -> partInstance != null && partInstance.getPart() == part);
        }

        public EquipmentTypeBuilder addOptional(final Identifier partId, final Part<?> part) {
            return add(partId, partInstance -> partInstance == null || partInstance.getPart() == part);
        }

        public EquipmentTypeBuilder add(final Identifier partId, final Tag<Part<?>> tag) {
            return add(partId, partInstance -> partInstance != null && tag.contains(partInstance.getPart()));
        }

        public EquipmentTypeBuilder addOptional(final Identifier partId, final Tag<Part<?>> tag) {
            return add(partId, partInstance -> partInstance == null || tag.contains(partInstance.getPart()));
        }

        //TODO detect all optional equipment
        public <T extends EquipmentData> EquipmentType<T> build(final Text name, final List<Text> description, final Codec<T> dataCodec, final Function<EquipmentDataCreationContext, T> initializer) {
            if (predicates.size() == 0) {
                throw new TBCExException("Size 0 equipment");
            }
            return new EquipmentType<>(name, description, new Object2ReferenceLinkedOpenHashMap<>(predicates), dataCodec, initializer);
        }
    }
}
