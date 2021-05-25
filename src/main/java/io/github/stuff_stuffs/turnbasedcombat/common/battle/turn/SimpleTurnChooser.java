package io.github.stuff_stuffs.turnbasedcombat.common.battle.turn;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityStateView;
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
    private int roundNumber = 0;

    public SimpleTurnChooser() {
        currentId = MIN;
    }

    private SimpleTurnChooser(final UUID state) {
        currentId = state;
    }

    @Override
    public TurnInfo nextTurn(final Collection<? extends EntityStateView> participants, final BattleStateView state) {
        UUID maxId = new UUID(Long.MIN_VALUE, Long.MIN_VALUE);
        UUID minId = new UUID(Long.MAX_VALUE, Long.MAX_VALUE);
        EntityStateView smallestView = null;
        for (final EntityStateView participant : participants) {
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
            roundNumber++;
        } else {
            UUID smallestGreaterThan = null;
            EntityStateView best = null;
            for (final EntityStateView participant : participants) {
                if (participant.getId().compareTo(currentId) > 0 && (smallestGreaterThan == null || participant.getId().compareTo(smallestGreaterThan) < 0)) {
                    smallestGreaterThan = participant.getId();
                    best = participant;
                }
            }
            if (smallestGreaterThan == null) {
                throw new RuntimeException();
            }
            final long lo = smallestGreaterThan.getLeastSignificantBits() + 1;
            long hi = smallestGreaterThan.getMostSignificantBits();
            if (lo == Long.MIN_VALUE) {
                hi = hi + 1;
            }
            currentId = new UUID(hi, lo);
            return new TurnInfo(best, roundNumber);
        }
        return new TurnInfo(smallestView, roundNumber);
    }

    @Override
    public EntityStateView getCurrent(final Collection<? extends EntityStateView> participants, final BattleStateView state) {
        UUID biggestLessThan = MIN;
        UUID biggest = MIN;
        EntityStateView biggestLessThanView = null;
        EntityStateView biggestView = null;
        for (final EntityStateView participant : participants) {
            if (participant.getId().compareTo(currentId) < 0) {
                if (participant.getId().compareTo(biggestLessThan) > 0) {
                    biggestLessThan = participant.getId();
                    biggestLessThanView = participant;
                }
            }
            if (participant.getId().compareTo(biggest) > 0) {
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
        roundNumber = 0;
    }

    @Override
    public TurnChooserTypeRegistry.Type getType() {
        return TurnChooserTypeRegistry.SIMPLE_TURN_CHOOSER_TYPE;
    }
}
