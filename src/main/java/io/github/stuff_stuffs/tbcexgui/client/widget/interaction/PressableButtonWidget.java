package io.github.stuff_stuffs.tbcexgui.client.widget.interaction;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.stuff_stuffs.tbcexgui.client.util.Rect2d;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;

import java.util.EnumMap;
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
    private final Runnable onClick;
    private boolean held = false;

    private double verticalPixel = 1 / 480d;
    private double horizontalPixel = 1 / 640d;

    public PressableButtonWidget(final WidgetPosition position, final DoubleSupplier borderWidth, final BooleanSupplier enabled, final double width, final double height, final Supplier<Text> message, final Runnable onClick) {
        this.position = position;
        this.borderWidth = borderWidth;
        this.enabled = enabled;
        this.width = width;
        this.height = height;
        this.message = message;
        this.onClick = onClick;
    }

    @Override
    public void resize(final double width, final double height, final int pixelWidth, final int pixelHeight) {
        super.resize(width, height, pixelWidth, pixelHeight);
        horizontalPixel = 1 / (double) pixelWidth;
        verticalPixel = 1 / (double) pixelHeight;
        while (horizontalPixel < 0.005) {
            horizontalPixel = horizontalPixel * 2;
        }
        while (verticalPixel < 0.005) {
            verticalPixel = verticalPixel * 2;
        }
        if (horizontalPixel < verticalPixel / 2d) {
            double inc = 1;
            while (inc * horizontalPixel < verticalPixel) {
                inc++;
            }
            horizontalPixel = inc * horizontalPixel;
        } else if (verticalPixel < horizontalPixel / 2d) {
            double inc = 1;
            while (inc * verticalPixel < horizontalPixel) {
                inc++;
            }
            verticalPixel = inc * verticalPixel;
        }
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
        return rect.isIn(mouseX,mouseY);
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        final Rect2d rect = new Rect2d(position.getX(), position.getY(), position.getX() + width, position.getY() + height);
        return rect.isIn(mouseX,mouseY);
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
        if (enabled.getAsBoolean()) {
            if (held) {
                state = ButtonState.HELD;
            } else {
                final Rect2d rect = new Rect2d(position.getX(), position.getY(), position.getX() + width, position.getY() + height);
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

        final BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        //top left
        renderRectangle(
                matrices,
                position.getX(),
                position.getY(),
                horizontalPixel * 4 * borderWidth,
                verticalPixel * 4 * borderWidth,
                sprites.get(ButtonPart.TOP_LEFT),
                0xffffffff,
                bufferBuilder
        );
        //top middle
        renderRectangle(
                matrices,
                position.getX() + horizontalPixel * 4 * borderWidth,
                position.getY(),
                width - horizontalPixel * 8 * borderWidth,
                verticalPixel * 4 * borderWidth,
                sprites.get(ButtonPart.TOP_MIDDLE),
                0xffffffff,
                bufferBuilder
        );
        //top right
        renderRectangle(
                matrices,
                position.getX() + width - 4 * horizontalPixel * borderWidth,
                position.getY(),
                horizontalPixel * 4 * borderWidth,
                verticalPixel * 4 * borderWidth,
                sprites.get(ButtonPart.TOP_RIGHT),
                0xffffffff,
                bufferBuilder
        );
        //left
        renderRectangle(
                matrices,
                position.getX(),
                position.getY() + verticalPixel * 4 * borderWidth,
                horizontalPixel * 4 * borderWidth,
                height - verticalPixel * 8 * borderWidth,
                sprites.get(ButtonPart.MIDDLE_LEFT),
                0xffffffff,
                bufferBuilder
        );
        //middle
        renderRectangle(
                matrices,
                position.getX() + horizontalPixel * 4 * borderWidth,
                position.getY() + verticalPixel * 4 * borderWidth,
                width - horizontalPixel * 8 * borderWidth,
                height - verticalPixel * 8 * borderWidth,
                sprites.get(ButtonPart.MIDDLE_MIDDLE),
                0xffffffff,
                bufferBuilder
        );
        //right
        renderRectangle(
                matrices,
                position.getX() + width - horizontalPixel * 4 * borderWidth,
                position.getY() + verticalPixel * 4 * borderWidth,
                horizontalPixel * 4 * borderWidth,
                height - verticalPixel * 8 * borderWidth,
                sprites.get(ButtonPart.MIDDLE_RIGHT),
                0xffffffff,
                bufferBuilder
        );

        //bottom left
        renderRectangle(
                matrices,
                position.getX(),
                position.getY() + height - verticalPixel * 4 * borderWidth,
                horizontalPixel * 4 * borderWidth,
                verticalPixel * 4 * borderWidth,
                sprites.get(ButtonPart.BOTTOM_LEFT),
                0xffffffff,
                bufferBuilder
        );
        //bottom middle
        renderRectangle(
                matrices,
                position.getX() + horizontalPixel * 4 * borderWidth,
                position.getY() + height - verticalPixel * 4 * borderWidth,
                width - horizontalPixel * 8 * borderWidth,
                verticalPixel * 4 * borderWidth,
                sprites.get(ButtonPart.BOTTOM_MIDDLE),
                0xffffffff,
                bufferBuilder
        );
        //bottom right
        renderRectangle(
                matrices,
                position.getX() + width - 4 * horizontalPixel * borderWidth,
                position.getY() + height - verticalPixel * 4 * borderWidth,
                horizontalPixel * 4 * borderWidth,
                verticalPixel * 4 * borderWidth,
                sprites.get(ButtonPart.BOTTOM_RIGHT),
                0xffffffff,
                bufferBuilder
        );

        final boolean shadow = !(state == ButtonState.INACTIVE || state == ButtonState.HELD);
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        final Text text = message.get();
        matrices.push();
        double textWidth = textRenderer.getWidth(text);
        matrices.translate((position.getX() + width / 2.0) - textWidth/2 /getPixelWidth(), (position.getY() + height / 2.0) - textRenderer.fontHeight/2.0 / (double)getPixelHeight(), 0);
        matrices.scale(1/(float)getPixelWidth(),1/(float)getPixelHeight(),1 );
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);


        if (shadow) {
            textRenderer.drawWithShadow(matrices, text, 0,0, -1);
        } else {
            textRenderer.draw(matrices, text, 0,0, -1);
        }
        matrices.pop();
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

    private static void renderRectangle(final MatrixStack matrices, final double x, final double y, final double width, final double height, final Sprite sprite, final int colour, final VertexConsumer consumer) {
        final Matrix4f model = matrices.peek().getModel();
        final int alpha = (colour >> 24) & 0xff;
        final int red = (colour >> 16) & 0xff;
        final int green = (colour >> 8) & 0xff;
        final int blue = (colour) & 0xff;
        consumer.vertex(model, (float) (x + width), (float) y, 0).color(red, green, blue, alpha).texture(sprite.getMaxU(), sprite.getMinV()).next();
        consumer.vertex(model, (float) x, (float) y, 0).color(red, green, blue, alpha).texture(sprite.getMinU(), sprite.getMinV()).next();
        consumer.vertex(model, (float) x, (float) (y + height), 0).color(red, green, blue, alpha).texture(sprite.getMinU(), sprite.getMaxV()).next();
        consumer.vertex(model, (float) (x + width), (float) (y + height), 0).color(red, green, blue, alpha).texture(sprite.getMaxU(), sprite.getMaxV()).next();
    }
}
