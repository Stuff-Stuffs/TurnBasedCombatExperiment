package io.github.stuff_stuffs.tbcexanimation.common;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class TBCExAnimation implements ModInitializer {
    public static final String MOD_ID = "tbcex_animation";

    @Override
    public void onInitialize() {

    }

    public static Identifier createId(final String path) {
        return new Identifier(MOD_ID, path);
    }
}
