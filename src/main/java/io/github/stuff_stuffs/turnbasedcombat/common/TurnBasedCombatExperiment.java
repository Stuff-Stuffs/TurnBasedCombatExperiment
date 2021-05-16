package io.github.stuff_stuffs.turnbasedcombat.common;

import io.github.stuff_stuffs.turnbasedcombat.common.entity.EntityTypes;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class TurnBasedCombatExperiment implements ModInitializer {
    public static final String MOD_ID = "turn_based_combat";

    public static Identifier createId(final String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        EntityTypes.init();
    }
}
