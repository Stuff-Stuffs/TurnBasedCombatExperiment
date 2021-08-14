package io.github.stuff_stuffs.tbcexcore.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.stuff_stuffs.tbcexcore.client.util.ItemStackInfo;
import io.github.stuff_stuffs.tbcexgui.client.render.ScissorStack;
import io.github.stuff_stuffs.tbcexgui.client.util.Rect2d;
import io.github.stuff_stuffs.tbcexgui.client.util.RenderUtil;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.IntConsumer;

public class BattleInventoryTabWidget extends AbstractWidget {
    public static final int COLUMN_COUNT = 4;
    private final WidgetPosition position;
    private final List<ItemStackInfo> stacks;
    private final double borderThickness;
    private final double entryHeight;
    private final double verticalSpacing;
    private final DoubleSupplier width;
    private final DoubleSupplier height;
    private final IntConsumer onSelect;
    private double pos = 0;
    private int selectedIndex = 0;

    public BattleInventoryTabWidget(final WidgetPosition position, final List<ItemStackInfo> stacks, final double borderThickness, final double entryHeight, final double verticalSpacing, final DoubleSupplier width, final DoubleSupplier height, final IntConsumer onSelect) {
        this.position = position;
        this.stacks = stacks;
        this.borderThickness = borderThickness;
        this.entryHeight = entryHeight;
        this.verticalSpacing = verticalSpacing;
        this.width = width;
        this.height = height;
        this.onSelect = onSelect;
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return position;
    }

