package io.github.stuff_stuffs.turnbasedcombat.mixin;

import io.github.stuff_stuffs.turnbasedcombat.common.api.MovementPlayer;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity implements MovementPlayer {
    @Shadow
    public Input input;
    private boolean movementDisabled = false;
    @Unique
    private Input stash;

    @Override
    public void setMovementDisabled_turn_based_combat(final boolean val) {
        movementDisabled = val;
        if (val && stash == null) {
            stash = input;
            input = new Input();
        } else {
            if (stash != null) {
                input = stash;
            }
            stash = null;
        }
    }

    @Override
    public Input getInput_turn_based_combat() {
        if (movementDisabled) {
            return stash;
        } else {
            return input;
        }
    }
}
