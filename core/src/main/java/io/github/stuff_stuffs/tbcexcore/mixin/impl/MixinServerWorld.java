package io.github.stuff_stuffs.tbcexcore.mixin.impl;

import io.github.stuff_stuffs.tbcexcore.common.battle.world.BattleWorld;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerWorld.class)
public class MixinServerWorld implements BattleWorldSupplier {
    @Shadow
    @Final
    private ServerChunkManager serverChunkManager;

    @Override
    public BattleWorld tbcex_getBattleWorld() {
        return ((BattleWorldSupplier) serverChunkManager).tbcex_getBattleWorld();
    }
}
