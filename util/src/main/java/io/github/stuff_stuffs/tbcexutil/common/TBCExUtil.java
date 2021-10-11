package io.github.stuff_stuffs.tbcexutil.common;

import io.github.stuff_stuffs.tbcexutil.common.path.MovementTypes;
import net.fabricmc.api.ModInitializer;

public final class TBCExUtil implements ModInitializer {
    @Override
    public void onInitialize() {
        MovementTypes.init();
    }
}
