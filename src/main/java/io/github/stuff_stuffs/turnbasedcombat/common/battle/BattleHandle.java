package io.github.stuff_stuffs.turnbasedcombat.common.battle;

//TODO dimension id?
public final class BattleHandle {
    public final int id;

    public BattleHandle(final int id) {
        this.id = id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BattleHandle)) {
            return false;
        }

        final BattleHandle that = (BattleHandle) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
