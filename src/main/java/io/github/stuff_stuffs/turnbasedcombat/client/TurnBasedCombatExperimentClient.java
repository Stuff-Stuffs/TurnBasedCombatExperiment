package io.github.stuff_stuffs.turnbasedcombat.client;

import io.github.stuff_stuffs.turnbasedcombat.client.render.Render;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TurnBasedCombatExperimentClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Render.init();
    }
}
