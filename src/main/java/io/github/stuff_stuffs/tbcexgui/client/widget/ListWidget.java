package io.github.stuff_stuffs.tbcexgui.client.widget;

import io.github.stuff_stuffs.tbcexgui.client.render.ScissorStack;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class ListWidget extends AbstractWidget {
    private final WidgetPosition position;
    private final List<Entry> entries;
    private final double width, height;
    private final double entryHeight;
    private double scrollPosition;

    public ListWidget(final WidgetPosition position, final double width, final double height, final double entryHeight) {
        this.position = position;
        this.width = width;
        this.height = height;
        this.entryHeight = entryHeight;
        entries = new ArrayList<>();
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return position;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        return false;
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        return false;
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        return false;
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        ScissorStack.push(
                matrices,
                position.getX() * getScreenWidth(),
                position.getY() * getScreenHeight(),
                position.getX() * getScreenWidth() + width,
                position.getY() * getScreenHeight() + height
        );
        final double pos = scrollPosition;
        int index = 0;
        for (final Entry entry : entries) {
            final double yPos = index - pos;
            if (entry.shown.getAsBoolean()) {
                matrices.push();
                matrices.translate(0, yPos * entryHeight, 0);
                entry.widget.render(matrices, mouseX, mouseY + yPos * entryHeight, delta);
                matrices.pop();
                index++;
            }
        }
        ScissorStack.pop();
    }

    private static class Entry {
        public final Widget widget;
        public final BooleanSupplier shown;

        public Entry(final Widget widget, final BooleanSupplier shown) {
            this.widget = widget;
            this.shown = shown;
        }
    }
}
