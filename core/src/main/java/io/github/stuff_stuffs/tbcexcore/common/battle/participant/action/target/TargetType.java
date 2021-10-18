package io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target;

import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface TargetType {
    @Nullable TargetInstance find(Vec3d pos, Vec3d direction, BattleParticipantHandle user, Battle battle);

    void render(@Nullable TargetInstance hovered, List<TargetInstance> targeted, BattleParticipantHandle user, BattleStateView battle, float tickDelta);

    final class Compound implements TargetType {
        private final TargetType[] targetTypes;
        private TargetType[] flattened;

        public Compound(final TargetType... targetTypes) {
            if (targetTypes.length == 0) {
                //TODO
                throw new RuntimeException();
            }
            this.targetTypes = targetTypes;
        }

        @Override
        public @Nullable TargetInstance find(final Vec3d pos, final Vec3d lookDirection, final BattleParticipantHandle user, final Battle battle) {
            double minDist = Double.POSITIVE_INFINITY;
            TargetInstance closest = null;
            for (final TargetType type : targetTypes) {
                final TargetInstance instance = type.find(pos, lookDirection, user, battle);
                if (instance != null) {
                    final double distance = instance.getDistance();
                    if (distance < minDist) {
                        minDist = distance;
                        closest = instance;
                    }
                }
            }
            return closest;
        }

        @Override
        public void render(@Nullable final TargetInstance hovered, final List<TargetInstance> targeted, final BattleParticipantHandle user, final BattleStateView battle, final float tickDelta) {
            for (final TargetType targetType : targetTypes) {
                targetType.render(hovered, targeted, user, battle, tickDelta);
            }
        }

        private TargetType[] getFlattened() {
            if (flattened == null) {
                final List<TargetType> primitives = new ArrayList<>(targetTypes.length);
                for (final TargetType type : targetTypes) {
                    if (type instanceof Compound compound) {
                        Collections.addAll(primitives, compound.getFlattened());
                    } else {
                        primitives.add(type);
                    }
                }
                flattened = primitives.toArray(new TargetType[0]);
            }
            return flattened;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Compound compound)) {
                return false;
            }
            return Arrays.equals(getFlattened(), compound.getFlattened());
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(getFlattened());
        }
    }
}
