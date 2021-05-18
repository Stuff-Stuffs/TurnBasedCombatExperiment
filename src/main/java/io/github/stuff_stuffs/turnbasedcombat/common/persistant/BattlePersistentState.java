package io.github.stuff_stuffs.turnbasedcombat.common.persistant;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.data.ServerBattleWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

public final class BattlePersistentState extends PersistentState {
    private final ServerBattleWorld data;

    private BattlePersistentState() {
        this(new ServerBattleWorld());
    }

    private BattlePersistentState(final ServerBattleWorld data) {
        this.data = data;
    }

    public ServerBattleWorld getData() {
        return data;
    }

    public static BattlePersistentState get(final PersistentStateManager manager) {
        return manager.getOrCreate(BattlePersistentState::fromNbt, BattlePersistentState::new, "battles");
    }

    private static BattlePersistentState fromNbt(final NbtCompound compound) {
        return new BattlePersistentState(ServerBattleWorld.fromNbt(compound));
    }

    @Override
    public NbtCompound writeNbt(final NbtCompound nbt) {
        return data.writeNbt(nbt);
    }
}
