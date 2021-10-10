package io.github.stuff_stuffs.tbcexutil.common.path;

import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import io.github.stuff_stuffs.tbcexutil.common.HorizontalDirection;
import io.github.stuff_stuffs.tbcexutil.common.WorldShapeCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface MovementType {
    @Nullable Movement modify(BattleParticipantBounds bounds, HorizontalDirection dir, BlockPos pos, Box pathBounds, World world, WorldShapeCache cache);
}
