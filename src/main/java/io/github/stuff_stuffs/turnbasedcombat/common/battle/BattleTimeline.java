package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public final class BattleTimeline implements BattleTimelineView {
    public static final Codec<BattleTimeline> CODEC = Codec.list(BattleAction.CODEC).xmap(BattleTimeline::new, timeline -> timeline.actions);
    private final List<BattleAction> actions;

    private BattleTimeline(final List<BattleAction> actions) {
        this.actions = actions;
    }

    public BattleTimeline() {
        actions = new ReferenceArrayList<>();
    }

    public void push(final BattleAction action) {
        actions.add(action);
    }

    @Override
    public int size() {
        return actions.size();
    }

    @Override
    public BattleAction get(final int index) {
        return actions.get(index);
    }

    @NotNull
    @Override
    public Iterator<BattleAction> iterator() {
        return Iterators.unmodifiableIterator(actions.iterator());
    }
}
