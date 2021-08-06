package io.github.stuff_stuffs.turnbasedcombat.mixin.impl;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.world.ClientBattleWorld;
import io.github.stuff_stuffs.turnbasedcombat.mixin.api.ClientBattleWorldSupplier;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientWorld.class)
public class MixinClientWorld implements ClientBattleWorldSupplier {
    private final ClientBattleWorld battleWorld = new ClientBattleWorld();

    @Override
    public ClientBattleWorld tbcex_getBattleWorld() {
        return battleWorld;
    }
}
