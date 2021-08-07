package io.github.stuff_stuffs.tbcexcore.common.battle;

import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.BattleAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.BattleActionRegistry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class BattleTimeline implements BattleTimelineView {
    public static final Codec<BattleTimeline> CODEC = Codec.list(BattleActionRegistry.CODEC).xmap(BattleTimeline::new, timeline -> timeline.actions);
    private final List<BattleAction<?>> actions;

    private BattleTimeline(final List<BattleAction<?>> actions) {
        this.actions = new ArrayList<>(actions);
    }

    public BattleTimeline() {
        actions = new ArrayList<>();
    }

    public void push(final BattleAction<?> action) {
        actions.add(action);
    }

    public void trim(final int size) {
        while (actions.size() > size) {
            actions.remove(actions.size() - 1);
        }
    }

    @Override
    public BattleAction<?> get(final int index) {
        return actions.get(index);
    }

    @Override
    public int getSize() {
        return actions.size();
    }

    @Override
    public Iterator<BattleAction<?>> iterator() {
        return Iterators.unmodifiableIterator(actions.iterator());
    }
}
