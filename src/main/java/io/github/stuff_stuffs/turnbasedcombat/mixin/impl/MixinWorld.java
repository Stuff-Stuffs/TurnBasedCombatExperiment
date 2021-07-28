package io.github.stuff_stuffs.turnbasedcombat.mixin.impl;

import io.github.stuff_stuffs.turnbasedcombat.mixin.api.BattleWorldSupplier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(World.class)
public abstract class MixinWorld implements BattleWorldSupplier {
}
