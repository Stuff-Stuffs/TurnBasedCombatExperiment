package io.github.stuff_stuffs.tbcexcharacter.common;

import io.github.stuff_stuffs.tbcexcharacter.common.entity.stat.StatSources;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class TBCExCharacter implements ModInitializer {
    public static final String MOD_ID = "tbcexcharacter";

    @Override
    public void onInitialize() {
        StatSources.init();
    }

    public static Identifier createId(final String path) {
        return new Identifier(MOD_ID, path);
    }
}
