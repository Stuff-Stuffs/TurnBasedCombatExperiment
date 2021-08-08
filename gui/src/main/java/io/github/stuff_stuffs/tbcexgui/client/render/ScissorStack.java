package io.github.stuff_stuffs.tbcexgui.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vector4f;

public final class ScissorStack {
    private static final Stack<Entry> STACK = new ObjectArrayList<>();

    public static void push(final MatrixStack matrices, final double minX, final double minY, final double maxX, final double maxY) {
        push(matrices.peek().getModel(), minX, minY, maxX, maxY);
    }

    public static void push(final Matrix4f model, final double minX, final double minY, final double maxX, final double maxY) {
        final Vector4f min = new Vector4f((float) minX, (float) minY, 1, 1);
        min.transform(model);
        final Vector4f max = new Vector4f((float) maxX, (float) maxY, 1, 1);
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
            final Window window = MinecraftClient.getInstance().getWindow();
            final double scaleFactor = window.getScaleFactor();
            final int height = (entry.maxY - entry.minY);
            RenderSystem.enableScissor((int) (entry.minX * scaleFactor), adaptY(entry.minY, height, scaleFactor), (int) (entry.maxX * scaleFactor) - (int) (entry.minX * scaleFactor), (int) Math.floor(height * scaleFactor)-4);
        }
    }

    //shamelessly stolen from https://github.com/LambdAurora/SpruceUI/blob/1.17/src/main/java/dev/lambdaurora/spruceui/util/ScissorManager.java, width odd adaptions
    private static int adaptY(final int y, final int height, final double scaleFactor) {
        final Window window = MinecraftClient.getInstance().getWindow();
        final int tmpHeight = (int) (window.getFramebufferHeight() / scaleFactor);
        final int scaledHeight = window.getFramebufferHeight() / scaleFactor >= (double) tmpHeight ? tmpHeight + 1 : tmpHeight;
        return (int) (scaleFactor * (scaledHeight - height - y))-1;
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
