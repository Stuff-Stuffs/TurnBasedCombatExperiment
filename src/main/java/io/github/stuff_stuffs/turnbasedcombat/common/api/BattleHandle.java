package io.github.stuff_stuffs.turnbasedcombat.common.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public final class BattleHandle {
    private final long id;
    private final RegistryKey<World> dimension;

    private BattleHandle(final long id, final RegistryKey<World> dimension) {
        this.id = id;
        this.dimension = dimension;
    }

    public RegistryKey<World> getDimension() {
        return dimension;
    }

    public long getId() {
        return id;
    }

    public CompoundTag toNbt() {
        final CompoundTag tag = new CompoundTag();
        tag.putLong("id", getId());
        tag.putString("dimension", getDimension().getValue().toString());
        return tag;
    }

    public void toBuf(final PacketByteBuf buf) {
        buf.writeIdentifier(dimension.getValue());
        buf.writeVarLong(id);
    }

    public static BattleHandle fromBuf(final PacketByteBuf buf) {
        return create(RegistryKey.of(Registry.DIMENSION, buf.readIdentifier()), buf.readVarLong());
    }

    public static BattleHandle fromTag(final CompoundTag tag) {
        return create(RegistryKey.of(Registry.DIMENSION, new Identifier(tag.getString("dimension"))), tag.getLong("id"));
    }

    public static BattleHandle create(final World world, final long id) {
        return create(world.getRegistryKey(), id);
    }

    public static BattleHandle create(final RegistryKey<World> dimension, final long id) {
        return new BattleHandle(id, dimension);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final BattleHandle that = (BattleHandle) o;

        if (id != that.id) {
            return false;
        }
        return dimension.equals(that.dimension);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + dimension.hashCode();
        return result;
    }
}
