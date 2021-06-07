package io.github.stuff_stuffs.tbcexgui.client.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.stuff_stuffs.tbcexgui.client.render.ScissorStack;
import io.github.stuff_stuffs.tbcexgui.client.util.Rect2d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class TextWidget extends AbstractWidget {
    private final WidgetPosition position;
    private final double width, height;
    private final Supplier<Text> message;
    private final IntSupplier colour;
    private final BooleanSupplier center;
    private final DoubleSupplier maxWidth;

    public TextWidget(final WidgetPosition position, final double width, final double height, final Supplier<Text> message, final IntSupplier colour, final BooleanSupplier center, final DoubleSupplier maxWidth) {
        this.position = position;
        this.width = width;
        this.height = height;
        this.message = message;
        this.colour = colour;
        this.center = center;
        this.maxWidth = maxWidth;
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return position;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        final Rect2d rect = new Rect2d(position.getX() * getScreenWidth(), position.getY() * getScreenHeight(), position.getX() * getScreenWidth() + width, position.getY() * getScreenHeight() + height);
        return rect.isIn(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        final Rect2d rect = new Rect2d(position.getX() * getScreenWidth(), position.getY() * getScreenHeight(), position.getX() * getScreenWidth() + width, position.getY() * getScreenHeight() + height);
        return rect.isIn(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        final Rect2d rect = new Rect2d(position.getX() * getScreenWidth(), position.getY() * getScreenHeight(), position.getX() * getScreenWidth() + width, position.getY() * getScreenHeight() + height);
        return rect.isIn(mouseX, mouseY);
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        matrices.translate(position.getX() * getScreenWidth(), position.getY() * getScreenHeight(), 0);
        ScissorStack.push(matrices, 0, 0, width, height);

        final Matrix4f model = matrices.peek().getModel();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        final BufferBuilder builder = Tessellator.getInstance().getBuffer();
        builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        builder.vertex(model, 1000, -1000, 0).color(255, 0, 0, 255).next();
        builder.vertex(model, -1000, -1000, 0).color(255, 0, 0, 255).next();
        builder.vertex(model, -1000, 1000, 0).color(255, 0, 0, 255).next();
        builder.vertex(model, 1000, 1000, 0).color(255, 0, 0, 255).next();
        builder.end();
        BufferRenderer.draw(builder);

        final float scale = 1 / (float) Math.min(getPixelWidth(), getPixelHeight());
        matrices.scale(scale, scale, 1);
        matrices.scale(2, 1.65f, 0);
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        final int colour = this.colour.getAsInt();
        final List<OrderedText> wrapped = textRenderer.wrapLines(message.get(), (int) (Math.min(maxWidth.getAsDouble() * getPixelWidth(), width * getPixelWidth())));
        float textHeight = 0;
        final boolean centered = center.getAsBoolean();
        for (final OrderedText text : wrapped) {
            textRenderer.draw(matrices, text, centered ? textRenderer.getWidth(text) / 2f : 0, textHeight, colour);
            textHeight += textRenderer.fontHeight * 1.1f;
        }
        ScissorStack.pop();
    }
}
