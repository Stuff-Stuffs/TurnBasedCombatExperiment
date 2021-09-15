package io.github.stuff_stuffs.tbcexgui.client.widget.interaction;

import io.github.stuff_stuffs.tbcexgui.client.render.GuiRenderLayers;
import io.github.stuff_stuffs.tbcexgui.client.render.NinePatch;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import io.github.stuff_stuffs.tbcexutil.common.Rect2d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
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


        final boolean shadow = !(state == ButtonState.INACTIVE);
        final Text text = messageFactory.apply(currentState);

        render(vertexConsumers -> {
            NinePatch.render(SPRITE_MAP.get(state), positionX, positionY, width, height, getHorizontalPixel(), getVerticalPixel(), borderWidth.getAsDouble(), matrices, vertexConsumers.getBuffer(GuiRenderLayers.POSITION_COLOUR_LAYER));
            renderFitTextWrap(matrices, text, positionX + getHorizontalPixel(), positionY + getVerticalPixel(), width - 2 * getHorizontalPixel(), height - 2 * getHorizontalPixel(), shadow, -1, vertexConsumers);
        });

        if (state == ButtonState.HOVERED) {
            renderTooltip(matrices, tooltipFactory.apply(currentState), mouseX, mouseY);
        }
    }

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        return false;
    }

    private static void reloadSpriteMap() {
        for (final ButtonState state : ButtonState.values()) {
            final Map<NinePatch.Part, Sprite> spriteMap = new EnumMap<>(NinePatch.Part.class);
            final Identifier base = new Identifier("tbcexgui", "gui/button/" + state.name().toLowerCase(Locale.ROOT));
            for (final NinePatch.Part part : NinePatch.Part.values()) {
                spriteMap.put(part, MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(part.append(base)));
            }
            SPRITE_MAP.put(state, spriteMap);
        }
    }
}
