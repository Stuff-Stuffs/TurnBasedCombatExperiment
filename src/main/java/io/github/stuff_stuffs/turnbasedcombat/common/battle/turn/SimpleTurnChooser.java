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
            CodecUtil.UUID_CODEC.fieldOf("currentId").forGetter(chooser -> chooser.currentId),
            Codec.INT.fieldOf("roundNumber").forGetter(chooser -> chooser.roundNumber)
    ).apply(instance, SimpleTurnChooser::new));
    private static final UUID MIN = new UUID(Long.MIN_VALUE, Long.MIN_VALUE);
    private UUID currentId;
    private int roundNumber = 0;

    public SimpleTurnChooser() {
        currentId = MIN;
    }

    private SimpleTurnChooser(final UUID currentId, int roundNumber) {
        this.currentId = currentId;
        this.roundNumber = roundNumber;
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
        if(currentId.compareTo(maxId) >= 0) {
            currentId = minId;
            roundNumber++;
            return new TurnInfo(smallestView, roundNumber);
        }
        UUID smallestGreaterThan = null;
        EntityStateView smallestViewGreaterThan = null;
        for (EntityStateView participant : participants) {
            if(participant.getId().compareTo(currentId)>0) {
                if(smallestGreaterThan==null || participant.getId().compareTo(smallestGreaterThan)<0) {
                    smallestGreaterThan = participant.getId();
                    smallestViewGreaterThan = participant;
                }
            }
        }
        if(smallestViewGreaterThan==null) {
            throw new RuntimeException();
        }
        currentId = smallestGreaterThan;
        return new TurnInfo(smallestViewGreaterThan, roundNumber);
    }

    @Override
    public EntityStateView getCurrent(final Collection<? extends EntityStateView> participants, final BattleStateView state) {
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
        if(currentId.compareTo(maxId) >= 0) {
            return smallestView;
        }
        UUID smallestGreaterThan = null;
        EntityStateView smallestViewGreaterThan = null;
        for (EntityStateView participant : participants) {
            if(participant.getId().compareTo(currentId)>0) {
                if(smallestGreaterThan==null || participant.getId().compareTo(smallestGreaterThan)<0) {
                    smallestGreaterThan = participant.getId();
                    smallestViewGreaterThan = participant;
                }
            }
        }
        if(smallestViewGreaterThan==null) {
            throw new RuntimeException();
        }
        return smallestViewGreaterThan;
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
