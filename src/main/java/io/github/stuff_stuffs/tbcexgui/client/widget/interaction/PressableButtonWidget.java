package io.github.stuff_stuffs.tbcexgui.client.widget.interaction;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.stuff_stuffs.tbcexgui.client.util.Rect2d;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class PressableButtonWidget extends AbstractWidget {
    private static boolean RELOAD_SPRITE_MAP = true;
    private static final Map<ButtonState, Map<ButtonPart, Sprite>> SPRITE_MAP = new EnumMap<>(ButtonState.class);
    private final WidgetPosition position;
    private final DoubleSupplier borderWidth;
    private final BooleanSupplier enabled;
    private final double width;
    private final double height;
    private final Supplier<Text> message;
    private final Supplier<List<TooltipComponent>> tooltip;
    private final Runnable onClick;
    private boolean held = false;

    public PressableButtonWidget(final WidgetPosition position, final DoubleSupplier borderWidth, final BooleanSupplier enabled, final double width, final double height, final Supplier<Text> message, final Supplier<List<TooltipComponent>> tooltip, final Runnable onClick) {
        this.position = position;
        this.borderWidth = borderWidth;
        this.enabled = enabled;
        this.width = width;
        this.height = height;
        this.message = message;
        this.tooltip = tooltip;
        this.onClick = onClick;
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return position;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        final Rect2d rect = new Rect2d(position.getX(), position.getY(), position.getX() + width, position.getY() + height);
        if (rect.isIn(mouseX, mouseY)) {
            if (button == 0 && !held && enabled.getAsBoolean()) {
                held = true;
                onClick.run();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        final Rect2d rect = new Rect2d(position.getX(), position.getY(), position.getX() + width, position.getY() + height);
        if (button == 0) {
            held = false;
        }
        return rect.isIn(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        final Rect2d rect = new Rect2d(position.getX(), position.getY(), position.getX() + width, position.getY() + height);
        return rect.isIn(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        final Rect2d rect = new Rect2d(position.getX(), position.getY(), position.getX() + width, position.getY() + height);
        return rect.isIn(mouseX, mouseY);
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        if (RELOAD_SPRITE_MAP) {
            reloadSpriteMap();
            RELOAD_SPRITE_MAP = false;
        }
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 10f);
        final ButtonState state;
        final double positionX = position.getX();
        final double positionY = position.getY();
        if (enabled.getAsBoolean()) {
            if (held) {
                state = ButtonState.HELD;
            } else {
                final Rect2d rect = new Rect2d(positionX, positionY, positionX + width, positionY + height);
                if (rect.isIn(mouseX, mouseY)) {
                    state = ButtonState.HOVERED;
                } else {
                    state = ButtonState.ACTIVE;
                }
            }
        } else {
            state = ButtonState.INACTIVE;
        }
        final Map<ButtonPart, Sprite> sprites = PressableButtonWidget.SPRITE_MAP.get(state);

        final double borderWidth = this.borderWidth.getAsDouble();
        final double horizontalPixel = getHorizontalPixel();
        final double verticalPixel = getVerticalPixel();

        final BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        //top left
        renderRectangle(
                matrices,
                positionX,
                positionY,
                horizontalPixel * 4 * borderWidth,
                verticalPixel * 4 * borderWidth,
                sprites.get(ButtonPart.TOP_LEFT),
                0xffffffff,
                bufferBuilder
        );
        //top middle
        renderRectangle(
                matrices,
                positionX + horizontalPixel * 4 * borderWidth,
                positionY,
                width - horizontalPixel * 8 * borderWidth,
                verticalPixel * 4 * borderWidth,
                sprites.get(ButtonPart.TOP_MIDDLE),
                0xffffffff,
                bufferBuilder
        );
        //top right
        renderRectangle(
                matrices,
                positionX + width - 4 * horizontalPixel * borderWidth,
                positionY,
                horizontalPixel * 4 * borderWidth,
                verticalPixel * 4 * borderWidth,
                sprites.get(ButtonPart.TOP_RIGHT),
                0xffffffff,
                bufferBuilder
        );
        //left
        renderRectangle(
                matrices,
                positionX,
                positionY + verticalPixel * 4 * borderWidth,
                horizontalPixel * 4 * borderWidth,
                height - verticalPixel * 8 * borderWidth,
                sprites.get(ButtonPart.MIDDLE_LEFT),
                0xffffffff,
                bufferBuilder
        );
        //middle
        renderRectangle(
                matrices,
                positionX + horizontalPixel * 4 * borderWidth,
                positionY + verticalPixel * 4 * borderWidth,
                width - horizontalPixel * 8 * borderWidth,
                height - verticalPixel * 8 * borderWidth,
                sprites.get(ButtonPart.MIDDLE_MIDDLE),
                0xffffffff,
                bufferBuilder
        );
        //right
        renderRectangle(
                matrices,
                positionX + width - horizontalPixel * 4 * borderWidth,
                positionY + verticalPixel * 4 * borderWidth,
                horizontalPixel * 4 * borderWidth,
                height - verticalPixel * 8 * borderWidth,
                sprites.get(ButtonPart.MIDDLE_RIGHT),
                0xffffffff,
                bufferBuilder
        );

        //bottom left
        renderRectangle(
                matrices,
                positionX,
                positionY + height - verticalPixel * 4 * borderWidth,
                horizontalPixel * 4 * borderWidth,
                verticalPixel * 4 * borderWidth,
                sprites.get(ButtonPart.BOTTOM_LEFT),
                0xffffffff,
                bufferBuilder
        );
        //bottom middle
        renderRectangle(
                matrices,
                positionX + horizontalPixel * 4 * borderWidth,
                positionY + height - verticalPixel * 4 * borderWidth,
                width - horizontalPixel * 8 * borderWidth,
                verticalPixel * 4 * borderWidth,
                sprites.get(ButtonPart.BOTTOM_MIDDLE),
                0xffffffff,
                bufferBuilder
        );
        //bottom right
        renderRectangle(
                matrices,
                positionX + width - 4 * horizontalPixel * borderWidth,
                positionY + height - verticalPixel * 4 * borderWidth,
                horizontalPixel * 4 * borderWidth,
                verticalPixel * 4 * borderWidth,
                sprites.get(ButtonPart.BOTTOM_RIGHT),
                0xffffffff,
                bufferBuilder
        );

        final boolean shadow = !(state == ButtonState.INACTIVE || state == ButtonState.HELD);
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        final Text text = message.get();

        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        final List<OrderedText> wrapped = textRenderer.wrapLines(text, (int) (width * getPixelWidth()));
        final double textYCenter = (positionY + height / 2.0) + textRenderer.fontHeight / 2.0 / (double) getPixelHeight();
        final double textYBottom = textYCenter - (((wrapped.size() + 1) / 2d) * textRenderer.fontHeight) / (double) getPixelHeight();
        for (int i = 0; i < wrapped.size(); i++) {
            final OrderedText current = wrapped.get(i);
            matrices.push();
            final double textWidth = textRenderer.getWidth(current);
            final double textY = textYBottom + (textRenderer.fontHeight * i) / (double) getPixelHeight();
            matrices.translate((positionX + width / 2.0) - textWidth / 2 / getPixelWidth(), textY, 0);
            matrices.scale(1 / (float) getPixelWidth(), 1 / (float) getPixelHeight(), 1);
            if (shadow) {
                textRenderer.drawWithShadow(matrices, current, 0, 0, -1);
            } else {
                textRenderer.draw(matrices, current, 0, 0, -1);
            }
            matrices.pop();
        }

        if (state == ButtonState.HOVERED) {
            matrices.translate(0,0,1);
            renderTooltip(matrices, tooltip.get(), mouseX, mouseY);
        }
    }

    private static void reloadSpriteMap() {
        for (final ButtonState state : ButtonState.values()) {
            final Map<ButtonPart, Sprite> spriteMap = new EnumMap<>(ButtonPart.class);
            for (final ButtonPart part : ButtonPart.values()) {
                spriteMap.put(part, MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(part.getIdentifier(state)));
            }
            SPRITE_MAP.put(state, spriteMap);
        }
    }
}
