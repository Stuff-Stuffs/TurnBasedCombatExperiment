package io.github.stuff_stuffs.tbcexcore.common.battle.event;

public record EventKey<T, View>(Class<T> type, Class<View> viewType) {
}
