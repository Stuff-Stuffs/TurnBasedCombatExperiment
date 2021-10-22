package io.github.stuff_stuffs.tbcexcore.common.battle.participant;

import io.github.stuff_stuffs.tbcexutil.common.path.*;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ParticipantPathGatherer {
    public static final PathPostProcessor DEFAULT = path -> {
        DoubleList costs = new DoubleArrayList(path.getMovements().size());
        for (Movement movement : path.getMovements()) {
            costs.add(movement.getCost() / 3.0);
        }
        return new BattlePath(path, costs);
    };
    protected final List<MovementType> movementTypes;
    protected final List<PathProcessor> processors;
    protected final PathPostProcessor postProcessor;

    protected ParticipantPathGatherer(final List<MovementType> movementTypes, final List<PathProcessor> processors, final PathPostProcessor postProcessor) {
        this.movementTypes = movementTypes;
        this.processors = processors;
        this.postProcessor = postProcessor;
    }

    public List<BattlePath> gather(final BattleParticipantStateView participant, final World world) {
        final List<Path> paths = DjikstraPather.INSTANCE.getPaths(participant.getPos(), participant.getBounds(), participant.getBattleState().getBounds().getBox(), world, movementTypes, processors);
        final List<BattlePath> battlePaths = new ArrayList<>(paths.size());
        for (final Path path : paths) {
            battlePaths.add(postProcessor.processPath(path));
        }
        return battlePaths;
    }

    public static Builder builder() {
        return new Builder();
    }

    public interface PathPostProcessor {
        BattlePath processPath(Path path);
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

        public ParticipantPathGatherer build(final PathPostProcessor postProcessor) {
            final List<MovementType> movementTypes = new ArrayList<>(this.movementTypes);
            final List<PathProcessor> processors = new ArrayList<>(this.processors);
            if (fallDamage != null) {
                processors.add(fallDamage);
            }
            return new ParticipantPathGatherer(movementTypes, processors, postProcessor);
        }
    }
}
