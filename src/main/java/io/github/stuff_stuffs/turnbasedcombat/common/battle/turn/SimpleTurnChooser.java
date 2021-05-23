package io.github.stuff_stuffs.turnbasedcombat.common.battle.turn;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipant;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.util.CodecUtil;

import java.util.Collection;
import java.util.UUID;

//TODO redo this mess
public final class SimpleTurnChooser implements TurnChooser {
    public static final Codec<SimpleTurnChooser> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CodecUtil.UUID_CODEC.fieldOf("state").forGetter(chooser -> chooser.currentId)
    ).apply(instance, SimpleTurnChooser::new));
    private static final UUID MIN = new UUID(Long.MIN_VALUE, Long.MIN_VALUE);
    private UUID currentId;

    public SimpleTurnChooser() {
        currentId = MIN;
    }

    private SimpleTurnChooser(final UUID state) {
        currentId = state;
    }

    @Override
    public BattleParticipant choose(final Collection<BattleParticipant> participants, final BattleStateView state) {
        UUID maxId = new UUID(Long.MIN_VALUE, Long.MIN_VALUE);
        UUID minId = new UUID(Long.MAX_VALUE, Long.MAX_VALUE);
        BattleParticipant smallestView = null;
        for (final BattleParticipant participant : participants) {
            if (maxId.compareTo(participant.id()) < 0) {
                maxId = participant.id();
            }
            if (minId.compareTo(participant.id()) > 0) {
                minId = participant.id();
                smallestView = participant;
            }
        }
        if (currentId.compareTo(maxId) < 0) {
            currentId = minId;
        } else {
            UUID smallestGreaterThan = null;
            BattleParticipant best = null;
            for (final BattleParticipant participant : participants) {
                if (participant.id().compareTo(currentId) > 0 && (smallestGreaterThan == null || participant.id().compareTo(smallestGreaterThan) < 0)) {
                    smallestGreaterThan = participant.id();
                    best = participant;
                }
            }
            if(smallestGreaterThan==null) {
                throw new RuntimeException();
            }
            long lo = smallestGreaterThan.getLeastSignificantBits() + 1;
            long hi = smallestGreaterThan.getMostSignificantBits();
            if(lo == Long.MIN_VALUE) {
                hi = hi + 1;
            }
            currentId = new UUID(hi, lo);
            return best;
        }
        return smallestView;
    }

    @Override
    public BattleParticipant getCurrent(final Collection<BattleParticipant> participants, final BattleStateView state) {
        UUID biggestLessThan = MIN;
        UUID biggest = MIN;
        BattleParticipant biggestLessThanView = null;
        BattleParticipant biggestView = null;
        for (final BattleParticipant participant : participants) {
            if (participant.id().compareTo(currentId)<0) {
                if (participant.id().compareTo(biggestLessThan)>0) {
                    biggestLessThan = participant.id();
                    biggestLessThanView = participant;
                }
            }
            if (participant.id().compareTo(biggest)>0) {
                biggest = participant.id();
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
        currentId = MIN;
    }

    @Override
    public TurnChooserTypeRegistry.Type getType() {
        return TurnChooserTypeRegistry.SIMPLE_TURN_CHOOSER_TYPE;
    }
}
