package io.github.stuff_stuffs.tbcexgui.client.widget.panel;

import io.github.stuff_stuffs.tbcexgui.client.render.GuiRenderLayers;
import io.github.stuff_stuffs.tbcexgui.client.render.NinePatch;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractParentWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.ParentWidget;
import io.github.stuff_stuffs.tbcexgui.client.widget.WidgetPosition;
import io.github.stuff_stuffs.tbcexutil.client.RenderUtil;
import io.github.stuff_stuffs.tbcexutil.common.Rect2d;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import io.github.stuff_stuffs.tbcexutil.common.colour.Colour;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Map;

public class TabsWidget extends AbstractWidget {
    private final Map<String, Panel> panels;
    private final WidgetPosition position;
    private final Side side;
    private final double tabSize;
    private @Nullable String selectedPanel;

    public TabsWidget(final WidgetPosition position, final Side side, final double tabSize) {
        panels = new Object2ReferenceOpenHashMap<>();
        this.position = position;
        this.side = side;
        this.tabSize = tabSize;
    }

    public void addPanel(final String name, final Text textName, final double width, final double height, final Identifier sprite) {
        if (panels.containsKey(name)) {
            throw new TBCExException("Tried to add panel: " + name + " twice!");
        }
        final WidgetPosition widgetPosition = WidgetPosition.combine(position, WidgetPosition.of(side.selectX(width, tabSize), side.selectY(height, tabSize), 0));
        final WidgetPosition tabPosition = switch (side) {
            case LEFT, RIGHT -> WidgetPosition.combine(position, WidgetPosition.of(0, tabSize * panels.size() + tabSize, 0));
            case TOP, BOTTOM -> WidgetPosition.combine(position, WidgetPosition.of(tabSize * panels.size() + tabSize, 0, 0));
        };
        final Panel panel = new Panel(widgetPosition, tabPosition, textName, side, width, height, tabSize, sprite);
        panels.put(name, panel);
        panel.resize(getScreenWidth(), getScreenHeight(), getPixelWidth(), getPixelHeight());
        if (selectedPanel == null) {
            selectedPanel = name;
        }
    }

    public ParentWidget getPanel(final String name) {
        return panels.get(name);
    }

