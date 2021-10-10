package io.github.stuff_stuffs.tbcexutil.common.path;

import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import io.github.stuff_stuffs.tbcexutil.common.HorizontalDirection;
import io.github.stuff_stuffs.tbcexutil.common.WorldShapeCache;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class DjikstraPather implements Pather {
    private static final class Node implements Comparable<Node> {
        private final double euclid;
        private @Nullable Node prev;
        private final BlockPos pos;
        private Movement movement;
        private double cost;

        private Node(final BlockPos start, @Nullable final Node prev, final BlockPos pos, final Movement movement, final double cost) {
            final BlockPos delta = pos.subtract(start);
            euclid = delta.getX() * delta.getX() + delta.getY() * delta.getY() + delta.getZ() * delta.getZ();
            this.prev = prev;
            this.pos = pos;
            this.movement = movement;
            this.cost = cost;
        }

        @Override
        public int compareTo(@NotNull final DjikstraPather.Node o) {
            int compare = Double.compare(cost, o.cost);
            if (compare != 0) {
                return compare;
            }
            compare = Double.compare(euclid, o.euclid);
            if (compare != 0) {
                return compare;
            }
            return pos.compareTo(o.pos);
        }
    }

    @Override
    public List<Path> getPaths(final BlockPos startPos, final HorizontalDirection startDir, final BattleParticipantBounds bounds, final Box pathBounds, final World world, final Collection<MovementType> movementTypes) {
        final PathHeap<Node> queue = new PathHeap<>(128);
        final Map<BlockPos, Node> nodes = new Object2ReferenceOpenHashMap<>(128);
        final Node start = new Node(startPos, null, startPos, null, 0);
        nodes.put(startPos, start);
        queue.enqueue(start);
        final WorldShapeCache shapeCache = new WorldShapeCache(world, null, pathBounds, 1024);
        while (!queue.isEmpty()) {
            final Node node = queue.dequeue();
            final BattleParticipantBounds moved = bounds.offset(node.pos.getX() - startPos.getX(), node.pos.getY() - startPos.getY(), node.pos.getZ() - startPos.getZ());
            final HorizontalDirection dir = node.movement == null ? startDir : node.movement.getRotation(node.movement.getLength());
            for (final MovementType movementType : movementTypes) {
                final Movement movement = movementType.modify(moved, dir, node.pos, pathBounds, world, shapeCache);
                if (movement != null) {
                    final Node next = nodes.computeIfAbsent(movement.getEndPos(), pos -> {
                        final Node n = new Node(startPos, node, pos, movement, node.cost + movement.getCost());
                        if (pathBounds.contains(n.pos.getX(), n.pos.getY(), n.pos.getZ())) {
                            queue.enqueue(n);
                            return n;
                        } else {
                            return null;
                        }
                    });
                    if (next != null && node.cost + movement.getCost() < next.cost) {
                        next.cost = node.cost + movement.getCost();
                        next.movement = movement;
                        next.prev = node;
                        queue.decreasePriority(next);
                    }
                }
            }
        }
        final List<Path> paths = new ArrayList<>(nodes.size());
        for (Node node : nodes.values()) {
            if (node.prev != null && node.movement.isValidEnding()) {
                final List<Movement> movements = new ArrayList<>(16);
                while (node.prev != null) {
                    movements.add(0, node.movement);
                    node = node.prev;
                }
                paths.add(new Path(movements));
            }
        }
        return paths;
    }
}
