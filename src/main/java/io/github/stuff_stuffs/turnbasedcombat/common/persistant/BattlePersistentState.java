package io.github.stuff_stuffs.turnbasedcombat.common.persistant;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.data.ServerBattleWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

public final class BattlePersistentState extends PersistentState {
    private final ServerBattleWorld data;

    private BattlePersistentState(ServerWorld world) {
        this(new ServerBattleWorld(world));
    }

    private BattlePersistentState(final ServerBattleWorld data) {
        this.data = data;
    }

    public ServerBattleWorld getData() {
        return data;
    }


    //hacky
    @Override
    public boolean isDirty() {
        return true;
    }

    public static BattlePersistentState get(final PersistentStateManager manager, ServerWorld world) {
        return manager.getOrCreate(nbt -> fromNbt(nbt, world), () -> new BattlePersistentState(world), "tbcex_battles");
    }

    private static BattlePersistentState fromNbt(final NbtCompound compound, ServerWorld world) {
        return new BattlePersistentState(ServerBattleWorld.fromNbt(compound, world));
    }

    @Override
    public NbtCompound writeNbt(final NbtCompound nbt) {
        return data.writeNbt(nbt);
    }
}
