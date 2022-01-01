package io.github.stuff_stuffs.tbcexgui.client.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiInputContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiTransform;
import io.github.stuff_stuffs.tbcexgui.client.api.MutableGuiQuad;
import io.github.stuff_stuffs.tbcexgui.client.impl.render.*;
import io.github.stuff_stuffs.tbcexgui.client.render.GuiRenderLayers;
import io.github.stuff_stuffs.tbcexutil.common.Vec2d;
import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class GuiContextImpl implements GuiContext {
    private final List<GuiTransform> transforms;
    private final Queue<DeferredEmittedQuad> quadStorage = new ArrayDeque<>();
    private final SortedMap<StencilState, List<DeferredEmittedQuad>> inUse = new Object2ObjectRBTreeMap<>(StencilState.COMPARATOR);
    private final Stack<StencilState> stencilStack = new ReferenceArrayList<>();
    private final VertexConsumerProvider.Immediate vertexConsumers;
    private final GuiVcpTextAdapter textAdapter;
    private final GuiQuadEmitterImpl emitter;
    private final GuiInputContextImpl inputContext;
    private float tickDelta;
    private int deferredCounter;

    public GuiContextImpl(final VertexConsumerProvider.Immediate vertexConsumers) {
        this.vertexConsumers = vertexConsumers;
        transforms = new ArrayList<>();
        textAdapter = new GuiVcpTextAdapter(this);
        emitter = new GuiQuadEmitterImpl(this, new MutableGuiQuadImpl());
        inputContext = new GuiInputContextImpl();
    }

    public void setup(final MatrixStack stack, final float tickDelta, final double mouseX, final double mouseY, final List<GuiInputContext.InputEvent> events) {
        this.tickDelta = tickDelta;
        transforms.clear();
        while (!stencilStack.isEmpty()) {
            stencilStack.pop();
        }
        pushMatrixMultiply(stack.peek().getPositionMatrix());
        inputContext.setup(mouseX, mouseY, events);
    }

    @Override
    public void pushQuadTransform(final GuiTransform transform) {
        transforms.add(transform);
        if (transform instanceof ScissorData data) {
            final MutableGuiQuadImpl quad = new MutableGuiQuadImpl();
            quad.pos(0, data.x, data.y);
            quad.pos(1, data.x, data.y + data.height);
            quad.pos(2, data.x + data.width, data.y + data.height);
            quad.pos(3, data.x + data.width, data.y);
            transformQuad(quad, true);
            stencilStack.push(new StencilState(stencilStack.isEmpty() ? null : stencilStack.top(), quad, deferredCounter++));
        }
    }

    @Override
    public void popQuadTransform() {
        if (transforms.remove(transforms.size() - 1) instanceof ScissorData) {
            stencilStack.pop();
        }
    }

    @Override
    public Vec2d transformMouseCursor(Vec2d mouseCursor) {
        for (final GuiTransform transform : transforms) {
            mouseCursor = transform.transformMouseCursor(mouseCursor);
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
        return transformQuad(quad, false);
    }

    public boolean transformQuad(final MutableGuiQuad quad, final boolean force) {
        for (int i = transforms.size() - 1; i >= 0; i--) {
            if (!transforms.get(i).transform(quad) && !force) {
                return false;
            }
        }
        return true;
    }

    public VertexConsumer getVertexConsumerForLayer(final RenderLayer layer) {
        return vertexConsumers.getBuffer(layer);
    }

    public DeferredEmittedQuad acquireDeferred() {
        final DeferredEmittedQuad quad;
        if (quadStorage.isEmpty()) {
            quad = new DeferredEmittedQuad();
        } else {
            quad = quadStorage.poll();
        }
        inUse.computeIfAbsent(stencilStack.isEmpty() ? null : stencilStack.top(), state -> new ArrayList<>()).add(quad);
        return quad;
    }

    public void draw() {
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        for (final Map.Entry<StencilState, List<DeferredEmittedQuad>> entry : inUse.entrySet()) {
            final StencilState state = entry.getKey();
            final List<DeferredEmittedQuad> quads = entry.getValue();
            if (state != null) {
                RenderSystem.stencilMask(0xFF);
                RenderSystem.clearStencil(0);
                RenderSystem.clear(GL11.GL_STENCIL_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
                RenderSystem.stencilOp(GL11.GL_INCR, GL11.GL_INCR, GL11.GL_INCR);
                RenderSystem.stencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
                StencilState stencilState = state;
                final VertexConsumer consumer = vertexConsumers.getBuffer(GuiRenderLayers.STENCIL_LAYER);
                while (stencilState != null) {
                    for (int i = 0; i < 4; i++) {
                        consumer.vertex(stencilState.quad.x(i), stencilState.quad.y(i), stencilState.quad.depth());
                        consumer.next();
                    }
                    stencilState = stencilState.parent;
                }
                vertexConsumers.draw();
                RenderSystem.stencilMask(0x00);
                RenderSystem.stencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
                RenderSystem.stencilFunc(GL11.GL_EQUAL, state.depth + 1, 0xFF);
            } else {
                RenderSystem.stencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
                RenderSystem.stencilFunc(GL11.GL_ALWAYS, 0, 0xFF);
            }
            quads.forEach(quad -> quad.emit(vertexConsumers));
            quadStorage.addAll(quads);
            vertexConsumers.draw();
        }
        inUse.clear();
        GL11.glDisable(GL11.GL_STENCIL_TEST);
    }

    private static final class StencilState {
        public static final Comparator<StencilState> COMPARATOR = Comparator.nullsFirst(Comparator.<StencilState>comparingInt(state -> state.depth).thenComparingInt(state -> state.counter));
        private final @Nullable StencilState parent;
        private final MutableGuiQuadImpl quad;
        private final int depth;
        private final int counter;

        private StencilState(@Nullable final StencilState parent, final MutableGuiQuadImpl quad, final int counter) {
            this.parent = parent;
            this.quad = quad;
            this.counter = counter;
            if (parent == null) {
                depth = 0;
            } else {
                depth = parent.depth + 1;
            }
        }
    }
}
