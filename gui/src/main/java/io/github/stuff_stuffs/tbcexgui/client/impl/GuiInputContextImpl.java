package io.github.stuff_stuffs.tbcexgui.client.impl;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiInputContext;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GuiInputContextImpl implements GuiInputContext {
    private double mouseX, mouseY;
    private List<ReservableInputEvent> events = new ArrayList<>();

    public void setup(final double mouseX, final double mouseY, final List<InputEvent> events) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.events = events.stream().map(ReservableInputEvent::new).toList();
    }

    @Override
    public double getMouseCursorX() {
        return mouseX;
    }

    @Override
    public double getMouseCursorY() {
        return mouseY;
    }

    @Override
    public EventIterator getEvents() {
        return new EventIteratorImpl();
    }

    private final class EventIteratorImpl implements EventIterator {
        private ReservableInputEvent cur;
        private boolean done = false;

        @Override
        public @Nullable InputEvent next() {
            if (done) {
                return null;
            }
            findNext();
            return cur.event;
        }

        @Override
        public void consume() {
            cur.removed = true;
        }

        @Override
        public void close() {
            if (cur != null) {
                cur.reserved = false;
            }
        }

        private void findNext() {
            if (done) {
                throw new IllegalStateException();
            }
            int idx;
            if (cur == null) {
                idx = 0;
            } else {
                cur.reserved = false;
                idx = events.indexOf(cur);
            }
            while (idx < events.size()) {
                final ReservableInputEvent event = events.get(idx);
                if (!event.reserved && !event.removed) {
                    event.reserved = true;
                    cur = event;
                    return;
                }
                idx++;
            }
            cur = null;
            done = true;
        }
    }

    private static final class ReservableInputEvent {
        private final InputEvent event;
        private boolean reserved = false;
        private boolean removed = false;

        private ReservableInputEvent(final InputEvent event) {
            this.event = event;
        }
    }
}
