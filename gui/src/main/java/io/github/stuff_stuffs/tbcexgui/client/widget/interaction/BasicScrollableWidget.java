package io.github.stuff_stuffs.tbcexgui.client.widget.interaction;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.stuff_stuffs.tbcexgui.client.render.ScissorStack;
import io.github.stuff_stuffs.tbcexgui.client.util.Rect2d;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.Widget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BasicScrollableWidget extends AbstractWidget {
    private final WidgetPosition position;
    private final List<SizedWidget> widgets;
    private final double width;
    private final double height;
    private final double verticalSpacing;
    private final double horizontalBorder;
    private double scrollOffsetX = 0;
    private double scrollOffsetY = 0;
    private @Nullable SizedWidget focused;

    public BasicScrollableWidget(final WidgetPosition position, final List<SizedWidget> widgets, final double width, final double height, final double verticalSpacing, final double horizontalBorder) {
        this.position = position;
        this.widgets = widgets;
        this.width = width;
        this.height = height;
        this.verticalSpacing = verticalSpacing;
        this.horizontalBorder = horizontalBorder;
    }

    public double getScrollOffsetX() {
        return scrollOffsetX;
    }

    public double getScrollOffsetY() {
        return scrollOffsetY;
    }

    public void setScrollOffsetY(final double scrollOffsetY) {
        this.scrollOffsetY = Math.max(Math.min(scrollOffsetY, calculateMaxY()-height), 0);
    }

    private double calculateMaxY() {
        double acc = 0;
        for (SizedWidget widget : widgets) {
            acc += widget.getHeight();
        }
        acc += verticalSpacing * (widgets.size()-1);
        return acc;
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return position;
    }

    @Override
    public void setFocused(final boolean focused) {
        super.setFocused(focused);
        if (!focused && this.focused != null) {
            this.focused.getWidget().setFocused(false);
            this.focused = null;
        }
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        for (final SizedWidget sizedWidget : widgets) {
            final Widget widget = sizedWidget.getWidget();
            final Rect2d rect = new Rect2d(widget.getWidgetPosition().getX(), widget.getWidgetPosition().getY(), widget.getWidgetPosition().getX() + sizedWidget.getWidth(), widget.getWidgetPosition().getY() + sizedWidget.getHeight());
            if (rect.isIn(mouseX, mouseY)) {
                final boolean b = widget.mouseClicked(mouseX, mouseY, button);
                if (b) {
                    focused = sizedWidget;
                    widget.setFocused(true);
                }
                return b;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        for (final SizedWidget sizedWidget : widgets) {
            final Widget widget = sizedWidget.getWidget();
            final Rect2d rect = new Rect2d(widget.getWidgetPosition().getX(), widget.getWidgetPosition().getY(), widget.getWidgetPosition().getX() + sizedWidget.getWidth(), widget.getWidgetPosition().getY() + sizedWidget.getHeight());
            if (rect.isIn(mouseX, mouseY)) {
                return widget.mouseReleased(mouseX, mouseY, button);
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        for (final SizedWidget sizedWidget : widgets) {
            final Widget widget = sizedWidget.getWidget();
            final Rect2d rect = new Rect2d(widget.getWidgetPosition().getX(), widget.getWidgetPosition().getY(), widget.getWidgetPosition().getX() + sizedWidget.getWidth(), widget.getWidgetPosition().getY() + sizedWidget.getHeight());
            if (rect.isIn(mouseX, mouseY)) {
                return widget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        for (final SizedWidget sizedWidget : widgets) {
            final Widget widget = sizedWidget.getWidget();
            final Rect2d rect = new Rect2d(widget.getWidgetPosition().getX(), widget.getWidgetPosition().getY(), widget.getWidgetPosition().getX() + sizedWidget.getWidth(), widget.getWidgetPosition().getY() + sizedWidget.getHeight());
            if (rect.isIn(mouseX, mouseY) && widget.mouseScrolled(mouseX, mouseY, amount)) {
                return true;
            }
        }
        final Rect2d rect = new Rect2d(position.getX(), position.getY(), position.getX() + width, position.getY() + height);
        if (rect.isIn(mouseX, mouseY)) {
            setScrollOffsetY(scrollOffsetY+amount);
            return true;
        }
        return false;
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        final double x = position.getX();
        final double y = position.getY();
        final Matrix4f model = matrices.peek().getModel();
        final BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(model, (float) (x+width), (float) y, 0).color(255, 0, 0, 127).next();
        buffer.vertex(model, (float) x, (float) y, 0).color(255, 0, 0, 127).next();
        buffer.vertex(model, (float) x, (float) (y+height), 0).color(255, 0, 0, 127).next();
        buffer.vertex(model, (float) (x+width), (float) (y+height), 0).color(255, 0, 0, 127).next();
        buffer.end();
        BufferRenderer.draw(buffer);

        ScissorStack.push(matrices, x + horizontalBorder, y, x + width - horizontalBorder, y + height);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(model, (float) (x+width), (float) y, 0).color(0, 0, 255, 127).next();
        buffer.vertex(model, (float) x, (float) y, 0).color(0, 0, 255, 127).next();
        buffer.vertex(model, (float) x, (float) (y+height), 0).color(0, 0, 255, 127).next();
        buffer.vertex(model, (float) (x+width), (float) (y+height), 0).color(0, 0, 255, 127).next();
        buffer.end();
        BufferRenderer.draw(buffer);

        for (final SizedWidget widget : widgets) {
            widget.getWidget().render(matrices, mouseX, mouseY, delta);
        }
        ScissorStack.pop();
    }

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        if (focused != null) {
            return focused.getWidget().keyPress(keyCode, scanCode, modifiers);
        }
        return false;
    }

    public interface SizedWidget {
        double getWidth();

        double getHeight();

        Widget getWidget();
    }
}
