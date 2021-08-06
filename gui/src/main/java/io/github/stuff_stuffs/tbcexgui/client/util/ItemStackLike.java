package io.github.stuff_stuffs.tbcexgui.client.util;

import net.minecraft.client.util.math.MatrixStack;

public interface ItemStackLike {
    void render(MatrixStack matrices, double mouseX, double mouseY, float delta);
}
