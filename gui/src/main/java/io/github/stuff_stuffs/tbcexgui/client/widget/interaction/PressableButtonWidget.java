package io.github.stuff_stuffs.tbcexgui.client.widget.interaction;

import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import io.github.stuff_stuffs.tbcexutil.client.NinePatch;
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
import java.util.function.Supplier;

public class PressableButtonWidget extends AbstractWidget {
    private static boolean RELOAD_SPRITE_MAP = true;
    private static final Map<ButtonState, Map<NinePatch.Part, Sprite>> SPRITE_MAP = new EnumMap<>(ButtonState.class);
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

        NinePatch.render(PressableButtonWidget.SPRITE_MAP.get(state), positionX, positionY, width, height, getHorizontalPixel(), getVerticalPixel(), borderWidth.getAsDouble(), matrices);

        final boolean shadow = !(state == ButtonState.INACTIVE || state == ButtonState.HELD);
        final Text text = message.get();
        render(vertexConsumers -> renderFitTextWrap(matrices, text, positionX + 2 * getHorizontalPixel(), positionY, width - 4 * getHorizontalPixel(), height, shadow, -1, vertexConsumers));

        if (state == ButtonState.HOVERED) {
            matrices.translate(0, 0, 1);
            renderTooltip(matrices, tooltip.get(), mouseX, mouseY);
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
