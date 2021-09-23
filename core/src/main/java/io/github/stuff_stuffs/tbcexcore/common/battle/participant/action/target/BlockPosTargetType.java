package io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target;

import com.google.common.collect.AbstractIterator;
import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.tbcexcore.client.TurnBasedCombatExperimentClient;
import io.github.stuff_stuffs.tbcexcore.client.render.BoxInfo;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;

public class BlockPosTargetType extends AbstractBoxedTargetType<BiFunction<BattleStateView, BattleParticipantHandle, Iterable<BlockPos>>> {
    public BlockPosTargetType(final BiFunction<BattleStateView, BattleParticipantHandle, Iterable<BlockPos>> locations) {
        super(locations);
    }

    @Override
    public void render(@Nullable final TargetInstance hovered, final List<TargetInstance> targeted, final BattleParticipantHandle user, final BattleStateView battle, final float tickDelta) {
        final Iterable<BlockPos> locations = source.apply(battle, user);
        final boolean inst = hovered instanceof BlockPosTargetInstance;
        for (final BlockPos location : locations) {
            final double r;
            final double g;
            if (inst && location.equals(((BlockPosTargetInstance) hovered).getBlockPos())) {
                r = 0;
                g = 1;
            } else {
                r = 1;
                g = 0;
            }
            TurnBasedCombatExperimentClient.addBoxInfo(new BoxInfo(location.getX(), location.getY(), location.getZ(), location.getX() + 1, location.getY() + 1, location.getZ() + 1, r, g, 0, 1));
        }
    }

    @Override
    protected BiFunction<Battle, BattleParticipantHandle, Iterable<Pair<Box, BiFunction<Battle, BattleParticipantHandle, TargetInstance>>>> createFunc(final BiFunction<BattleStateView, BattleParticipantHandle, Iterable<BlockPos>> source) {
        return (battle, user) -> () -> new AbstractIterator<>() {
            private final Iterator<BlockPos> iterator = source.apply(battle.getState(), user).iterator();

            @Override
            protected Pair<Box, BiFunction<Battle, BattleParticipantHandle, TargetInstance>> computeNext() {
                if (iterator.hasNext()) {
                    final BattleParticipantStateView battleParticipant = battle.getState().getParticipant(user);
                    if (battleParticipant == null) {
                        //TODO
                        throw new RuntimeException();
                    }
                    final BlockPos location = iterator.next();
                    return Pair.of(new Box(location), (battle, user) -> new BlockPosTargetInstance(location, battleParticipant.getPos().getSquaredDistance(location), BlockPosTargetType.this));
                }
                return endOfData();
            }
        };
    }

    public static class BlockPosTargetInstance implements TargetInstance {
        private final BlockPos blockPos;
        private final double distance;
        private final BlockPosTargetType type;

        private BlockPosTargetInstance(final BlockPos blockPos, final double distance, final BlockPosTargetType type) {
            this.blockPos = blockPos;
            this.distance = distance;
            this.type = type;
        }

        public BlockPos getBlockPos() {
            return blockPos;
        }

        @Override
        public TargetType getType() {
            return type;
        }

        @Override
        public double getDistance() {
            return distance;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof BlockPosTargetInstance that)) {
                return false;
            }
            if (!blockPos.equals(that.blockPos)) {
                return false;
            }
            return type.equals(that.type);
        }

        @Override
        public int hashCode() {
            int result = blockPos.hashCode();
            result = 31 * result + type.hashCode();
            return result;
        }
    }
}
