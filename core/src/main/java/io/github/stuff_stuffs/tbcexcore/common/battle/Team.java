package io.github.stuff_stuffs.tbcexcore.common.battle;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public record Team(String teamId) {
    public static final Codec<Team> CODEC = Codec.STRING.xmap(Team::new, Team::teamId);

    public int getColour() {
        final Random random = new Random(teamId.hashCode());
        final float h = random.nextFloat();
        return MathHelper.hsvToRgb(h, 1, 1);
    }
}
