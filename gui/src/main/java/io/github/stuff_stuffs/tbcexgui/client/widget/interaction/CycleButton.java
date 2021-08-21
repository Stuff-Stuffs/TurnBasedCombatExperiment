package io.github.stuff_stuffs.tbcexgui.client.widget.interaction;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.stuff_stuffs.tbcexutil.client.NinePatch;
import io.github.stuff_stuffs.tbcexutil.client.Rect2d;
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
import net.minecraft.util.Identifier;

import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class CycleButton<T> extends AbstractWidget {
    private static boolean RELOAD_SPRITE_MAP = true;
    private static final Map<ButtonState, Map<NinePatch.Part, Sprite>> SPRITE_MAP = new EnumMap<>(ButtonState.class);
    private final WidgetPosition position;
    private final DoubleSupplier borderWidth;
    private final BooleanSupplier enabled;
    private final double width;
    private final double height;
    private final UnaryOperator<T> successor;
    private final Function<T, Text> messageFactory;
    private final Function<T, List<TooltipComponent>> tooltipFactory;
    private T currentState;

    public CycleButton(final WidgetPosition position, final DoubleSupplier borderWidth, final BooleanSupplier enabled, final double width, final double height, final T start, final UnaryOperator<T> successor, final Function<T, Text> messageFactory, final Function<T, List<TooltipComponent>> tooltipFactory) {
        this.position = position;
        this.borderWidth = borderWidth;
        this.enabled = enabled;
        this.width = width;
        this.height = height;
        currentState = start;
        this.successor = successor;
        this.messageFactory = messageFactory;
        this.tooltipFactory = tooltipFactory;
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return position;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        final Rect2d rect = new Rect2d(position.getX(), position.getY(), position.getX() + width, position.getY() + height);
        final boolean in = rect.isIn(mouseX, mouseY);
        if (in) {
            currentState = successor.apply(currentState);
        }
        return in;
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        final Rect2d rect = new Rect2d(position.getX(), position.getY(), position.getX() + width, position.getY() + height);
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
            final Rect2d rect = new Rect2d(positionX, positionY, positionX + width, positionY + height);
            if (rect.isIn(mouseX, mouseY)) {
                state = ButtonState.HOVERED;
            } else {
                state = ButtonState.ACTIVE;
            }
        } else {
            state = ButtonState.INACTIVE;
        }

        NinePatch.render(SPRITE_MAP.get(state), positionX, positionY, width, height, getHorizontalPixel(), getVerticalPixel(), borderWidth.getAsDouble(), matrices);

        final boolean shadow = !(state == ButtonState.INACTIVE);
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        final Text text = messageFactory.apply(currentState);

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
            matrices.translate(0, 0, 1);
            renderTooltip(matrices, tooltipFactory.apply(currentState), mouseX, mouseY);
        }
    }

    @Override
    public boolean keyPress(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    private static void reloadSpriteMap() {
        for (final ButtonState state : ButtonState.values()) {
            final Map<NinePatch.Part, Sprite> spriteMap = new EnumMap<>(NinePatch.Part.class);
            Identifier base = new Identifier("tbcexgui", "gui/button/" + state.name().toLowerCase(Locale.ROOT));
            for (final NinePatch.Part part : NinePatch.Part.values()) {
                spriteMap.put(part, MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(part.append(base)));
            }
            SPRITE_MAP.put(state, spriteMap);
        }
    }
}
