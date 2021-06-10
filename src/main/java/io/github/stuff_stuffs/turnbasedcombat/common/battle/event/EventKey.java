package io.github.stuff_stuffs.turnbasedcombat.common.battle.event;

public record EventKey<T, View>(Class<T> type, Class<View> viewType) {
}
