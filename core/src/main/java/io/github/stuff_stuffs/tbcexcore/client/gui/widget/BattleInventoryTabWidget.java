package io.github.stuff_stuffs.tbcexcore.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.stuff_stuffs.tbcexcore.client.util.ItemStackInfo;
import io.github.stuff_stuffs.tbcexgui.client.render.ScissorStack;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexutil.client.RenderUtil;
import io.github.stuff_stuffs.tbcexutil.common.Rect2d;
import io.github.stuff_stuffs.tbcexutil.common.colour.Colour;
import io.github.stuff_stuffs.tbcexutil.common.colour.IntRgbColour;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

public class BattleInventoryTabWidget extends AbstractWidget {
    public static final int COLUMN_COUNT = 3;
    private static final Colour EQUIPED_COLOR = new IntRgbColour(200, 31, 0);
    private final WidgetPosition position;
    private final Supplier<List<ItemStackInfo>> stacks;
    private final double borderThickness;
    private final double entryHeight;
    private final double verticalSpacing;
    private final DoubleSupplier width;
    private final DoubleSupplier height;
    private final IntConsumer onSelect;
    private final DoubleSelect doubleSelect;
    private double pos = 0;
    private int selectedIndex = 0;

    public BattleInventoryTabWidget(final WidgetPosition position, final Supplier<List<ItemStackInfo>> stacks, final double borderThickness, final double entryHeight, final double verticalSpacing, final DoubleSupplier width, final DoubleSupplier height, final IntConsumer onSelect, final DoubleSelect doubleSelect) {
        this.position = position;
        this.stacks = stacks;
        this.borderThickness = borderThickness;
        this.entryHeight = entryHeight;
        this.verticalSpacing = verticalSpacing;
        this.width = width;
        this.height = height;
        this.onSelect = onSelect;
        this.doubleSelect = doubleSelect;
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return position;
    }

    public void resetSelectedIndex() {
        setSelectedIndex(-1, 0, 0);
    }

