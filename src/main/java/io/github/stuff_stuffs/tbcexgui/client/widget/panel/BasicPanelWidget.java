package io.github.stuff_stuffs.tbcexgui.client.widget.panel;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.stuff_stuffs.tbcexgui.client.util.Rect2d;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractParentWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.BasicWidgetPosition;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

public class BasicPanelWidget extends AbstractParentWidget {
    private static final Map<PanelPart, Sprite> SPRITE_MAP;
    private static boolean RELOAD_SPRITE_MAP = true;
    private final WidgetPosition position;
    private final BooleanSupplier draggable;
    private final DoubleSupplier borderWidth;
    private BasicWidgetPosition offset = new BasicWidgetPosition(0, 0, 0);
    private WidgetPosition combined;
    private final double panelWidth, panelHeight;

    public BasicPanelWidget(final WidgetPosition position, final BooleanSupplier draggable, DoubleSupplier borderWidth, final double panelWidth, final double panelHeight) {
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
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 10f);
        final BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        double borderWidth = this.borderWidth.getAsDouble();
        double horizontalPixel = getHorizontalPixel();
        double verticalPixel = getVerticalPixel();
        //top left
        renderRectangle(
                matrices,
                combined.getX(),
                combined.getY(),
                horizontalPixel * 4 * borderWidth,
                verticalPixel * 4 * borderWidth,
                SPRITE_MAP.get(PanelPart.TOP_LEFT),
                0xffffffff,
                bufferBuilder
        );
        //top middle
        renderRectangle(
                matrices,
                combined.getX() + horizontalPixel * 4 * borderWidth,
                combined.getY(),
                panelWidth - horizontalPixel * 8 * borderWidth,
                verticalPixel * 4 * borderWidth,
                SPRITE_MAP.get(PanelPart.TOP),
                0xffffffff,
                bufferBuilder
        );
        //top right
        renderRectangle(
                matrices,
                combined.getX() + panelWidth - 4 * horizontalPixel * borderWidth,
                combined.getY(),
                horizontalPixel * 4 * borderWidth,
                verticalPixel * 4 * borderWidth,
                SPRITE_MAP.get(PanelPart.TOP_RIGHT),
                0xffffffff,
                bufferBuilder
        );
        //left
        renderRectangle(
                matrices,
                combined.getX(),
                combined.getY() + verticalPixel * 4 * borderWidth,
                horizontalPixel * 4 * borderWidth,
                panelHeight - verticalPixel * 8 * borderWidth,
                SPRITE_MAP.get(PanelPart.LEFT),
                0xffffffff,
                bufferBuilder
        );
        //middle
        renderRectangle(
                matrices,
                combined.getX() + horizontalPixel * 4 * borderWidth,
                combined.getY() + verticalPixel * 4 * borderWidth,
                panelWidth - horizontalPixel * 8 * borderWidth,
                panelHeight - verticalPixel * 8 * borderWidth,
                SPRITE_MAP.get(PanelPart.MIDDLE),
                0xffffffff,
                bufferBuilder
        );
        //right
        renderRectangle(
                matrices,
                combined.getX() + panelWidth - horizontalPixel * 4 * borderWidth,
                combined.getY() + verticalPixel * 4 * borderWidth,
                horizontalPixel * 4 * borderWidth,
                panelHeight - verticalPixel * 8 * borderWidth,
                SPRITE_MAP.get(PanelPart.RIGHT),
                0xffffffff,
                bufferBuilder
        );

        //bottom left
        renderRectangle(
                matrices,
                combined.getX(),
                combined.getY() + panelHeight - verticalPixel * 4 * borderWidth,
                horizontalPixel * 4 * borderWidth,
                verticalPixel * 4 * borderWidth,
                SPRITE_MAP.get(PanelPart.BOTTOM_LEFT),
                0xffffffff,
                bufferBuilder
        );
        //bottom middle
        renderRectangle(
                matrices,
                combined.getX() + horizontalPixel * 4 * borderWidth,
                combined.getY() + panelHeight - verticalPixel * 4 * borderWidth,
                panelWidth - horizontalPixel * 8 * borderWidth,
                verticalPixel * 4 * borderWidth,
                SPRITE_MAP.get(PanelPart.BOTTOM),
                0xffffffff,
                bufferBuilder
        );
        //bottom right
        renderRectangle(
                matrices,
                combined.getX() + panelWidth - 4 * horizontalPixel * borderWidth,
                combined.getY() + panelHeight - verticalPixel * 4 * borderWidth,
                horizontalPixel * 4 * borderWidth,
                verticalPixel * 4 * borderWidth,
                SPRITE_MAP.get(PanelPart.BOTTOM_RIGHT),
                0xffffffff,
                bufferBuilder
        );
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        super.render(matrices, mouseX, mouseY, delta);
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

    private static void reloadSpriteMap() {
        for (final PanelPart part : PanelPart.values()) {
            SPRITE_MAP.put(part, MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(part.getIdentifier()));
        }
    }

    static {
        SPRITE_MAP = new EnumMap<>(PanelPart.class);
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> RELOAD_SPRITE_MAP = true);
    }
}
