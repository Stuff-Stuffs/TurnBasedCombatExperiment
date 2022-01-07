package io.github.stuff_stuffs.tbcexgui.client.widget;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiInputContext;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;

import java.util.function.Predicate;

public abstract class AbstractWidget implements Widget {
    private double width, height;
    private int pixelWidth, pixelHeight;

    @Override
    public void resize(final double width, final double height, final int pixelWidth, final int pixelHeight) {
        this.width = width;
        this.height = height;
        this.pixelWidth = pixelWidth;
        this.pixelHeight = pixelHeight;
    }

    public double getScreenWidth() {
        return width;
    }

    public double getScreenHeight() {
        return height;
    }

    public int getPixelWidth() {
        return pixelWidth;
    }

    public int getPixelHeight() {
        return pixelHeight;
    }

    protected void processEvents(final GuiContext context, final Predicate<GuiInputContext.InputEvent> eventConsumer) {
        try (final GuiInputContext.EventIterator events = context.getInputContext().getEvents()) {
            GuiInputContext.InputEvent event;
            while ((event = events.next()) != null) {
                if (eventConsumer.test(event)) {
                    events.consume();
                }
            }
        } catch (final Exception e) {
            throw new TBCExException("Exception while processing gui events", e);
        }
    }
}