    @Override
    public void resize(final double width, final double height, final int pixelWidth, final int pixelHeight) {
        super.resize(width, height, pixelWidth, pixelHeight);
        for (final Panel panel : panels.values()) {
            panel.resize(width, height, pixelWidth, pixelHeight);
        }
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return position;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        for (final Map.Entry<String, Panel> entry : panels.entrySet()) {
            final Panel panel = entry.getValue();
            if (panel.mouseClickTab(mouseX, mouseY, button)) {
                selectedPanel = entry.getKey();
            }
        }
        if (selectedPanel != null) {
            return panels.get(selectedPanel).mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        if (selectedPanel != null) {
            return panels.get(selectedPanel).mouseReleased(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        if (selectedPanel != null) {
            return panels.get(selectedPanel).mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        if (selectedPanel != null) {
            return panels.get(selectedPanel).mouseScrolled(mouseX, mouseY, amount);
        }
        return false;
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        render(vertexConsumers -> {
            for (final Map.Entry<String, Panel> entry : panels.entrySet()) {
                if (!entry.getKey().equals(selectedPanel)) {
                    entry.getValue().renderButton(matrices, mouseX, mouseY, vertexConsumers);
                }
            }
        });
        if (selectedPanel != null) {
            final Panel panel = panels.get(selectedPanel);
            panel.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        if (selectedPanel != null) {
            return panels.get(selectedPanel).keyPress(keyCode, scanCode, modifiers);
        }
        return false;
    }

    public static final class Panel extends AbstractParentWidget {
        private final WidgetPosition position;
        private final WidgetPosition buttonPosition;
        private final Text textName;
        private final Identifier sprite;
        private final Side side;
        private final double width;
        private final double height;
        private final double buttonSize;


        private Panel(final WidgetPosition position, final WidgetPosition buttonPosition, final Text textName, final Side side, final double width, final double height, final double buttonSize, final Identifier sprite) {
            this.position = position;
            this.buttonPosition = buttonPosition;
            this.textName = textName;
            this.buttonSize = buttonSize;
            this.width = width;
            this.height = height;
            this.sprite = sprite;
            this.side = side;
        }

        public boolean mouseClickTab(final double mouseX, final double mouseY, final int button) {
            if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                return false;
            }
            final double x = buttonPosition.getX();
            final double y = buttonPosition.getY();
            return new Rect2d(x, y, x + buttonSize, y + buttonSize).isIn(mouseX, mouseY);
        }

        @Override
        public WidgetPosition getWidgetPosition() {
            return position;
        }

        public void renderButton(final MatrixStack matrices, final double mouseX, final double mouseY, final VertexConsumerProvider vertexConsumers) {
            final VertexConsumer buffer = vertexConsumers.getBuffer(GuiRenderLayers.getPositionColourTextureLayer(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE));
            final Map<NinePatch.Part, Sprite> sprites = BasicPanelWidget.getSprites();
            side.replaceSprites(sprites);
            NinePatch.render(sprites, buttonPosition.getX(), buttonPosition.getY(), buttonSize, buttonSize, getHorizontalPixel(), getVerticalPixel(), 2, matrices, buffer);
            RenderUtil.renderRectangle(matrices, buttonPosition.getX(), buttonPosition.getY(), buttonSize, buttonSize, MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(sprite), Colour.WHITE, 255, vertexConsumers.getBuffer(GuiRenderLayers.getPositionColourTextureLayer(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)));
            final double x = buttonPosition.getX();
            final double y = buttonPosition.getY();
            if (new Rect2d(x, y, x + buttonSize, y + buttonSize).isIn(mouseX, mouseY)) {
                renderTooltip(matrices, List.of(TooltipComponent.of(textName.asOrderedText())), mouseX, mouseY);
            }
        }

        @Override
        public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
            render(vertexConsumers -> {
                final VertexConsumer buffer = vertexConsumers.getBuffer(GuiRenderLayers.getPositionColourTextureLayer(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE));
                final Map<NinePatch.Part, Sprite> sprites = BasicPanelWidget.getSprites();
                NinePatch.render(sprites, position.getX(), position.getY(), width, height, getHorizontalPixel(), getVerticalPixel(), 2, matrices, buffer);
                renderButton(matrices, mouseX, mouseY, vertexConsumers);
            });
            super.render(matrices, mouseX, mouseY, delta);
        }
    }

    public enum Side {
        TOP {
            @Override
            public double selectX(final double panelWidth, final double tabWidth) {
                return 0;
            }

            @Override
            public double selectY(final double panelHeight, final double tabHeight) {
                return -panelHeight;
            }

            @Override
            public void replaceSprites(final Map<NinePatch.Part, Sprite> spriteMap) {
                spriteMap.put(NinePatch.Part.BOTTOM_LEFT, spriteMap.get(NinePatch.Part.MIDDLE_LEFT));
                spriteMap.put(NinePatch.Part.BOTTOM_MIDDLE, spriteMap.get(NinePatch.Part.MIDDLE_MIDDLE));
                spriteMap.put(NinePatch.Part.BOTTOM_RIGHT, spriteMap.get(NinePatch.Part.MIDDLE_RIGHT));
            }
        },
        BOTTOM {
            @Override
            public double selectX(final double panelWidth, final double tabWidth) {
                return 0;
            }

            @Override
            public double selectY(final double panelHeight, final double tabHeight) {
                return tabHeight;
            }

            @Override
            public void replaceSprites(final Map<NinePatch.Part, Sprite> spriteMap) {
                spriteMap.put(NinePatch.Part.BOTTOM_LEFT, spriteMap.get(NinePatch.Part.MIDDLE_LEFT));
                spriteMap.put(NinePatch.Part.BOTTOM_MIDDLE, spriteMap.get(NinePatch.Part.MIDDLE_MIDDLE));
                spriteMap.put(NinePatch.Part.BOTTOM_RIGHT, spriteMap.get(NinePatch.Part.MIDDLE_RIGHT));
            }
        },
        LEFT {
            @Override
            public double selectX(final double panelWidth, final double tabWidth) {
                return tabWidth;
            }

            @Override
            public double selectY(final double panelHeight, final double tabHeight) {
                return 0;
            }

            @Override
            public void replaceSprites(final Map<NinePatch.Part, Sprite> spriteMap) {
                spriteMap.put(NinePatch.Part.TOP_LEFT, spriteMap.get(NinePatch.Part.TOP_MIDDLE));
                spriteMap.put(NinePatch.Part.MIDDLE_LEFT, spriteMap.get(NinePatch.Part.MIDDLE_MIDDLE));
                spriteMap.put(NinePatch.Part.BOTTOM_LEFT, spriteMap.get(NinePatch.Part.BOTTOM_MIDDLE));
            }
        },
        RIGHT {
            @Override
            public double selectX(final double panelWidth, final double tabWidth) {
                return -panelWidth;
            }

            @Override
            public double selectY(final double panelHeight, final double tabHeight) {
                return 0;
            }

            @Override
            public void replaceSprites(final Map<NinePatch.Part, Sprite> spriteMap) {
                spriteMap.put(NinePatch.Part.TOP_LEFT, spriteMap.get(NinePatch.Part.TOP_MIDDLE));
                spriteMap.put(NinePatch.Part.MIDDLE_LEFT, spriteMap.get(NinePatch.Part.MIDDLE_MIDDLE));
                spriteMap.put(NinePatch.Part.BOTTOM_LEFT, spriteMap.get(NinePatch.Part.BOTTOM_MIDDLE));
            }
        };

        public abstract double selectX(double panelWidth, double tabWidth);

        public abstract double selectY(double panelHeight, double tabHeight);

        public abstract void replaceSprites(Map<NinePatch.Part, Sprite> spriteMap);
    }
}
