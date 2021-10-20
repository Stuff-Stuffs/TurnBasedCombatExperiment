package io.github.stuff_stuffs.tbcexutil.common.path;

import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import io.github.stuff_stuffs.tbcexutil.common.HorizontalDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.List;

public interface Pather {
    List<Path> getPaths(BlockPos pos, BattleParticipantBounds bounds, Box pathBounds, World world, Collection<MovementType> movementTypes, Collection<PathProcessor> processors);
}
