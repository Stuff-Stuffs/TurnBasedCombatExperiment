package io.github.stuff_stuffs.tbcexequipment.common.equipment;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.tbcexequipment.common.creation.EquipmentDataCreationContext;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.data.EquipmentData;
import io.github.stuff_stuffs.tbcexequipment.common.part.Part;
import io.github.stuff_stuffs.tbcexequipment.common.part.PartInstance;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public final class EquipmentType<T extends EquipmentData> {
    private final Text name;
    private final List<Text> description;
    private final Predicate<List<PartInstance>> partPredicate;
    private final Function<List<PartInstance>, IntList> indicesFunction;
    private final Codec<T> dataCodec;
    private final Codec<EquipmentData> uncheckedCodec;
    private final Function<EquipmentDataCreationContext, T> initializer;
    private final Int2ObjectMap<Identifier> poseMap;

    public EquipmentType(final Text name, final List<Text> description, final Predicate<List<PartInstance>> partPredicate, final Function<List<PartInstance>, IntList> indicesFunction, final Codec<T> dataCodec, final Function<EquipmentDataCreationContext, T> initializer, final Int2ObjectMap<Identifier> poseMap) {
        this.name = name;
        this.description = description;
        this.partPredicate = partPredicate;
        this.indicesFunction = indicesFunction;
        this.dataCodec = dataCodec;
        uncheckedCodec = dataCodec.xmap(Function.identity(), data -> (T) data);
        this.initializer = initializer;
        this.poseMap = poseMap;
    }

    public Identifier getPose(final int index) {
        final Identifier identifier = poseMap.get(index);
        if (identifier == null) {
            throw new TBCExException("Missing pose for index: " + index);
        }
        return identifier;
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

    public boolean check(final List<PartInstance> partInstances) {
        return partPredicate.test(partInstances);
    }

    public static EquipmentTypeBuilder builder() {
        return new EquipmentTypeBuilder();
    }

    public IntList generateIndices(final List<PartInstance> parts) {
        return indicesFunction.apply(parts);
    }

    private interface OptionalPredicate<T> {
        boolean test(T instance);

        boolean optional();
    }

    public static final class EquipmentTypeBuilder {
        private final List<OptionalPredicate<PartInstance>> predicates;
        private final Int2ObjectMap<Identifier> poses;

        private EquipmentTypeBuilder() {
            predicates = new ArrayList<>();
            poses = new Int2ObjectOpenHashMap<>();
        }

        public EquipmentTypeBuilder add(final Predicate<PartInstance> predicate, final Identifier poseName) {
            poses.put(poses.size(), poseName);
            predicates.add(new OptionalPredicate<>() {
                @Override
                public boolean test(final PartInstance instance) {
                    return predicate.test(instance) && instance.isValid();
                }

                @Override
                public boolean optional() {
                    return false;
                }
            });
            return this;
        }

        public EquipmentTypeBuilder addOptional(final Predicate<PartInstance> predicate, final Identifier poseName) {
            poses.put(poses.size(), poseName);
            predicates.add(new OptionalPredicate<>() {
                @Override
                public boolean test(final PartInstance instance) {
                    return predicate.test(instance) && instance.isValid();
                }

                @Override
                public boolean optional() {
                    return true;
                }
            });
            return this;
        }

        public EquipmentTypeBuilder add(final Part<?> part, final Identifier poseName) {
            return add(partInstance -> partInstance.getPart() == part, poseName);
        }

        public EquipmentTypeBuilder addOptional(final Part<?> part, final Identifier poseName) {
            return addOptional(partInstance -> partInstance.getPart() == part, poseName);
        }

        public EquipmentTypeBuilder add(final Tag<Part<?>> tag, final Identifier poseName) {
            return add(partInstance -> tag.contains(partInstance.getPart()), poseName);
        }

        public EquipmentTypeBuilder addOptional(final Tag<Part<?>> tag, final Identifier poseName) {
            return addOptional(partInstance -> tag.contains(partInstance.getPart()), poseName);
        }

        //TODO detect all optional equipment
        public <T extends EquipmentData> EquipmentType<T> build(final Text name, final List<Text> description, final Codec<T> dataCodec, final Function<EquipmentDataCreationContext, T> initializer) {
            if (predicates.size() == 0) {
                throw new TBCExException("Size 0 equipment");
            }
            final List<OptionalPredicate<PartInstance>> predicatesCopy = new ArrayList<>(predicates);
            return new EquipmentType<>(name, description, list -> generateIndices(predicatesCopy, list) != null, list -> generateIndices(predicatesCopy, list), dataCodec, initializer, poses);
        }

        private static IntList generateIndices(final List<OptionalPredicate<PartInstance>> predicates, final List<PartInstance> parts) {
            if (parts.size() == 0) {
                return null;
            }
            final IntList indices = new IntArrayList(parts.size());
            int i = 0, j = 0;
            while (true) {
                if (i == parts.size()) {
                    while (j < predicates.size()) {
                        if (!predicates.get(j).optional()) {
                            return null;
                        } else {
                            j++;
                        }
                    }
                    return indices;
                }
                if (j == predicates.size()) {
                    return null;
                }
                final PartInstance instance = parts.get(i);
                final OptionalPredicate<PartInstance> predicate = predicates.get(j);
                if (predicate.test(instance)) {
                    indices.add(j);
                    i++;
                    j++;
                } else {
                    if (!predicate.optional()) {
                        return null;
                    } else if (j < predicates.size()) {
                        j++;
                    }
                }
            }
        }
    }
}
