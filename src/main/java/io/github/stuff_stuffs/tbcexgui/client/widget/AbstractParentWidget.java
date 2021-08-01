package io.github.stuff_stuffs.tbcexgui.client.widget;

import io.github.stuff_stuffs.tbcexgui.client.util.IdSupplier;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Comparator;
import java.util.List;

public abstract class AbstractParentWidget extends AbstractWidget implements ParentWidget {
    private static final Comparator<WrappedWidget> COMPARATOR = Comparator.<WrappedWidget>comparingDouble(
            widget -> widget.widget.getWidgetPosition().getZ()
    ).thenComparingInt(
            widget -> widget.clicks
    );
    protected final int thisId;
    protected final IdSupplier ids;
    protected final Int2ReferenceMap<WrappedWidget> widgetById;
    protected final List<WrappedWidget> sorted;

    protected AbstractParentWidget() {
        thisId = ID_SUPPLIER.nextId();
        ids = new IdSupplier();
        widgetById = new Int2ReferenceOpenHashMap<>();
        sorted = new ReferenceArrayList<>();
    }

    @Override
    public WidgetHandle addWidget(final Widget widget) {
        final int id = ids.nextId();
        final WrappedWidget wrappedWidget = new WrappedWidget(widget);
        widgetById.put(id, wrappedWidget);
        sorted.add(wrappedWidget);
        sorted.sort(COMPARATOR);
        return new WidgetHandle(id, thisId);
    }

    @Override
    public void removeWidget(final WidgetHandle handle) {
        if (handle.parentId != thisId) {
            throw new RuntimeException("Mismatched parent ids");
        }
        final WrappedWidget removed = widgetById.remove(handle.widgetId);
        if (removed != null) {
            sorted.remove(removed);
        }
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        for (int i = sorted.size() - 1; i >= 0; i--) {
            final WrappedWidget widget = sorted.get(i);
            if (widget.widget.mouseClicked(mouseX, mouseY, button)) {
                widget.clicks++;
                sorted.sort(COMPARATOR);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (int i = sorted.size() - 1; i >= 0; i--) {
            final WrappedWidget widget = sorted.get(i);
            if (widget.widget.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        for (int i = sorted.size() - 1; i >= 0; i--) {
            final WrappedWidget widget = sorted.get(i);
            if (widget.widget.mouseScrolled(mouseX, mouseY, amount)) {
                widget.clicks++;
                sorted.sort(COMPARATOR);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        for (int i = sorted.size() - 1; i >= 0; i--) {
            final WrappedWidget widget = sorted.get(i);
            if (widget.widget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                widget.clicks++;
                sorted.sort(COMPARATOR);
                return true;
            }
        }
        return false;
    }

    @Override
    public void resize(final double width, final double height, final int pixelWidth, final int pixelHeight) {
        super.resize(width, height, pixelWidth, pixelHeight);
        for (final WrappedWidget widget : sorted) {
            widget.widget.resize(width, height, pixelWidth, pixelHeight);
        }
    }

    @Override
    public void render(MatrixStack matrices, double mouseX, double mouseY, float delta) {
        for (WrappedWidget widget : sorted) {
            widget.widget.render(matrices, mouseX, mouseY, delta);
        }
    }

    protected static class WrappedWidget {
        public final Widget widget;
        public int clicks = 0;

        public WrappedWidget(final Widget widget) {
            this.widget = widget;
        }
    }
}
