package io.github.stuff_stuffs.tbcexgui.client.widget.interaction;

import io.github.stuff_stuffs.tbcexgui.client.util.ItemStackLike;
import io.github.stuff_stuffs.tbcexgui.client.util.NinePatch;
import io.github.stuff_stuffs.tbcexgui.client.util.Rect2d;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class InventorySlotsWidget extends AbstractWidget {
    private static final Map<NinePatch.Part, Sprite> SPRITE_MAP;
    private static final Map<NinePatch.Part, Sprite> SELECTED_SPRITE_MAP;
    private static boolean RELOAD_SPRITE_MAP = true;
    private final WidgetPosition position;
    private final double size;
    private final ItemStackLike[][] inventory;
    private final double borderWidth;
    private final Handler handler;
    private int selectedX = 0;
    private int selectedY = 0;

    public InventorySlotsWidget(final WidgetPosition position, final double size, final ItemStackLike[][] inventory, final double borderWidth, final Handler handler) {
        this.position = position;
        this.size = size;
        this.inventory = inventory;
        this.borderWidth = borderWidth;
        this.handler = handler;
    }

    public Rect2d[][] getAreas() {
        final Rect2d[][] areas = new Rect2d[inventory.length][];
        final double x = position.getX();
        final double y = position.getY();
        for (int i = 0; i < inventory.length; i++) {
            areas[i] = new Rect2d[inventory[i].length];
            for (int j = 0; j < areas[i].length; j++) {
                areas[i][j] = new Rect2d(x + i * size, y + size * j, x + i * size + size, y + size * j + size);
            }
        }
        return areas;
    }

    public void setSelected(final int x, final int y) {
        selectedX = x;
        selectedY = y;
    }

    @Override
    public void setFocused(final boolean focused) {
        super.setFocused(focused);
        handler.focusChange(this, focused);
    }

    public void setItemStack(final int x, final int y, final ItemStackLike itemStack) {
        inventory[x][y] = itemStack;
    }

    public @Nullable ItemStackLike getItemStack(final int x, final int y) {
        return inventory[x][y];
    }

    public int getHeight() {
        return inventory.length;
    }

    public int getWidth(final int row) {
        return inventory[row].length;
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return position;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        final Rect2d[][] areas = getAreas();
        for (int i = 0; i < areas.length; i++) {
            final Rect2d[] row = areas[i];
            for (int j = 0; j < row.length; j++) {
                if (row[j].isIn(mouseX, mouseY)) {
                    handler.onClick(this, button, i, j);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        final Rect2d[][] areas = getAreas();
        for (int i = 0; i < areas.length; i++) {
            final Rect2d[] row = areas[i];
            for (int j = 0; j < row.length; j++) {
                if (row[j].isIn(mouseX, mouseY)) {
                    handler.onReleased(this, button, i, j);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        final Rect2d[][] areas = getAreas();
        for (int i = 0; i < areas.length; i++) {
            final Rect2d[] row = areas[i];
            for (int j = 0; j < row.length; j++) {
                if (row[j].isIn(mouseX, mouseY)) {
                    handler.onDrag(this, mouseX, mouseY, deltaX, deltaY);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        final Rect2d[][] areas = getAreas();
        for (int i = 0; i < areas.length; i++) {
            final Rect2d[] row = areas[i];
            for (int j = 0; j < row.length; j++) {
                if (row[j].isIn(mouseX, mouseY)) {
                    handler.onScroll(this, i, j, amount);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        if (RELOAD_SPRITE_MAP) {
            reloadSpriteMap();
            RELOAD_SPRITE_MAP = false;
        }
        final double x = position.getX();
        final double y = position.getY();

        final BufferBuilder bufferBuilder = NinePatch.renderSetup();
        for (int i = 0; i < inventory.length; i++) {
            final ItemStackLike[] row = inventory[i];
            for (int j = 0; j < row.length; j++) {
                final Map<NinePatch.Part, Sprite> spriteMap = (i == selectedX && j == selectedY) ? SELECTED_SPRITE_MAP : SPRITE_MAP;
                NinePatch.renderMain(spriteMap, x + i * size, y + j * size, size, size, getHorizontalPixel(), getVerticalPixel(), borderWidth, 0xFFFFFFFF, matrices, bufferBuilder);
            }
        }
        NinePatch.renderEnd(bufferBuilder);
    }

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        return false;
    }

    private static void reloadSpriteMap() {
        //TODO sprites
        Identifier base = new Identifier("tbcexgui", "gui/hotbar/single");
        for (final NinePatch.Part part : NinePatch.Part.values()) {
            SPRITE_MAP.put(part, MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(part.append(base)));
        }
        base = new Identifier("tbcexgui", "gui/hotbar/single/selected");
        for (final NinePatch.Part part : NinePatch.Part.values()) {
            SELECTED_SPRITE_MAP.put(part, MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(part.append(base)));
        }
    }

    public interface Handler {
        default void onClick(final InventorySlotsWidget widget, final int button, final int x, final int y) {
        }

        default void onReleased(final InventorySlotsWidget widget, final int button, final int x, final int y) {
        }

        default void onDrag(final InventorySlotsWidget widget, final double x, final double y, final double dx, final double dy) {
        }

        default void onScroll(final InventorySlotsWidget widget, final int x, final int y, final double amount) {
        }

        default void focusChange(final InventorySlotsWidget widget, final boolean focused) {
        }
    }

    static {
        SPRITE_MAP = new EnumMap<>(NinePatch.Part.class);
        SELECTED_SPRITE_MAP = new EnumMap<>(NinePatch.Part.class);
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> RELOAD_SPRITE_MAP = true);
    }
}
