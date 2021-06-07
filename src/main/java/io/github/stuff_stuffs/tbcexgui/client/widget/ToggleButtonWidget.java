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

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ToggleButtonWidget extends AbstractWidget {
    private final WidgetPosition position;
    private final double width, height;
    private final Consumer<Boolean> toggle;
    private final BooleanSupplier canToggle;
    private final Supplier<Text> message;
    private boolean state;


    public ToggleButtonWidget(final WidgetPosition position, final double width, final double height, final Consumer<Boolean> toggle, final BooleanSupplier canToggle, final Supplier<Text> message) {
        this.width = width;
        this.height = height;
        this.position = position;
        this.toggle = toggle;
        this.canToggle = canToggle;
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
            if (button == GLFW.GLFW_MOUSE_BUTTON_1 && canToggle.getAsBoolean()) {
                state = !state;
                toggle.accept(state);
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
        final boolean toggleable = canToggle.getAsBoolean();
        if (!toggleable) {
            builder.vertex(model, (float) (position.getX() * getScreenWidth() + width), (float) (position.getY() * getScreenHeight()), (float) position.getZ())             .texture(200 / 256f, 46 / 256f).next();
            builder.vertex(model, (float) (position.getX() * getScreenWidth()), (float) (position.getY() * getScreenHeight()), (float) position.getZ())                     .texture(0, 46 / 256f).next();
            builder.vertex(model, (float) (position.getX() * getScreenWidth()), (float) (position.getY() * getScreenHeight() + height), (float) position.getZ())            .texture(0, (46 + 20) / 256f).next();
            builder.vertex(model, (float) (position.getX() * getScreenWidth() + width), (float) (position.getY() * getScreenHeight() + height), (float) position.getZ())    .texture(200 / 256f, (46 + 20) / 256f).next();
        } else if (!rect.isIn(mouseX, mouseY)) {
            builder.vertex(model, (float) (position.getX() * getScreenWidth() + width), (float) (position.getY() * getScreenHeight()), (float) position.getZ())         .texture(200 / 256f, 66 / 256f).next();
            builder.vertex(model, (float) (position.getX() * getScreenWidth()), (float) (position.getY() * getScreenHeight()), (float) position.getZ())                 .texture(0, 66 / 256f).next();
            builder.vertex(model, (float) (position.getX() * getScreenWidth()), (float) (position.getY() * getScreenHeight() + height), (float) position.getZ())        .texture(0, (66 + 20) / 256f).next();
            builder.vertex(model, (float) (position.getX() * getScreenWidth() + width), (float) (position.getY() * getScreenHeight() + height), (float) position.getZ()).texture(200 / 256f, (66 + 20) / 256f).next();
        } else {
            builder.vertex(model, (float) (position.getX() * getScreenWidth() + width), (float) (position.getY() * getScreenHeight()), (float) position.getZ())             .texture(200 / 256f, 86 / 256f).next();
            builder.vertex(model, (float) (position.getX() * getScreenWidth()), (float) (position.getY() * getScreenHeight()), (float) position.getZ())                     .texture(0, 86 / 256f).next();
            builder.vertex(model, (float) (position.getX() * getScreenWidth()), (float) (position.getY() * getScreenHeight() + height), (float) position.getZ())            .texture(0, (86 + 20) / 256f).next();
            builder.vertex(model, (float) (position.getX() * getScreenWidth() + width), (float) (position.getY() * getScreenHeight() + height), (float) position.getZ())    .texture(200 / 256f, (86 + 20) / 256f).next();
        }
        builder.end();
        BufferRenderer.draw(builder);
        final int colour = state ? 0xffffffff : 0xffa0a0a0;
        matrices.translate(position.getX() * getScreenWidth() + width/2d, position.getY() * getScreenHeight() + height/3d, 0);
        float scale = 1/ (float)Math.min(getPixelWidth(), getPixelHeight());
        matrices.scale(scale, scale, 1);
        matrices.scale(2, 1.65f, 0);
        DrawableHelper.drawCenteredText(matrices, textRenderer, message.get(), 0, 0, colour);
    }
}
