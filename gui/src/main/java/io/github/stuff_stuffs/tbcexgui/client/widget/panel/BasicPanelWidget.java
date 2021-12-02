package io.github.stuff_stuffs.tbcexgui.client.widget.panel;

import io.github.stuff_stuffs.tbcexgui.client.render.GuiRenderLayers;
import io.github.stuff_stuffs.tbcexgui.client.render.NinePatch;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractParentWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.BasicWidgetPosition;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import io.github.stuff_stuffs.tbcexutil.common.Rect2d;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

public class BasicPanelWidget extends AbstractParentWidget {
    private static final Map<NinePatch.Part, Sprite> SPRITE_MAP;
    private static boolean RELOAD_SPRITE_MAP = true;
    private final WidgetPosition position;
    private final BooleanSupplier draggable;
    private final DoubleSupplier borderWidth;
    private BasicWidgetPosition offset = new BasicWidgetPosition(0, 0, 0);
    private WidgetPosition combined;
    private final double panelWidth, panelHeight;

    public BasicPanelWidget(final WidgetPosition position, final BooleanSupplier draggable, final DoubleSupplier borderWidth, final double panelWidth, final double panelHeight) {
        this.position = position;
        this.draggable = draggable;
        this.borderWidth = borderWidth;
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        combined = WidgetPosition.combine(position, offset);
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        final boolean mouseDragged = super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        if (!mouseDragged && draggable.getAsBoolean()) {
            final Rect2d rect2d = new Rect2d(combined.getX(), combined.getY(), combined.getX() + panelWidth, combined.getY() + panelHeight);
            if (rect2d.isIn(mouseX, mouseY)) {
                offset = offset.withX(offset.getX() + deltaX * getScreenWidth()).withY(offset.getY() + deltaY * getScreenHeight());
                combined = WidgetPosition.combine(position, offset);
                return true;
            }
        }
        return mouseDragged;
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return combined;
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        if (RELOAD_SPRITE_MAP) {
            reloadSpriteMap();
            RELOAD_SPRITE_MAP = false;
        }
        render(vertexConsumers -> NinePatch.render(SPRITE_MAP, combined.getX(), combined.getY(), panelWidth, panelHeight, getHorizontalPixel(), getVerticalPixel(), borderWidth.getAsDouble(), matrices, vertexConsumers.getBuffer(GuiRenderLayers.getPositionColourTextureLayer(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE))));
        super.render(matrices, mouseX, mouseY, delta);
    }

    public static Map<NinePatch.Part, Sprite> getSprites() {
        if (RELOAD_SPRITE_MAP) {
            reloadSpriteMap();
            RELOAD_SPRITE_MAP = false;
        }
        return new Object2ReferenceOpenHashMap<>(SPRITE_MAP);
    }

    private static void reloadSpriteMap() {
        final Identifier base = new Identifier("tbcexgui", "gui/panel");
        for (final NinePatch.Part part : NinePatch.Part.values()) {
            SPRITE_MAP.put(part, MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(part.append(base)));
        }
    }

    static {
        SPRITE_MAP = new EnumMap<>(NinePatch.Part.class);
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> RELOAD_SPRITE_MAP = true);
    }
}
