package io.github.stuff_stuffs.turnbasedcombat.common.battle.turn;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipantView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;

import java.util.Collection;

public final class SimpleTurnChooser implements TurnChooser {
    public static final Codec<SimpleTurnChooser> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("state").forGetter(chooser -> chooser.currentId)
    ).apply(instance, SimpleTurnChooser::new));
    private int currentId;

    public SimpleTurnChooser() {
        currentId = -1;
    }

    private SimpleTurnChooser(final int state) {
        currentId = state;
    }

    @Override
    public BattleParticipantView choose(final Collection<? extends BattleParticipantView> participants, final BattleStateView state) {
        int maxId = -Integer.MAX_VALUE;
        int minId = Integer.MAX_VALUE;
        BattleParticipantView smallestView = null;
        for (final BattleParticipantView participant : participants) {
            maxId = Math.max(maxId, participant.getId());
            if (participant.getId() < minId) {
                minId = participant.getId();
                smallestView = participant;
            }
        }
        if (currentId > maxId) {
            currentId = minId - 1;
        } else {
            int smallestGreaterThan = -1;
            BattleParticipantView best = null;
            for (final BattleParticipantView participant : participants) {
                if (participant.getId() > currentId && participant.getId() < smallestGreaterThan) {
                    smallestGreaterThan = participant.getId();
                    best = participant;
                }
            }
            currentId = smallestGreaterThan + 1;
            return best;
        }
        return smallestView;
    }

    @Override
    public BattleParticipantView getCurrent(final Collection<? extends BattleParticipantView> participants, final BattleStateView state) {
        int biggestLessThan = -Integer.MAX_VALUE;
        int biggest = -Integer.MAX_VALUE;
        BattleParticipantView biggestLessThanView = null;
        BattleParticipantView biggestView = null;
        for (final BattleParticipantView participant : participants) {
            if (participant.getId() < currentId) {
                if (participant.getId() > biggestLessThan) {
                    biggestLessThan = participant.getId();
                    biggestLessThanView = participant;
                }
            }
            if (participant.getId() > biggest) {
                biggest = participant.getId();
                biggestView = participant;
            }
        }
        if (biggestLessThanView == null) {
            return biggestView;
        }
        return biggestLessThanView;
    }

    @Override
    public void reset() {
        currentId = -1;
    }

    @Override
    public TurnChooserTypeRegistry.Type getType() {
        return TurnChooserTypeRegistry.SIMPLE_TURN_CHOOSER_TYPE;
    }
}
