package io.github.stuff_stuffs.tbcexcore.common.battle.participant;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats.BattleParticipantStat;

public final class EnergyTracker {
    private final BattleParticipantState state;
    private double energyRemaining;

    public EnergyTracker(final BattleParticipantState state) {
        this.state = state;
    }

    public EnergyTracker(final BattleParticipantState state, final double energyRemaining) {
        this(state);
        this.energyRemaining = energyRemaining;
    }

    public void use(final double amount) {
        energyRemaining -= amount;
        if (energyRemaining < 0) {
            //TODO
            throw new RuntimeException();
        }
    }

    public void reset() {
        energyRemaining = state.getStat(BattleParticipantStat.ENERGY_PER_TURN_STAT);
    }

    public double getEnergyRemaining() {
        return energyRemaining;
    }
}
