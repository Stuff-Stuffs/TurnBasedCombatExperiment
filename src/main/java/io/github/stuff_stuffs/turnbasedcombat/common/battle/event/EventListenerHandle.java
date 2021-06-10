package io.github.stuff_stuffs.turnbasedcombat.common.battle.event;

import java.util.function.Consumer;

public final class EventListenerHandle {
    final EventHolder<?, ?> holder;
    final int id;
    private final Consumer<EventListenerHandle> destroyer;
    private boolean destroyed = false;

    EventListenerHandle(final EventHolder<?, ?> holder, final int id, final Consumer<EventListenerHandle> destroyer) {
        this.holder = holder;
        this.destroyer = destroyer;
        this.id = id;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void destroy() {
        if (!destroyed) {
            destroyer.accept(this);
            destroyed = true;
        }
    }
}
