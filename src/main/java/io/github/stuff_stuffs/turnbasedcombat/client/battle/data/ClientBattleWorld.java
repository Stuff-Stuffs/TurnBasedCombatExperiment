package io.github.stuff_stuffs.turnbasedcombat.client.battle.data;

import io.github.stuff_stuffs.turnbasedcombat.client.network.RequestBattleUpdateSender;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleTimeline;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.data.BattleWorld;
import io.github.stuff_stuffs.turnbasedcombat.mixin.api.ClientBattleWorldProvider;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;

public class ClientBattleWorld implements BattleWorld {
    private final Int2ReferenceMap<Battle> battles;

    public ClientBattleWorld() {
        battles = new Int2ReferenceOpenHashMap<>();
    }

    public Battle create(final BattleHandle handle) {
        if (battles.get(handle.id) != null) {
            throw new RuntimeException();
        }
        final Battle battle = new Battle(handle.id, new BattleTimeline());
        battles.put(handle.id, battle);
        return battle;
    }

    @Override
    public @Nullable Battle getBattle(final BattleHandle handle) {
        final Battle battle = battles.get(handle.id);
        RequestBattleUpdateSender.send(handle, battle != null ? battle.getTimeline().size() : 0);
        return battle;
    }

    public static ClientBattleWorld get(final ClientWorld world) {
        return ((ClientBattleWorldProvider) world).tbcex_getBattleWorld();
    }
}
