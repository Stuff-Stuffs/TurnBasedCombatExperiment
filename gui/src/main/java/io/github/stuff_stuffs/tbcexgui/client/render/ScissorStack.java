package io.github.stuff_stuffs.tbcexgui.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vector4f;

public final class ScissorStack {
    private static final Stack<Entry> STACK = new ObjectArrayList<>();

    public static void push(final MatrixStack matrices, final double minX, final double minY, final double maxX, final double maxY) {
        push(matrices.peek().getModel(), minX, minY, maxX, maxY);
    }

    public static void push(final Matrix4f model, final double minX, final double minY, final double maxX, final double maxY) {
        final Vector4f min = new Vector4f((float) minX, (float) minY, 0, 1);
        min.transform(model);
        final Vector4f max = new Vector4f((float) maxX, (float) maxY, 0, 1);
        max.transform(model);
        push(
                (int) Math.min(Math.floor(min.getX()), Math.floor(max.getX())),
                (int) Math.min(Math.floor(min.getY()), Math.floor(max.getY())),
                (int) Math.max(Math.ceil(min.getX()), Math.ceil(max.getX())),
                (int) Math.max(Math.ceil(min.getY()), Math.ceil(max.getY()))
        );
    }

    public static void push(final int minX, final int minY, final int maxX, final int maxY) {
        final int prevMinX;
        final int prevMinY;
        final int prevMaxX;
        final int prevMaxY;
        if (!STACK.isEmpty()) {
            final Entry prev = STACK.top();
            prevMinX = prev.minX;
            prevMinY = prev.minY;
            prevMaxX = prev.maxX;
            prevMaxY = prev.maxY;
        } else {
            prevMinX = -Integer.MAX_VALUE;
            prevMinY = -Integer.MAX_VALUE;
            prevMaxX = Integer.MAX_VALUE;
            prevMaxY = Integer.MAX_VALUE;
        }
        final int curMinX = Math.max(minX, prevMinX);
        final int curMinY = Math.max(minY, prevMinY);
        final int curMaxX = Math.min(maxX, prevMaxX);
        final int curMaxY = Math.min(maxY, prevMaxY);
        STACK.push(new Entry(Math.min(curMinX, curMaxX), Math.min(curMinY, curMaxY), Math.max(curMaxX, curMinX), Math.max(curMaxY, curMinY)));
        update();
    }

    public static void pop() {
        STACK.pop();
        update();
    }

    private static void update() {
        if (STACK.isEmpty()) {
            RenderSystem.disableScissor();
        } else {
            final Entry entry = STACK.top();
            final int h = MinecraftClient.getInstance().getWindow().getFramebufferHeight();
            final int height = (h - entry.minY) - (h - entry.maxY);
            RenderSystem.enableScissor(entry.minX, h - entry.maxY, entry.maxX - entry.minX, height);
        }
    }

    private static class Entry {
        public final int minX, minY, maxX, maxY;

        public Entry(final int minX, final int minY, final int maxX, final int maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }
    }

    private ScissorStack() {
    }
}
