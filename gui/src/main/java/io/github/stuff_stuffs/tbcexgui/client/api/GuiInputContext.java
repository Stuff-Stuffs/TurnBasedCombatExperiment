package io.github.stuff_stuffs.tbcexgui.client.api;

import org.jetbrains.annotations.Nullable;

public interface GuiInputContext {
    double getMouseCursorX();

    double getMouseCursorY();

    EventIterator getEvents();

    interface EventIterator extends AutoCloseable {
        @Nullable InputEvent next();

        void consume();
    }

    sealed interface InputEvent permits KeyModsPress, KeyPress, MouseClick, MouseDrag, MouseMove, MouseReleased, MouseScroll {
    }

    final class MouseMove implements InputEvent {
        public final double mouseX, mouseY;

        public MouseMove(final double mouseX, final double mouseY) {
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }
    }

    final class MouseClick implements InputEvent {
        public final double mouseX, mouseY;
        public final int button;

        public MouseClick(final double mouseX, final double mouseY, final int button) {
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.button = button;
        }
    }

    final class MouseDrag implements InputEvent {
        public final double mouseX, mouseY;
        public final double deltaX, deltaY;
        public final int button;

        public MouseDrag(final double mouseX, final double mouseY, final double deltaX, final double deltaY, final int button) {
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.deltaX = deltaX;
            this.deltaY = deltaY;
            this.button = button;
        }
    }

    final class MouseReleased implements InputEvent {
        public final double mouseX, mouseY;
        public final int button;

        public MouseReleased(final double mouseX, final double mouseY, final int button) {
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.button = button;
        }
    }

    final class MouseScroll implements InputEvent {
        public final double mouseX, mouseY;
        public final double amount;

        public MouseScroll(final double mouseX, final double mouseY, final double amount) {
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.amount = amount;
        }
    }

    final class KeyPress implements InputEvent {
        public final int keyCode;
        public final boolean shift, alt, ctrl, capsLock, numLock;

        public KeyPress(final int keyCode, final boolean shift, final boolean alt, final boolean ctrl, final boolean capsLock, final boolean numLock) {
            this.keyCode = keyCode;
            this.shift = shift;
            this.alt = alt;
            this.ctrl = ctrl;
            this.capsLock = capsLock;
            this.numLock = numLock;
        }
    }

    final class KeyModsPress implements InputEvent {
        public final int codePoint;
        public final boolean shift, alt, ctrl, capsLock, numLock;

        public KeyModsPress(final int codePoint, final boolean shift, final boolean alt, final boolean ctrl, final boolean capsLock, final boolean numLock) {
            this.codePoint = codePoint;
            this.shift = shift;
            this.alt = alt;
            this.ctrl = ctrl;
            this.capsLock = capsLock;
            this.numLock = numLock;
        }
    }
}
