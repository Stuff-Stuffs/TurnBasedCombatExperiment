package io.github.stuff_stuffs.turnbasedcombat.client;

import io.github.stuff_stuffs.turnbasedcombat.client.render.Render;
import io.github.stuff_stuffs.turnbasedcombat.mixin.api.ClientBattleWorldSupplier;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

@Environment(EnvType.CLIENT)
public class TurnBasedCombatExperimentClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Render.init();
        ClientTickEvents.START_WORLD_TICK.register(world -> ((ClientBattleWorldSupplier) world).tbcex_getBattleWorld().tick());
    }
}
