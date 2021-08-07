package io.github.stuff_stuffs.tbcexcore.common.battle.participant.stats;

import io.github.stuff_stuffs.tbcexcore.common.battle.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.text.Text;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;

public final class BattleParticipantStatModifiers {
    private final Map<BattleParticipantStat, StatModifierList> modifiers;

    public BattleParticipantStatModifiers() {
        modifiers = new Reference2ObjectOpenHashMap<>();
    }

    public Handle modify(BattleParticipantStat stat, BattleParticipantStatModifier modifier) {
        return modifiers.computeIfAbsent(stat, s -> new StatModifierList()).add(modifier);
    }


    public double calculate(BattleParticipantStat stat, double baseValue, BattleStateView battleState, BattleParticipantStateView entityState) {
        final StatModifierList modifierList = modifiers.get(stat);
        if (modifierList == null) {
            return baseValue;
        }
        return modifierList.calculate(baseValue, battleState, entityState);
    }

    private static final class StatModifierList {
        private static final Comparator<BattleParticipantStatModifierFrozen> COMPARATOR = Comparator.<BattleParticipantStatModifierFrozen>
                        comparingInt(BattleParticipantStatModifier::getPhase).
                thenComparing(BattleParticipantStatModifier::getStage, BattleParticipantStatModifier.Stage.COMPARATOR).
                thenComparingInt(BattleParticipantStatModifierFrozen::getId);
        private final SortedSet<BattleParticipantStatModifierFrozen> modifiers;
        private int nextId;

        public StatModifierList() {
            modifiers = new ObjectAVLTreeSet<>(COMPARATOR);
        }

        public Handle add(final BattleParticipantStatModifier modifier) {
            final BattleParticipantStatModifierFrozen frozen = new BattleParticipantStatModifierFrozen(modifier, nextId++);
            if (!modifiers.add(frozen)) {
                throw new RuntimeException();
            }
            return new Handle(this, frozen);
        }

        private void remove(final BattleParticipantStatModifierFrozen frozen) {
            if (!modifiers.remove(frozen)) {
                throw new RuntimeException();
            }
        }

        public double calculate(double baseValue, BattleStateView battleState, BattleParticipantStateView entityState) {
            double val = baseValue;
            for (BattleParticipantStatModifierFrozen modifier : modifiers) {
                val = modifier.modify(val, battleState, entityState);
            }
            return val;
        }
    }

    private static class BattleParticipantStatModifierFrozen implements BattleParticipantStatModifier {
        private final BattleParticipantStatModifier delegate;
        private final int phase;
        private final Stage stage;
        private final int id;

        private BattleParticipantStatModifierFrozen(final BattleParticipantStatModifier delegate, final int id) {
            this.delegate = delegate;
            phase = delegate.getPhase();
            stage = delegate.getStage();
            this.id = id;
        }

        @Override
        public Text getTooltip() {
            return delegate.getTooltip();
        }

        @Override
        public double modify(final double in, final BattleStateView battleState, final BattleParticipantStateView participantState) {
            return delegate.modify(in, battleState, participantState);
        }

        @Override
        public int getPhase() {
            return phase;
        }

        @Override
        public Stage getStage() {
            return stage;
        }

        public int getId() {
            return id;
        }
    }

    public static final class Handle {
        private final StatModifierList list;
        private final BattleParticipantStatModifierFrozen frozen;
        private boolean destroyed;

        public Handle(final StatModifierList list, final BattleParticipantStatModifierFrozen frozen) {
            this.list = list;
            this.frozen = frozen;
            destroyed = false;
        }

        public boolean isDestroyed() {
            return destroyed;
        }

        public void destroy() {
            if (!isDestroyed()) {
                destroyed = true;
                list.remove(frozen);
            }
        }
    }
}