    public void setSelectedIndex(final int selectedIndex, final double mouseX, final double mouseY) {
        if (0 <= selectedIndex && selectedIndex < stacks.get().size()) {
            if (selectedIndex != this.selectedIndex) {
                this.selectedIndex = selectedIndex;
                onSelect.accept(selectedIndex);
                doubleSelect.onDoubleSelect(-1, 0, 0);
            } else {
                doubleSelect.onDoubleSelect(selectedIndex, mouseX, mouseY);
            }
        } else {
            this.selectedIndex = -1;
            onSelect.accept(selectedIndex);
            doubleSelect.onDoubleSelect(-1, 0, 0);
        }
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (new Rect2d(position.getX(), position.getY(), position.getX() + width.getAsDouble(), position.getY() + height.getAsDouble()).isIn(mouseX, mouseY)) {
            final int index = findHoverIndex(mouseX, mouseY + pos);
            setSelectedIndex(index, mouseX, mouseY);
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
        final Matrix4f model = matrices.peek().getPositionMatrix();
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
        List<ItemStackInfo> stacks = this.stacks.get();
        for (int i = 0; i < stacks.size(); i++) {
            renderInfo(stacks.get(i), buffer, matrices, i, hoverIndex);
        }
        buffer.end();
        BufferRenderer.draw(buffer);
        render(vertexConsumers -> {
            for (int i = 0; i < stacks.size(); i++) {
                renderDecorations(stacks.get(i), matrices, i, hoverIndex, vertexConsumers);
            }
        });

        matrices.pop();
        ScissorStack.pop();
    }

    private void renderDecorations(final ItemStackInfo info, final MatrixStack matrices, final int index, final int hoverIndex, final VertexConsumerProvider vertexConsumers) {
        final float offsetX = (float) position.getX();
        final float offsetY = (float) position.getY();
        final double maxWidth = ((width.getAsDouble() - 2 * borderThickness) / (double) COLUMN_COUNT);
        final double y = offsetY + borderThickness + index * entryHeight + index * verticalSpacing;
        final boolean shadow = index == hoverIndex || selectedIndex == index;
        final Text name = info.stack.getItem().getName();
        renderFitTextWrap(matrices, name, offsetX + borderThickness, y, maxWidth, entryHeight, shadow, IntRgbColour.WHITE, 255, vertexConsumers);
        renderFitTextWrap(matrices, new LiteralText("" + info.stack.getCount()), offsetX + borderThickness + maxWidth, y, maxWidth, entryHeight, shadow, IntRgbColour.WHITE, 255, vertexConsumers);
        renderFitTextWrap(matrices, info.stack.getItem().getRarity().getAsText(), offsetX + borderThickness + maxWidth + maxWidth, y, maxWidth, entryHeight, shadow, new IntRgbColour(info.stack.getItem().getRarity().getRarity().getColour()), 255, vertexConsumers);
    }

    private void renderInfo(final ItemStackInfo info, final VertexConsumer vertexConsumer, final MatrixStack matrices, final int index, final int hoverIndex) {
        final double offsetX = position.getX();
        final double offsetY = position.getY();
        final Matrix4f model = matrices.peek().getPositionMatrix();
        final float startX = (float) (offsetX + borderThickness);
        final float endX = (float) (offsetX + width.getAsDouble() - borderThickness);
        final float startY = (float) (offsetY + borderThickness + index * entryHeight + index * verticalSpacing);
        final float endY = (float) (offsetY + borderThickness + index * entryHeight + index * verticalSpacing + entryHeight);
        final Colour backgroundColour = getBackgroundColour(index);
        final int alpha;
        if (hoverIndex == index || selectedIndex == index) {
            alpha = 0xFF;
        } else {
            alpha = 0x77;
        }
        info.location.ifLeft(loc -> {
            RenderUtil.colour(vertexConsumer.vertex(model, endX, startY, 0), backgroundColour, alpha).next();
            RenderUtil.colour(vertexConsumer.vertex(model, startX, startY, 0), backgroundColour, alpha).next();
            RenderUtil.colour(vertexConsumer.vertex(model, startX, endY, 0), backgroundColour, alpha).next();
            RenderUtil.colour(vertexConsumer.vertex(model, endX, endY, 0), backgroundColour, alpha).next();
        }).ifRight(loc -> {
            RenderUtil.colour(vertexConsumer.vertex(model, endX, startY, 0), EQUIPED_COLOR, 255).next();
            RenderUtil.colour(vertexConsumer.vertex(model, startX, startY, 0), EQUIPED_COLOR, 255).next();
            RenderUtil.colour(vertexConsumer.vertex(model, startX, endY, 0), EQUIPED_COLOR, 255).next();
            RenderUtil.colour(vertexConsumer.vertex(model, endX, endY, 0), EQUIPED_COLOR, 255).next();
        });
    }

    private static Colour getBackgroundColour(final int index) {
        return (index & 1) == 0 ? BattleInventoryFilterWidget.FIRST_BACKGROUND_COLOUR : BattleInventoryFilterWidget.SECOND_BACKGROUND_COLOUR;
    }

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        final double offsetX = position.getX();
        final double offsetY = position.getY();
        if (keyCode == GLFW.GLFW_KEY_UP) {
            final int index = selectedIndex - 1;
            final double startX = (offsetX + borderThickness);
            final double endX = (offsetX + width.getAsDouble() - borderThickness);
            final double startY = (offsetY + borderThickness + selectedIndex * entryHeight + index * verticalSpacing);
            final double endY = (offsetY + borderThickness + index * entryHeight + index * verticalSpacing + entryHeight);
            pos = Math.min(Math.max(pos - entryHeight, 0), getListHeight() - (offsetY - 2 * borderThickness));
            setSelectedIndex(index, (startX + endX) / 2, (startY + endY) / 2);
        } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
            final int index = selectedIndex + 1;
            final double startX = (offsetX + borderThickness);
            final double endX = (offsetX + width.getAsDouble() - borderThickness);
            final double startY = (offsetY + borderThickness + selectedIndex * entryHeight + index * verticalSpacing);
            final double endY = (offsetY + borderThickness + index * entryHeight + index * verticalSpacing + entryHeight);
            pos = Math.min(Math.max(pos + entryHeight, 0), getListHeight() - (offsetY - 2 * borderThickness));
            setSelectedIndex(index, (startX + endX) / 2, (startY + endY) / 2);
        } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            final int index = selectedIndex;
            final double startX = (offsetX + borderThickness);
            final double endX = (offsetX + width.getAsDouble() - borderThickness);
            final double startY = (offsetY + borderThickness + selectedIndex * entryHeight + index * verticalSpacing);
            final double endY = (offsetY + borderThickness + index * entryHeight + index * verticalSpacing + entryHeight);
            setSelectedIndex(index, (startX + endX) / 2, (startY + endY) / 2);
        } else {
            return false;
        }
        return true;
    }

    private int findHoverIndex(final double mouseX, final double mouseY) {
        final double offsetX = position.getX();
        final double offsetY = position.getY();
        final double width = this.width.getAsDouble();
        for (int index = 0; index < stacks.get().size(); index++) {
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
        final int size = stacks.get().size();
        return size * entryHeight + size * verticalSpacing;
    }

    public boolean tick() {
        pos = Math.min(Math.max(pos, 0), getListHeight() - (height.getAsDouble() - 2 * borderThickness));
        return false;
    }

    public interface DoubleSelect {
        void onDoubleSelect(int index, double mouseX, double mouseY);
    }
}
