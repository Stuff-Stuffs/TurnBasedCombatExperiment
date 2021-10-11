package io.github.stuff_stuffs.tbcexutil.common.path;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public final class Path {
    public static final Codec<Path> CODEC = Codec.list(Movement.CODEC).xmap(Path::new, Path::getMovements);
    private final List<Movement> movements;
    private final BlockPos start;
    private final BlockPos end;
    private final double cost;

    public Path(final List<Movement> movements) {
        this.movements = movements;
        start = movements.get(0).getStartPos();
        end = movements.get(movements.size() - 1).getEndPos();
        double c = 0;
        for (final Movement movement : movements) {
            c += movement.getCost();
        }
        cost = c;
    }

    public List<Movement> getMovements() {
        return movements;
    }

    public BlockPos getStart() {
        return start;
    }

    public BlockPos getEnd() {
        return end;
    }

    public double getCost() {
        return cost;
    }
}
