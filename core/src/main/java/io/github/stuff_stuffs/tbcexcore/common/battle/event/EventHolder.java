package io.github.stuff_stuffs.tbcexcore.common.battle.event;

public interface EventHolder<T, View> {
    EventListenerHandle register(View viewListener);

    T invoker();
}
