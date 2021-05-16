package io.github.stuff_stuffs.turnbasedcombat.common.api;

public interface BattleLog {
    int size();

    BattleAction getAction(int index);

    void push(BattleAction action);
}
