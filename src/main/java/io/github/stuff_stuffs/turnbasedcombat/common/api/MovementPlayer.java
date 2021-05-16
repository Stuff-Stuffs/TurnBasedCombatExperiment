package io.github.stuff_stuffs.turnbasedcombat.common.api;

import net.minecraft.client.input.Input;

public interface MovementPlayer {
    void setMovementDisabled_turn_based_combat(boolean val);

    Input getInput_turn_based_combat();
}
