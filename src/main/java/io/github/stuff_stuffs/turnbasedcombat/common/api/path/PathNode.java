package io.github.stuff_stuffs.turnbasedcombat.common.api.path;

import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class PathNode {
    private final BlockPos pos;
    private final int distance;
    private final @Nullable PathNode previous;

    public PathNode(final BlockPos pos, final int distance, @Nullable final PathNode previous) {
        this.pos = pos;
        this.distance = distance;
        this.previous = previous;
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getDistance() {
        return distance;
    }

    public @Nullable PathNode getPrevious() {
        return previous;
    }
}
