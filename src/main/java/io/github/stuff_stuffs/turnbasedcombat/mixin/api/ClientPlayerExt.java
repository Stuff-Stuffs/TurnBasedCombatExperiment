package io.github.stuff_stuffs.turnbasedcombat.mixin.api;

public interface ClientPlayerExt {
    boolean tbcex_isCurrentTurn();

    void tbcex_setCurrentTurn(boolean val);
}
