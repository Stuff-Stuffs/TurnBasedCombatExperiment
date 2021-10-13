package io.github.stuff_stuffs.tbcexcore.common.battle.participant;

import io.github.stuff_stuffs.tbcexutil.common.path.DjikstraPather;
import io.github.stuff_stuffs.tbcexutil.common.path.MovementType;
import io.github.stuff_stuffs.tbcexutil.common.path.Path;
import io.github.stuff_stuffs.tbcexutil.common.path.PathProcessor;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ParticipantPathGatherer {
    protected final List<MovementType> movementTypes;
    protected final List<PathProcessor> processors;

    protected ParticipantPathGatherer(final List<MovementType> movementTypes, final List<PathProcessor> processors) {
        this.movementTypes = movementTypes;
        this.processors = processors;
    }

    public List<Path> gather(final BattleParticipantStateView participant, final World world) {
        return DjikstraPather.INSTANCE.getPaths(participant.getPos(), participant.getFacing(), participant.getBounds(), participant.getBattleState().getBounds().getBox(), world, movementTypes, processors);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        protected final List<MovementType> movementTypes = new ArrayList<>();
        protected final List<PathProcessor> processors = new ArrayList<>();
        protected PathProcessor fallDamage = null;

        public Builder addMovementType(final MovementType movementType) {
            if (!movementTypes.contains(movementType)) {
                movementTypes.add(movementType);
            }
            return this;
        }

        public Builder addMovementTypes(final Collection<MovementType> movementTypes) {
            for (final MovementType movementType : movementTypes) {
                addMovementType(movementType);
            }
            return this;
        }

        public Builder addProcessor(final PathProcessor processor) {
            if (!processors.contains(processor)) {
                processors.add(processor);
            }
            return this;
        }

        public Builder fallDamage(final double fallHeight, final double costPerBlock) {
            fallDamage = PathProcessor.fallDamageProcessor(fallHeight, costPerBlock);
            return this;
        }

        public ParticipantPathGatherer build() {
            final List<MovementType> movementTypes = new ArrayList<>(this.movementTypes);
            final List<PathProcessor> processors = new ArrayList<>(this.processors);
            if (fallDamage != null) {
                processors.add(fallDamage);
            }
            return new ParticipantPathGatherer(movementTypes, processors);
        }
    }
}
