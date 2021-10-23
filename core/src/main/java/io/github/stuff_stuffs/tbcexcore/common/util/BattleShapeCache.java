package io.github.stuff_stuffs.tbcexcore.common.util;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import io.github.stuff_stuffs.tbcexutil.common.MathUtil;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import io.github.stuff_stuffs.tbcexutil.common.WorldShapeCache;
import net.minecraft.block.BlockState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.RegistryWorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

public final class BattleShapeCache {
    private final WorldShapeCache worldCache;
    private final BattleStateView battleState;

    public BattleShapeCache(final RegistryWorldView world, final BattleStateView battleState) {
        worldCache = new WorldShapeCache(world, null, new Box(0, 0, 0, 0, 0, 0), 2048);
        this.battleState = battleState;
    }

    public BlockState getState(final BlockPos pos) {
        return worldCache.getState(pos);
    }

    public VoxelShape getShape(final BlockPos pos) {
        return worldCache.getShape(pos);
    }

    public VoxelShape getShape(final int x, final int y, final int z) {
        return worldCache.getShape(x, y, z);
    }

    public Iterator<BattleParticipantHandle> getParticipants() {
        return battleState.getParticipants();
    }

    public BattleParticipantBounds getShape(final BattleParticipantHandle handle) {
        final BattleParticipantStateView participant = battleState.getParticipant(handle);
        if (participant == null) {
            throw new TBCExException("Missing participant in battle, probably cme");
        }
        return participant.getBounds();
    }

    public boolean canSeeAny(Vec3d eyePos, Vec3d[] targets, BattleParticipantHandle ...exclusions) {
        final Iterator<BattleParticipantHandle> participants = getParticipants();
        while (participants.hasNext()) {
            BattleParticipantHandle next = participants.next();
            boolean excluded = false;
            for (BattleParticipantHandle  exclusion: exclusions) {
                if(exclusion.equals(next)) {
                    excluded = true;
                    break;
                }
            }
            if(!excluded) {
                boolean seeAny = false;
                final BattleParticipantBounds shape = getShape(next);
                for (Vec3d target : targets) {
                    final BattleParticipantBounds.RaycastResult raycast = shape.raycast(eyePos, target);
                    if(raycast==null) {
                        seeAny = true;
                        break;
                    }
                }
                if(!seeAny) {
                    return false;
                }
            }
        }
        boolean seeAny = false;
        for (Vec3d target : targets) {
            final HitResult hitResult = MathUtil.rayCast(eyePos, target, pos -> {
                VoxelShape shape = getShape(pos);
                if (shape == VoxelShapes.empty() || shape.isEmpty()) {
                    return null;
                }
                final BlockHitResult raycast = shape.raycast(eyePos, target, pos);
                if (raycast != null && raycast.getType() != HitResult.Type.MISS) {
                    return raycast;
                }
                return null;
            });
            if(hitResult==null||hitResult.getType()== HitResult.Type.MISS) {
                seeAny = true;
                break;
            }
        }
        return seeAny;
    }
}
