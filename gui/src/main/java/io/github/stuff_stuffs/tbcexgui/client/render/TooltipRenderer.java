package io.github.stuff_stuffs.tbcexgui.client.render;

import io.github.stuff_stuffs.tbcexgui.client.api.GuiContext;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiRenderMaterial;
import io.github.stuff_stuffs.tbcexgui.client.api.GuiRenderMaterialFinder;
import io.github.stuff_stuffs.tbcexgui.client.api.text.TextDrawer;
import io.github.stuff_stuffs.tbcexgui.client.api.text.TextDrawers;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Identifier;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class TooltipRenderer {
    private static final Map<NinePatch.Part, Sprite> TOOLTIP_SPRITE_MAP = new EnumMap<>(NinePatch.Part.class);
    private static final TextDrawer TOOLTIP_TEXT_DRAWER = TextDrawers.oneShot(TextDrawers.HorizontalJustification.LEFT, TextDrawers.VerticalJustification.BOTTOM, -1, 0, false);
    private static boolean RELOAD_SPRITE_MAP = true;

    private TooltipRenderer() {
    }

    public static void render(final List<OrderedText> components, final double x, final double y, final GuiContext context) {
        if (RELOAD_SPRITE_MAP) {
            reloadSpriteMap();
        }
        double maxWidth = 0;
        double height = 0;
        final MinecraftClient client = MinecraftClient.getInstance();
        for (final OrderedText component : components) {
            maxWidth = Math.max(maxWidth, context.getTextRenderer().getWidth(component));
            height += context.getTextRenderer().getHeight();
        }
        final double actualWidth = maxWidth / (double) client.getWindow().getScaledWidth() + 0.01;
        final double actualHeight = height / (double) client.getWindow().getScaledWidth() + 0.01;
        final GuiRenderMaterial material = GuiRenderMaterialFinder.finder().ignoreLight(true).ignoreTexture(false).texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).translucent(true).depthTest(false).find();
        NinePatch.render(TOOLTIP_SPRITE_MAP, x - 0.005, y - 0.005, actualWidth, actualHeight, 0.005, 0.005, 1 / 64.0, context, material);
        final int size = components.size();
        for (int i = 0; i < size; i++) {
            context.pushTranslate(0, (i * actualHeight) / (double) size, 0);
            TOOLTIP_TEXT_DRAWER.draw(actualWidth, actualHeight / size, components.get(i), context);
            context.popGuiTransform();
        }
    }

    private static void reloadSpriteMap() {
        RELOAD_SPRITE_MAP = false;
        final Identifier base = new Identifier("tbcexgui", "gui/tooltip");
        for (final NinePatch.Part part : NinePatch.Part.values()) {
            TOOLTIP_SPRITE_MAP.put(part, MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(part.append(base)));
        }
    }

    static {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier("tbcexgui", "tooltip_sprite_callback");
            }

            @Override
            public void reload(final ResourceManager manager) {
                RELOAD_SPRITE_MAP = true;
            }
        });
    }
}
