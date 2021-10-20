package io.github.stuff_stuffs.tbcexutil.common;

import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public final class BattleParticipantBounds implements Iterable<BattleParticipantBounds.Part> {
    private static final Codec<Part> PART_CODEC = RecordCodecBuilder.create(instance -> instance.group(Identifier.CODEC.fieldOf("name").forGetter(part -> part.name), CodecUtil.BOX_CODEC.fieldOf("box").forGetter(part -> part.box)).apply(instance, Part::new));
    public static final Codec<BattleParticipantBounds> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.unboundedMap(Identifier.CODEC, PART_CODEC).fieldOf("parts").forGetter(bounds -> bounds.parts), CodecUtil.VEC3D_CODEC.fieldOf("center").forGetter(bounds -> bounds.center)).apply(instance, BattleParticipantBounds::new));

    private final Map<Identifier, Part> parts;
    private final Vec3d center;

    private BattleParticipantBounds(final Map<Identifier, Part> parts, final Vec3d center) {
        this.parts = parts;
        this.center = center;
    }

    public BattleParticipantBounds offset(final double x, final double y, final double z) {
        if (x == 0 && y == 0 && z == 0) {
            return this;
        }
        final Builder builder = builder();
        for (final Part part : parts.values()) {
            builder.add(part.name, part.box.offset(x, y, z));
        }
        return builder.build(new Vec3d(x + center.x, y + center.y, z + center.z));
    }

    public BattleParticipantBounds withCenter(final double x, final double y, final double z) {
        return offset(-center.x, -center.y, -center.z).offset(x, y, z);
    }

    public @Nullable RaycastResult raycast(final Vec3d start, final Vec3d end) {
        Vec3d res = null;
        Identifier resPart = null;
        for (final Part part : parts.values()) {
            final Optional<Vec3d> raycast = part.box.raycast(start, end);
            if (raycast.isPresent()) {
                final Vec3d p = raycast.get();
                if (res == null || p.squaredDistanceTo(start) < res.squaredDistanceTo(start)) {
                    res = p;
                    resPart = part.name;
                }
            }
        }
        if (res != null) {
            return new RaycastResult(resPart, res);
        }
        return null;
    }

    public @Nullable CollisionResult collision(final Box box) {
        Box intersection = null;
        Identifier intersectionPart = null;
        for (final Part part : parts.values()) {
            if (part.box.intersects(box)) {
                final Box inter = box.intersection(box);
                if ((intersection == null && inter.getAverageSideLength() != 0) || (intersection != null && intersection.getXLength() * intersection.getYLength() * intersection.getZLength() < inter.getXLength() * inter.getYLength() * inter.getZLength())) {
                    intersection = inter;
                    intersectionPart = part.name;
                }
            }
        }
        if (intersection != null) {
            return new CollisionResult(intersectionPart, intersection);
        }
        return null;
    }

    @NotNull
    @Override
    public Iterator<Part> iterator() {
        return Iterators.unmodifiableIterator(parts.values().iterator());
    }

    public static final class Part {
        public final Identifier name;
        public final Box box;

        private Part(final Identifier name, final Box box) {
            this.name = name;
            this.box = box;
        }
    }

    public record CollisionResult(Identifier part, Box intersection) {
    }

    public record RaycastResult(Identifier part, Vec3d hitPoint) {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Map<Identifier, Part> parts;

        public Builder() {
            parts = new Object2ReferenceOpenHashMap<>();
        }

        public Builder add(final Identifier name, final Box box) {
            parts.put(name, new Part(name, box));
            return this;
        }

        public BattleParticipantBounds build() {
            return build(Vec3d.ZERO);
        }

        public BattleParticipantBounds build(final Vec3d center) {
            if (parts.size() == 0) {
                throw new RuntimeException();
            }
            return new BattleParticipantBounds(new Object2ReferenceOpenHashMap<>(parts), center);
        }
    }
}
