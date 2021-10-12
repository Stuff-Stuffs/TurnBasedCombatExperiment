package io.github.stuff_stuffs.tbcexcore.common.battle;

public final class TurnTimer {
    //TODO per battle config?
    private final int turnTime;
    private int remaining;

    public TurnTimer(final int turnTime, final int remaining) {
        this.turnTime = turnTime;
        this.remaining = remaining;
        if (remaining < 0 || remaining > turnTime) {
            throw new RuntimeException();
        }
    }

    public boolean tick() {
        return remaining-- == 0;
    }

    public int getRemaining() {
        return remaining==-1?turnTime:remaining;
    }

    public int getMax() {
        return turnTime;
    }
}
