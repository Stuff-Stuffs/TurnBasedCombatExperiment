package io.github.stuff_stuffs.turnbasedcombat.common.battle.turn;

//TODO extract to interface
public final class TurnTimer {
    private final int maxTime;
    private int time;

    public TurnTimer(final int maxTime) {
        this.maxTime = maxTime;
    }

    public void tick() {
        time++;
    }

    public boolean shouldEndTurn() {
        return time >= maxTime;
    }

    public void reset() {
        time = 0;
    }
}
