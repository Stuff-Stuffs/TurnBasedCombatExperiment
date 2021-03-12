package io.github.stuff_stuffs.turnbasedcombat.common.impl.component;

import io.github.stuff_stuffs.turnbasedcombat.common.api.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.component.BattleWorldComponent;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.api.ClientBattleImpl;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ClientBattleWorldComponent implements BattleWorldComponent {
    private final Map<BattleHandle, ClientBattleImpl> activeBattles;
    private final ClientWorld clientWorld;

    public ClientBattleWorldComponent(final ClientWorld clientWorld) {
        this.clientWorld = clientWorld;
        activeBattles = new Object2ReferenceOpenHashMap<>();
    }

    @Override
    public @Nullable Battle fromHandle(final BattleHandle handle) {
        return activeBattles.get(handle);
    }

    public void addBattle(final BattleHandle handle, final ClientBattleImpl battle) {
        if (activeBattles.put(handle, battle) != null) {
            throw new RuntimeException();
        }
    }

    public void removeBattle(final BattleHandle handle) {
        activeBattles.remove(handle);
    }

    @Override
    public void readFromNbt(final CompoundTag tag) {
    }

    @Override
    public void writeToNbt(final CompoundTag tag) {
    }

    @Override
    public void tick() {
        for (final ClientBattleImpl battle : activeBattles.values()) {
            battle.tick();
        }
    }
}
