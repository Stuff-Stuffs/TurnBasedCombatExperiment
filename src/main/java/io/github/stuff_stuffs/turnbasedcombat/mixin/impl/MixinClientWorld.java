package io.github.stuff_stuffs.turnbasedcombat.mixin.impl;

import io.github.stuff_stuffs.turnbasedcombat.client.battle.data.ClientBattleWorld;
import io.github.stuff_stuffs.turnbasedcombat.mixin.api.ClientBattleWorldProvider;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientWorld.class)
public class MixinClientWorld implements ClientBattleWorldProvider {
    @Unique
    private final ClientBattleWorld battleWorld = new ClientBattleWorld();

    @Override
    public ClientBattleWorld tbcex_getBattleWorld() {
        return battleWorld;
    }
}
