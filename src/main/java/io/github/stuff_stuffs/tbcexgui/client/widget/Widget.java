package io.github.stuff_stuffs.tbcexgui.client.widget;

import net.minecraft.client.util.math.MatrixStack;

public interface Widget {
    WidgetPosition getWidgetPosition();

    boolean mouseClicked(double mouseX, double mouseY, int button);

    boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY);

    boolean mouseScrolled(double mouseX, double mouseY, double amount);

    void resize(double width, double height, int pixelWidth, int pixelHeight);

    void render(MatrixStack matrices, double mouseX, double mouseY, float delta);
}
