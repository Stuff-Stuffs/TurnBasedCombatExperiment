package io.github.stuff_stuffs.tbcexgui.client.widget;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Comparator;
import java.util.List;

public abstract class AbstractParentWidget extends AbstractWidget {
    private static final Comparator<WrappedWidget> COMPARATOR = Comparator.<WrappedWidget>comparingDouble(wrapped -> wrapped.widget.getWidgetPosition().getZ()).thenComparingInt(wrapped -> wrapped.lastClicked);
    private final List<WrappedWidget> sorted;
    private int clickCount;

    public AbstractParentWidget() {
        sorted = new ReferenceArrayList<>();
    }

    public void addWidget(final Widget widget) {
        final WrappedWidget wrappedWidget = new WrappedWidget(widget, clickCount++);
        sorted.add(wrappedWidget);
        sorted.sort(COMPARATOR);
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        for (int i = sorted.size() - 1; i >= 0; i--) {
            final WrappedWidget wrapped = sorted.get(i);
            final boolean b = wrapped.widget.mouseClicked(mouseX, mouseY, button);
            if (b) {
                wrapped.lastClicked++;
                sorted.sort(COMPARATOR);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        for (int i = sorted.size() - 1; i >= 0; i--) {
            final WrappedWidget wrapped = sorted.get(i);
            final boolean b = wrapped.widget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            if (b) {
                wrapped.lastClicked++;
                sorted.sort(COMPARATOR);
                return true;
            }
        }
        return false;
    }

    @Override
    public void resize(double width, double height, int pixelWidth, int pixelHeight) {
        super.resize(width, height, pixelWidth, pixelHeight);
        for (WrappedWidget wrappedWidget : sorted) {
            wrappedWidget.widget.resize(width,height, pixelWidth, pixelHeight);
        }
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        for (int i = sorted.size() - 1; i >= 0; i--) {
            final WrappedWidget wrapped = sorted.get(i);
            final boolean b = wrapped.widget.mouseScrolled(mouseX, mouseY, amount);
            if (b) {
                wrapped.lastClicked++;
                sorted.sort(COMPARATOR);
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        for (int i = sorted.size() - 1; i >= 0; i--) {
            final WrappedWidget wrapped = sorted.get(i);
            final Widget widget = wrapped.widget;
            matrices.push();
            //matrices.translate(0,0,-0.1);
            widget.render(matrices, mouseX, mouseY, delta);
            matrices.pop();
        }
    }

    protected static class WrappedWidget {
        private final Widget widget;
        private int lastClicked;

        private WrappedWidget(final Widget widget, final int lastClicked) {
            this.widget = widget;
            this.lastClicked = lastClicked;
        }
    }
}
