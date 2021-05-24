package io.github.stuff_stuffs.turnbasedcombat.client.battle.data;

import io.github.stuff_stuffs.turnbasedcombat.client.network.RequestBattleUpdateSender;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleTimeline;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.data.BattleWorld;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.BattleEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.turn.TurnChooser;
import io.github.stuff_stuffs.turnbasedcombat.mixin.api.ClientBattleWorldProvider;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ClientBattleWorld implements BattleWorld {
    private final Int2ReferenceMap<Battle> battles;
    private final IntSet requestedUpdates;

    public ClientBattleWorld() {
        battles = new Int2ReferenceOpenHashMap<>();
        requestedUpdates = new IntOpenHashSet();
    }

    public Battle create(final BattleHandle handle, final TurnChooser chooser) {
        if (battles.get(handle.id()) != null) {
            throw new RuntimeException();
        }
        final Battle battle = new Battle(handle.id(), chooser, new BattleTimeline());
        battles.put(handle.id(), battle);
        return battle;
    }

    @Override
    public @Nullable Battle getBattle(final BattleHandle handle) {
        final Battle battle = battles.get(handle.id());
        if (requestedUpdates.add(handle.id())) {
            RequestBattleUpdateSender.send(handle, battle != null ? battle.getTimeline().size() : 0, battle == null);
        }
        return battle;
    }

    @Override
    public @Nullable Battle getBattle(final BattleEntity entity) {
        final UUID id = ((Entity) entity).getUuid();
        for (final Battle battle : battles.values()) {
            if (battle.getStateView().contains(id)) {
                return battle;
            }
        }
        return null;
    }

    @Override
    public void join(final BattleEntity entity, final BattleHandle handle) {
        throw new UnsupportedOperationException("Should not join battle client side!");
    }

    @Override
    public BattleHandle create() {
        throw new UnsupportedOperationException("Should not create battle client side!");
    }

    public static ClientBattleWorld get(final ClientWorld world) {
        return ((ClientBattleWorldProvider) world).tbcex_getBattleWorld();
    }

    public void tick() {
        requestedUpdates.clear();
    }
}
