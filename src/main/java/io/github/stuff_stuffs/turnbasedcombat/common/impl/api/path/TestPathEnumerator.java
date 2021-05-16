package io.github.stuff_stuffs.turnbasedcombat.common.impl.api.path;

import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleBounds;
import io.github.stuff_stuffs.turnbasedcombat.common.api.path.PathNode;
import io.github.stuff_stuffs.turnbasedcombat.common.util.WorldShapeCache;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Collection;

public class TestPathEnumerator extends AbstractPathEnumerator {
    public static final TestPathEnumerator INSTANCE = new TestPathEnumerator();

    private TestPathEnumerator() {
    }

    @Override
    protected Collection<PathNode> getAdjacent(final PathNode prev, final WorldShapeCache cache, final BattleBounds bounds) {
        final BlockPos pos = prev.getPos();
        final Collection<PathNode> adjacent = new ObjectArrayList<>(4);
        for (final Direction dir : Direction.Type.HORIZONTAL) {
            final BlockPos offset = pos.offset(dir);
            assert cache.getEntity() != null;
            if (checkPosition(cache.getEntity(), offset, bounds, cache) && checkFloorPosition(cache.getEntity(), offset, bounds, cache)) {
                adjacent.add(new PathNode(offset, prev.getDistance() + 1, prev));
            }
        }
        return adjacent;
    }
}
