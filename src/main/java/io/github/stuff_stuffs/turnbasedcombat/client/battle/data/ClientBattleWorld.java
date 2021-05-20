package io.github.stuff_stuffs.turnbasedcombat.client.battle.data;

import io.github.stuff_stuffs.turnbasedcombat.client.network.RequestBattleUpdateSender;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.*;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.data.BattleWorld;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.turn.TurnChooser;
import io.github.stuff_stuffs.turnbasedcombat.mixin.api.ClientBattleWorldProvider;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class ClientBattleWorld implements BattleWorld {
    private final Int2ReferenceMap<Battle> battles;
    private final IntSet requestedUpdates;

    public ClientBattleWorld() {
        battles = new Int2ReferenceOpenHashMap<>();
        requestedUpdates = new IntOpenHashSet();
    }

    public Battle create(final BattleHandle handle, final TurnChooser chooser) {
        if (battles.get(handle.id) != null) {
            throw new RuntimeException();
        }
        final Battle battle = new Battle(handle.id, chooser, new BattleTimeline());
        battles.put(handle.id, battle);
        return battle;
    }

    @Override
    public @Nullable Battle getBattle(final BattleHandle handle) {
        final Battle battle = battles.get(handle.id);
        if (requestedUpdates.add(handle.id)) {
            RequestBattleUpdateSender.send(handle, battle != null ? battle.getTimeline().size() : 0, battle == null);
        }
        return battle;
    }

    @Override
    public BattleParticipant create(final Text name, final Team team) {
        throw new UnsupportedOperationException("Should not be creating participants on the client!");
    }

    public static ClientBattleWorld get(final ClientWorld world) {
        return ((ClientBattleWorldProvider) world).tbcex_getBattleWorld();
    }

    public void tick() {
        requestedUpdates.clear();
    }
}
