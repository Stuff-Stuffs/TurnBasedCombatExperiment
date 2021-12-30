package io.github.stuff_stuffs.tbcexgui.client.impl;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiInputContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiTransform;
import io.github.stuff_stuffs.tbcexgui.client.api.MutableGuiQuad;
import io.github.stuff_stuffs.tbcexgui.client.impl.render.GuiQuadEmitterImpl;
import io.github.stuff_stuffs.tbcexgui.client.impl.render.GuiVcpTextAdapter;
import io.github.stuff_stuffs.tbcexgui.client.impl.render.MutableGuiQuadImpl;
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
    private final GuiInputContextImpl inputContext;
    private final float tickDelta;

    public GuiContextImpl(final MatrixStack stack, final VertexConsumerProvider vertexConsumers, final double mouseX, final double mouseY, final List<GuiInputContext.InputEvent> events, final float tickDelta) {
        this.vertexConsumers = vertexConsumers;
        this.tickDelta = tickDelta;
        transforms = new ArrayList<>();
        pushMatrixMultiply(stack.peek().getPositionMatrix());
        textAdapter = new GuiVcpTextAdapter(this);
        emitter = new GuiQuadEmitterImpl(vertexConsumers, this, new MutableGuiQuadImpl());
        context = () -> emitter;
        inputContext = new GuiInputContextImpl(mouseX, mouseY, events);
    }

    public GuiContextImpl(final List<GuiTransform> transforms, final VertexConsumerProvider vertexConsumers, final GuiInputContextImpl inputContext, final float tickDelta) {
        this.transforms = transforms;
        this.vertexConsumers = vertexConsumers;
        this.inputContext = inputContext;
        this.tickDelta = tickDelta;
        textAdapter = new GuiVcpTextAdapter(this);
        emitter = new GuiQuadEmitterImpl(vertexConsumers, this, new MutableGuiQuadImpl());
        context = () -> emitter;
    }

    @Override
    public GuiContext createChild() {
        return new GuiContextImpl(new ArrayList<>(transforms), vertexConsumers, inputContext, tickDelta);
    }

    @Override
    public void pushQuadTransform(final GuiTransform transform) {
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
    public void renderText(final OrderedText text, final TextOutline outline, final int colour, final int outlineColour, final int underlineColour) {
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        final Matrix4f identity = Matrix4f.translate(0, 0, 0);
        switch (outline) {
            case NONE -> textRenderer.draw(text, 0, 0, colour, false, identity, textAdapter, true, underlineColour, LightmapTextureManager.MAX_LIGHT_COORDINATE);
            case OUTLINE -> textRenderer.drawWithOutline(text, 0, 0, colour, outlineColour, identity, textAdapter, LightmapTextureManager.MAX_LIGHT_COORDINATE);
            case SHADOW -> textRenderer.draw(text, 0, 0, colour, true, identity, textAdapter, true, underlineColour, LightmapTextureManager.MAX_LIGHT_COORDINATE);
        }
    }

    @Override
    public float getTickDelta() {
        return tickDelta;
    }

    @Override
    public GuiInputContext getInputContext() {
        return inputContext;
    }

    @Override
    public GuiQuadEmitterImpl getEmitter() {
        return emitter;
    }

    public boolean transformQuad(final MutableGuiQuad quad) {
        for (int i = transforms.size() - 1; i >= 0; i--) {
            if (!transforms.get(i).transform(quad, context)) {
                return false;
            }
        }
        return true;
    }

    public VertexConsumer getVertexConsumerForLayer(final RenderLayer layer) {
        return vertexConsumers.getBuffer(layer);
    }
}
