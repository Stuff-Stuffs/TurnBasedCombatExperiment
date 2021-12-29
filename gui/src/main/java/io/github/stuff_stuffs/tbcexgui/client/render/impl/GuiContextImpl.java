package io.github.stuff_stuffs.tbcexgui.client.render.impl;

import io.github.stuff_stuffs.tbcexgui.client.render.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.render.GuiTransform;
import io.github.stuff_stuffs.tbcexgui.client.render.MutableGuiQuad;
import io.github.stuff_stuffs.tbcexutil.common.Vec2d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class GuiContextImpl implements GuiContext {
    private final List<GuiTransform> transforms;
    private final VertexConsumerProvider vertexConsumers;
    private final GuiVcpTextAdapter textAdapter;
    private final GuiQuadEmitterImpl emitter;
    private final GuiTransform.Context context;

    public GuiContextImpl(MatrixStack stack, VertexConsumerProvider vertexConsumers) {
        this.vertexConsumers = vertexConsumers;
        this.transforms = new ArrayList<>();
        pushMatrixMultiply(stack.peek().getPositionMatrix());
        textAdapter = new GuiVcpTextAdapter(this);
        emitter = new GuiQuadEmitterImpl(vertexConsumers, this, new MutableGuiQuadImpl());
        context = () -> emitter;
    }

    public GuiContextImpl(List<GuiTransform> transforms, VertexConsumerProvider vertexConsumers) {
        this.transforms = transforms;
        this.vertexConsumers = vertexConsumers;
        textAdapter = new GuiVcpTextAdapter(this);
        emitter = new GuiQuadEmitterImpl(vertexConsumers, this, new MutableGuiQuadImpl());
        context = () -> emitter;
    }

    @Override
    public GuiContext createChild() {
        return new GuiContextImpl(new ArrayList<>(transforms), vertexConsumers);
    }

    @Override
    public void pushQuadTransform(GuiTransform transform) {
        transforms.add(transform);
    }

    @Override
    public void popQuadTransform() {
        transforms.remove(transforms.size() - 1);
    }

    @Override
    public Vec2d transformMouseCursor(Vec2d mouseCursor) {
        for (int i = transforms.size() - 1; i >= 0; i--) {
            mouseCursor = transforms.get(i).transformMouseCursor(mouseCursor);
        }
        return mouseCursor;
    }

    @Override
    public void renderText(OrderedText text, TextOutline outline, int colour, int outlineColour, int underlineColour) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        Matrix4f identity = Matrix4f.translate(0, 0, 0);
        switch (outline) {
            case NONE -> textRenderer.draw(text, 0, 0, colour, false, identity, textAdapter, true, underlineColour, LightmapTextureManager.MAX_LIGHT_COORDINATE);
            case OUTLINE -> textRenderer.drawWithOutline(text, 0, 0, colour, outlineColour, identity, textAdapter, LightmapTextureManager.MAX_LIGHT_COORDINATE);
            case SHADOW -> textRenderer.draw(text, 0, 0, colour, true, identity, textAdapter, true, underlineColour, LightmapTextureManager.MAX_LIGHT_COORDINATE);
        }
    }

    public boolean transformQuad(MutableGuiQuad quad) {
        for (int i = transforms.size() - 1; i >= 0; i--) {
            emitter.reset();
            if (!transforms.get(i).transform(quad, context)) {
                return false;
            }
        }
        emitter.reset();
        return true;
    }

    public VertexConsumer getVertexConsumerForLayer(RenderLayer layer) {
        return vertexConsumers.getBuffer(layer);
    }
}
