package io.github.stuff_stuffs.turnbasedcombat.common.battle.turn;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipantView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.util.CodecUtil;

import java.util.Collection;
import java.util.UUID;

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
    public BattleParticipantView choose(final Collection<? extends BattleParticipantView> participants, final BattleStateView state) {
        UUID maxId = new UUID(Long.MIN_VALUE, Long.MIN_VALUE);
        UUID minId = new UUID(Long.MAX_VALUE, Long.MAX_VALUE);
        BattleParticipantView smallestView = null;
        for (final BattleParticipantView participant : participants) {
            if (maxId.compareTo(participant.getId()) < 0) {
                maxId = participant.getId();
            }
            if (minId.compareTo(participant.getId()) > 0) {
                minId = participant.getId();
                smallestView = participant;
            }
        }
        if (currentId.compareTo(maxId) < 0) {
            currentId = minId;
        } else {
            UUID smallestGreaterThan = null;
            BattleParticipantView best = null;
            for (final BattleParticipantView participant : participants) {
                if (participant.getId().compareTo(currentId) > 0 && (smallestGreaterThan == null || participant.getId().compareTo(smallestGreaterThan) < 0)) {
                    smallestGreaterThan = participant.getId();
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
    public BattleParticipantView getCurrent(final Collection<? extends BattleParticipantView> participants, final BattleStateView state) {
        UUID biggestLessThan = MIN;
        UUID biggest = MIN;
        BattleParticipantView biggestLessThanView = null;
        BattleParticipantView biggestView = null;
        for (final BattleParticipantView participant : participants) {
            if (participant.getId().compareTo(currentId)<0) {
                if (participant.getId().compareTo(biggestLessThan)>0) {
                    biggestLessThan = participant.getId();
                    biggestLessThanView = participant;
                }
            }
            if (participant.getId().compareTo(biggest)>0) {
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
        currentId = MIN;
    }

    @Override
    public TurnChooserTypeRegistry.Type getType() {
        return TurnChooserTypeRegistry.SIMPLE_TURN_CHOOSER_TYPE;
    }
}
