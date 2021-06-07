package io.github.stuff_stuffs.tbcexgui.client.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.stuff_stuffs.tbcexgui.client.util.Rect2d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

public class ButtonWidget extends AbstractWidget {
    private final WidgetPosition position;
    private final double width, height;
    private final Runnable onPress;
    private final Supplier<Text> message;


    public ButtonWidget(final WidgetPosition position, final double width, final double height, final Runnable onPress, final Supplier<Text> message) {
        this.width = width;
        this.height = height;
        this.position = position;
        this.onPress = onPress;
        this.message = message;
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return position;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        final Rect2d rect = new Rect2d(position.getX() * getScreenWidth(), position.getY() * getScreenHeight(), position.getX() * getScreenWidth() + width, position.getY() * getScreenHeight() + height);
        if (rect.isIn(mouseX, mouseY)) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                onPress.run();
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
            return true;
        }
        return false;
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
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ClickableWidget.WIDGETS_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        final BufferBuilder builder = Tessellator.getInstance().getBuffer();
        final Rect2d rect = new Rect2d(position.getX() * getScreenWidth(), position.getY() * getScreenHeight(), position.getX() * getScreenWidth() + width, position.getY() * getScreenHeight() + height);
        builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        final Matrix4f model = matrices.peek().getModel();
        if (!rect.isIn(mouseX, mouseY)) {
            builder.vertex(model, (float) (position.getX() * getScreenWidth() + width), (float) (position.getY() * getScreenHeight()), (float) position.getZ()).texture(200 / 256f, 66 / 256f).next();
            builder.vertex(model, (float) (position.getX() * getScreenWidth()), (float) (position.getY() * getScreenHeight()), (float) position.getZ()).texture(0, 66 / 256f).next();
            builder.vertex(model, (float) (position.getX() * getScreenWidth()), (float) (position.getY() * getScreenHeight() + height), (float) position.getZ()).texture(0, (66 + 20) / 256f).next();
            builder.vertex(model, (float) (position.getX() * getScreenWidth() + width), (float) (position.getY() * getScreenHeight() + height), (float) position.getZ()).texture(200 / 256f, (66 + 20) / 256f).next();
        } else {
            builder.vertex(model, (float) (position.getX() * getScreenWidth() + width), (float) (position.getY() * getScreenHeight()), (float) position.getZ()).texture(200 / 256f, 86 / 256f).next();
            builder.vertex(model, (float) (position.getX() * getScreenWidth()), (float) (position.getY() * getScreenHeight()), (float) position.getZ()).texture(0, 86 / 256f).next();
            builder.vertex(model, (float) (position.getX() * getScreenWidth()), (float) (position.getY() * getScreenHeight() + height), (float) position.getZ()).texture(0, (86 + 20) / 256f).next();
            builder.vertex(model, (float) (position.getX() * getScreenWidth() + width), (float) (position.getY() * getScreenHeight() + height), (float) position.getZ()).texture(200 / 256f, (86 + 20) / 256f).next();
        }
        builder.end();
        BufferRenderer.draw(builder);
        final int colour = 0xffffffff;
        matrices.translate(position.getX() + width / 2d, position.getY() + height / 3d, 0);
        final float scale = 1 / (float) Math.min(getPixelWidth(), getPixelHeight());
        matrices.scale(scale, scale, 1);
        matrices.scale(2, 1.65f, 0);
        DrawableHelper.drawCenteredText(matrices, textRenderer, message.get(), 0, 0, colour);
    }
}
