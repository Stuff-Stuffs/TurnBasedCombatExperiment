package io.github.stuff_stuffs.tbcexgui.client.render;

import net.minecraft.util.Identifier;

public interface GuiRenderMaterial {
    boolean depthTest();

    boolean translucent();

    boolean ignoreTexture();

    String shader();

    Identifier texture();
}
