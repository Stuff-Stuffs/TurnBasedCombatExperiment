package io.github.stuff_stuffs.turnbasedcombat.common.api.path;

import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleBounds;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RegistryWorldView;

import java.util.Map;

public interface PathEnumerator {
    Map<BlockPos, PathNode> getPaths(RegistryWorldView blockView, BlockPos startPos, Entity entity, BattleBounds bounds, int maxLength);
}
