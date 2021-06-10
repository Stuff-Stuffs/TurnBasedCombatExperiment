package io.github.stuff_stuffs.turnbasedcombat.common.battle.event;

public interface EventHolder<T, View> {
    EventListenerHandle register(View viewListener);

    T invoker();
}
