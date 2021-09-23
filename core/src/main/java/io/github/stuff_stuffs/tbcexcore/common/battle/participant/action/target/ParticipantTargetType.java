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

public class ParticipantTargetType extends AbstractBoxedTargetType<BiFunction<BattleStateView, BattleParticipantHandle, Iterable<BattleParticipantHandle>>> {
    public ParticipantTargetType(final BiFunction<BattleStateView, BattleParticipantHandle, Iterable<BattleParticipantHandle>> source) {
        super(source);
    }

    @Override
    public void render(@Nullable final TargetInstance hovered, final List<TargetInstance> targeted, final BattleParticipantHandle user, final BattleStateView battle, final float tickDelta) {
        final Iterable<BattleParticipantHandle> locations = source.apply(battle, user);
        final boolean inst = hovered instanceof ParticipantTargetInstance;
        for (final BattleParticipantHandle targetHandle : locations) {
            final BattleParticipantStateView target = battle.getParticipant(targetHandle);
            if(target==null) {
                //TODO
                throw new RuntimeException();
            }
            final double r;
            final double g;
            if (inst && targetHandle.equals(((ParticipantTargetInstance) hovered).getHandle())) {
                r = 0;
                g = 1;
            } else {
                r = 1;
                g = 0;
            }
            BlockPos location = target.getPos();
            TurnBasedCombatExperimentClient.addBoxInfo(new BoxInfo(location.getX(), location.getY(), location.getZ(), location.getX() + 1, location.getY() + 2, location.getZ() + 1, r, g, 0, 1));
        }
    }

    @Override
    protected BiFunction<Battle, BattleParticipantHandle, Iterable<Pair<Box, BiFunction<Battle, BattleParticipantHandle, TargetInstance>>>> createFunc(final BiFunction<BattleStateView, BattleParticipantHandle, Iterable<BattleParticipantHandle>> source) {
        return (battle, user) -> () -> new AbstractIterator<>() {
            private final Iterator<BattleParticipantHandle> iterator = source.apply(battle.getState(), user).iterator();

            @Override
            protected Pair<Box, BiFunction<Battle, BattleParticipantHandle, TargetInstance>> computeNext() {
                if (iterator.hasNext()) {
                    final BattleParticipantStateView battleParticipant = battle.getState().getParticipant(user);
                    if (battleParticipant == null) {
                        //TODO
                        throw new RuntimeException();
                    }
                    final BattleParticipantHandle targetHandle = iterator.next();
                    final BattleParticipantStateView target = battle.getState().getParticipant(targetHandle);
                    if (target == null) {
                        throw new RuntimeException();
                    }
                    return Pair.of(new Box(target.getPos()).stretch(0, 1, 0), (battle, user) -> new ParticipantTargetInstance(targetHandle, battleParticipant.getPos().getSquaredDistance(target.getPos()), ParticipantTargetType.this));
                }
                return endOfData();
            }
        };
    }

    public record ParticipantTargetInstance(
            BattleParticipantHandle handle,
            double distance,
            ParticipantTargetType type
    ) implements TargetInstance {

        public BattleParticipantHandle getHandle() {
            return handle;
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
            if (!(o instanceof ParticipantTargetInstance that)) {
                return false;
            }
            if (!handle.equals(that.handle)) {
                return false;
            }
            return type.equals(that.type);
        }

        @Override
        public int hashCode() {
            int result = handle.hashCode();
            result = 31 * result + type.hashCode();
            return result;
        }
    }
}
