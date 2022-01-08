package io.github.stuff_stuffs.tbcexgui.client.widget;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;

import java.util.function.DoubleSupplier;

public final class WidgetModifiers {
    private WidgetModifiers() {
    }

    public static PositionedWidget positioned(final Widget widget, final DoubleSupplier x, final DoubleSupplier y) {
        return new PositionedWidget() {
            @Override
            public double getX() {
                return x.getAsDouble();
            }

            @Override
            public double getY() {
                return y.getAsDouble();
            }

            @Override
            public void resize(final double width, final double height, final int pixelWidth, final int pixelHeight) {
                widget.resize(width, height, pixelWidth, pixelHeight);
            }

            @Override
            public void render(final GuiContext context) {
                widget.render(context);
            }

            @Override
            public String getDebugName() {
                return "Positioned{" + widget.getDebugName() + "}";
            }
        };
    }
}
