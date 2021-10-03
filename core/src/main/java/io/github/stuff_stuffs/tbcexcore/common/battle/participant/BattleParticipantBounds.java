package io.github.stuff_stuffs.tbcexcore.common.battle.participant;

import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexutil.common.CodecUtil;
import io.github.stuff_stuffs.tbcexutil.common.HorizontalDirection;
import io.github.stuff_stuffs.tbcexutil.common.HorizontalRotation;
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
    public static final Codec<BattleParticipantBounds> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.unboundedMap(Identifier.CODEC, PART_CODEC).fieldOf("parts").forGetter(bounds -> bounds.parts), CodecUtil.VEC3D_CODEC.fieldOf("center").forGetter(bounds -> bounds.center), HorizontalDirection.CODEC.fieldOf("direction").forGetter(bounds -> bounds.direction)).apply(instance, BattleParticipantBounds::new));

    private final Map<Identifier, Part> parts;
    private final Vec3d center;
    private final HorizontalDirection direction;
    private final Map<HorizontalDirection, BattleParticipantBounds> rotationCache;

    private BattleParticipantBounds(final Map<Identifier, Part> parts, final Vec3d center) {
        this.parts = parts;
        direction = HorizontalDirection.NORTH;
        rotationCache = new EnumMap<>(HorizontalDirection.class);
        rotationCache.put(HorizontalDirection.NORTH, this);
        this.center = center;
    }

    private BattleParticipantBounds(final Map<Identifier, Part> parts, final Vec3d center, final HorizontalDirection dir) {
        this.parts = parts;
        this.center = center;
        direction = dir;
        rotationCache = new EnumMap<>(HorizontalDirection.class);
        rotationCache.put(HorizontalDirection.NORTH, this);
    }

    private BattleParticipantBounds(final Map<Identifier, Part> parts, final Vec3d center, final HorizontalDirection dir, final Map<HorizontalDirection, BattleParticipantBounds> rotationCache) {
        this.parts = parts;
        this.center = center;
        direction = dir;
        this.rotationCache = rotationCache;
    }

    public BattleParticipantBounds offset(final double x, final double y, final double z) {
        final Builder builder = builder();
        for (final Part part : parts.values()) {
            builder.add(part.name, part.box.offset(x, y, z));
        }
        return builder.build();
    }

    public BattleParticipantBounds withRotation(final HorizontalDirection dir) {
        if (dir == direction) {
            return this;
        } else {
            return rotationCache.computeIfAbsent(dir, this::computeRotation);
        }
    }

    private BattleParticipantBounds computeRotation(final HorizontalDirection direction) {
        final HorizontalRotation rotation = HorizontalRotation.compute(this.direction, direction);
        if (rotation == HorizontalRotation.R0) {
            return this;
        }
        final Map<Identifier, Part> parts = new Object2ReferenceOpenHashMap<>();
        for (final Part part : this.parts.values()) {
            parts.put(part.name, new Part(part.name, rotation.rotateBox(part.box.offset(-center.x, -center.y, -center.z)).offset(center)));
        }
        return new BattleParticipantBounds(parts, center, direction, rotationCache);
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
