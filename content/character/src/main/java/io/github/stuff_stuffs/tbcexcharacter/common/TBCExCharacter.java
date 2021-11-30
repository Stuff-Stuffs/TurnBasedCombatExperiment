package io.github.stuff_stuffs.tbcexcharacter.common;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class TBCExCharacter implements ModInitializer {
    public static final String MOD_ID = "tbcexcharacter";

    @Override
    public void onInitialize() {

    }

    public static Identifier createId(final String path) {
        return new Identifier(MOD_ID, path);
    }
}