    public void setSelectedIndex(final int selectedIndex) {
        if (0 <= selectedIndex && selectedIndex < stacks.size()) {
            if (selectedIndex != this.selectedIndex) {
                this.selectedIndex = selectedIndex;
                onSelect.accept(selectedIndex);
            }
        } else {
            this.selectedIndex = -1;
            onSelect.accept(selectedIndex);
        }
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (new Rect2d(position.getX(), position.getY(), position.getX() + width.getAsDouble(), position.getY() + height.getAsDouble()).isIn(mouseX, mouseY)) {
            final int index = findHoverIndex(mouseX, mouseY + pos);
            if (selectedIndex != index) {
                selectedIndex = index;
                onSelect.accept(index);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        return new Rect2d(position.getX(), position.getY(), position.getX() + width.getAsDouble(), position.getY() + height.getAsDouble()).isIn(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        final double height = this.height.getAsDouble();
        if (new Rect2d(position.getX(), position.getY(), position.getX() + width.getAsDouble(), position.getY() + height).isIn(mouseX, mouseY)) {
            pos = Math.min(Math.max(pos + deltaY, 0), getListHeight() - (height - 2 * borderThickness));
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        final double height = this.height.getAsDouble();
        if (new Rect2d(position.getX(), position.getY(), position.getX() + width.getAsDouble(), position.getY() + height).isIn(mouseX, mouseY)) {
            pos = Math.min(Math.max(pos - amount, 0), getListHeight() - (height - 2 * borderThickness));
            return true;
        }
        return false;
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        final Matrix4f model = matrices.peek().getModel();
        final BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        final double offsetX = position.getX();
        final double offsetY = position.getY();
        final double width = this.width.getAsDouble();
        final double height = this.height.getAsDouble();
        buffer.vertex(model, (float) (offsetX + width), (float) offsetY, 0).color(0, 0, 0, 127).next();
        buffer.vertex(model, (float) offsetX, (float) offsetY, 0).color(0, 0, 0, 127).next();
        buffer.vertex(model, (float) offsetX, (float) (offsetY + height), 0).color(0, 0, 0, 127).next();
        buffer.vertex(model, (float) (offsetX + width), (float) (offsetY + height), 0).color(0, 0, 0, 127).next();
        final double scrollbarThickness = borderThickness / 3.0;
        final double scrollbarHeight = scrollbarThickness * 8;
        final double scrollAreaHeight = height - 2 * borderThickness - scrollbarHeight;
        final double progress = pos / (getListHeight() - (height - 2 * borderThickness));
        buffer.vertex(model, (float) (offsetX + scrollbarThickness * 2), (float) (offsetY + borderThickness + progress * scrollAreaHeight), 0).color(127, 127, 127, 192).next();
        buffer.vertex(model, (float) (offsetX + scrollbarThickness), (float) (offsetY + borderThickness + progress * scrollAreaHeight), 0).color(127, 127, 127, 192).next();
        buffer.vertex(model, (float) (offsetX + scrollbarThickness), (float) (offsetY + borderThickness + progress * scrollAreaHeight + scrollbarHeight), 0).color(127, 127, 127, 192).next();
        buffer.vertex(model, (float) (offsetX + scrollbarThickness * 2), (float) (offsetY + borderThickness + progress * scrollAreaHeight + scrollbarHeight), 0).color(127, 127, 127, 192).next();
        buffer.end();
        BufferRenderer.draw(buffer);
        ScissorStack.push(matrices, borderThickness + offsetX, borderThickness + offsetY, offsetX + width - borderThickness, (offsetY + height - borderThickness) * 1.1);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        matrices.push();
        matrices.translate(0, -pos, 0);
        final int hoverIndex = findHoverIndex(mouseX, mouseY + pos);
        for (int i = 0; i < stacks.size(); i++) {
            renderInfo(stacks.get(i), buffer, matrices, i, hoverIndex);
        }
        buffer.end();
        BufferRenderer.draw(buffer);
        for (int i = 0; i < stacks.size(); i++) {
            renderDecorations(stacks.get(i), matrices, i, hoverIndex);
        }
        matrices.pop();
        ScissorStack.pop();
    }

    private void renderDecorations(final ItemStackInfo info, final MatrixStack matrices, final int index, final int hoverIndex) {
        final float offsetX = (float) position.getX();
        final float offsetY = (float) position.getY();
        final double maxWidth = ((width.getAsDouble() - 2 * borderThickness) / (double) COLUMN_COUNT);
        final double y = offsetY + borderThickness + index * entryHeight + index * verticalSpacing;
        final boolean shadow = index == hoverIndex || selectedIndex == index;
        final Text name = info.stack.getItem().getName();
        renderFitTextWrap(matrices, name, offsetX + borderThickness, y, maxWidth, entryHeight, shadow, -1);
        renderFitTextWrap(matrices, new LiteralText("" + info.stack.getCount()), offsetX + borderThickness + maxWidth, y, maxWidth, entryHeight, shadow, -1);
        renderFitTextWrap(matrices, info.stack.getItem().getCategory().getName(), offsetX + borderThickness + maxWidth + maxWidth, y, maxWidth, entryHeight, shadow, -1);
        renderFitTextWrap(matrices, info.stack.getItem().getRarity().getAsText(), offsetX + borderThickness + maxWidth + maxWidth + maxWidth, y, maxWidth, entryHeight, shadow, info.stack.getItem().getRarity().getRarity().getColour());
    }

    private void renderInfo(final ItemStackInfo info, final BufferBuilder buffer, final MatrixStack matrices, final int index, final int hoverIndex) {
        final double offsetX = position.getX();
        final double offsetY = position.getY();
        final Matrix4f model = matrices.peek().getModel();
        final float startX = (float) (offsetX + borderThickness);
        final float endX = (float) (offsetX + width.getAsDouble() - borderThickness);
        final float startY = (float) (offsetY + borderThickness + index * entryHeight + index * verticalSpacing);
        final float endY = (float) (offsetY + borderThickness + index * entryHeight + index * verticalSpacing + entryHeight);
        int backgroundColour = getBackgroundColour(index);
        if (hoverIndex == index || selectedIndex == index) {
            backgroundColour |= 0xFF000000;
        }
        RenderUtil.colour(buffer.vertex(model, endX, startY, 0), backgroundColour).next();
        RenderUtil.colour(buffer.vertex(model, startX, startY, 0), backgroundColour).next();
        RenderUtil.colour(buffer.vertex(model, startX, endY, 0), backgroundColour).next();
        RenderUtil.colour(buffer.vertex(model, endX, endY, 0), backgroundColour).next();
    }

    private static int getBackgroundColour(final int index) {
        return (index & 1) == 0 ? 0x77111111 : 0x77222222;
    }

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_UP) {
            setSelectedIndex(selectedIndex - 1);
        } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
            setSelectedIndex(selectedIndex + 1);
        } else {
            return false;
        }
        return true;
    }

    private int findHoverIndex(final double mouseX, final double mouseY) {
        final double offsetX = position.getX();
        final double offsetY = position.getY();
        final double width = this.width.getAsDouble();
        for (int index = 0; index < stacks.size(); index++) {
            final double startX = offsetX + borderThickness;
            final double endX = offsetX + width - borderThickness;
            final double startY = offsetY + borderThickness + index * entryHeight + index * verticalSpacing;
            final double endY = startY + entryHeight;
            if (new Rect2d(startX, startY, endX, endY).isIn(mouseX, mouseY)) {
                return index;
            }
        }
        return -1;
    }

    private double getListHeight() {
        final int size = stacks.size();
        return size * entryHeight + size * verticalSpacing;
    }

    public boolean tick() {
        pos = Math.min(Math.max(pos, 0), getListHeight() - (height.getAsDouble() - 2 * borderThickness));
        return false;
    }
}
