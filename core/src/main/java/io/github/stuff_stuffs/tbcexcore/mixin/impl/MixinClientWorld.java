package io.github.stuff_stuffs.tbcexcore.mixin.impl;

import io.github.stuff_stuffs.tbcexcore.common.battle.world.ClientBattleWorld;
import io.github.stuff_stuffs.tbcexcore.mixin.api.ClientBattleWorldSupplier;
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
