package io.github.stuff_stuffs.tbcexcore.common.battle.participant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexutil.common.path.Path;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;

import java.util.List;

public final class BattlePath {
    public static final Codec<BattlePath> CODEC = RecordCodecBuilder.create(instance -> instance.group(Path.CODEC.fieldOf("path").forGetter(path -> path.path), Codec.list(Codec.DOUBLE).fieldOf("costs").forGetter(path -> path.costs)).apply(instance, BattlePath::new));

    private final Path path;
    private final DoubleList costs;
    private final double cost;

    public BattlePath(final Path path, final List<Double> costs) {
        this(path, new DoubleArrayList(costs));
    }

    public BattlePath(final Path path, final DoubleList costs) {
        this.path = path;
        this.costs = costs;
        double c = 0;
        final DoubleListIterator iterator = costs.iterator();
        while (iterator.hasNext()) {
            c += iterator.nextDouble();
        }
        cost = c;
    }

    public Path getPath() {
        return path;
    }

    public double getCostAtMovement(final int index) {
        return costs.getDouble(index);
    }

    public double getCost() {
        return cost;
    }
}
