package io.github.stuff_stuffs.tbcexgui.client.render;

public interface GuiRenderMaterial {
    boolean depthTest();

    boolean translucent();

    boolean ignoreTexture();

    String shader();
}
