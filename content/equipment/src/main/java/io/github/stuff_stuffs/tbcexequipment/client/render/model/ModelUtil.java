package io.github.stuff_stuffs.tbcexequipment.client.render.model;

import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.tbcexequipment.client.material.MaterialPalette;
import io.github.stuff_stuffs.tbcexequipment.client.material.MaterialRenderInfo;
import io.github.stuff_stuffs.tbcexequipment.client.part.PartRenderInfo;
import io.github.stuff_stuffs.tbcexequipment.common.material.Material;
import io.github.stuff_stuffs.tbcexequipment.common.material.Materials;
import io.github.stuff_stuffs.tbcexequipment.common.part.Part;
import io.github.stuff_stuffs.tbcexequipment.common.part.Parts;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import io.github.stuff_stuffs.tbcexutil.common.colour.Colour;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.function.Function;

public final class ModelUtil {
    public static Mesh buildMesh(final Pair<Material, Part> key, final boolean[][] mask, final float thicknessFactor) {
        final Function<Identifier, Sprite> atlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        final Part part = key.getSecond();
        final PartRenderInfo partRenderInfo = PartRenderInfo.get(Parts.REGISTRY.getId(part));
        int maxSize = 0;
        for (final MaterialPalette.EntryType type : MaterialPalette.EntryType.values()) {
            final Sprite sprite = atlas.apply(partRenderInfo.getTexture(type));
            if (sprite.getWidth() != sprite.getHeight()) {
                throw new TBCExException("Non square part mask!");
            }
            if ((sprite.getWidth() & sprite.getWidth() - 1) != 0) {
                throw new TBCExException("Non power of two mask!");
            }
            maxSize = Math.max(maxSize, sprite.getWidth());
        }
        final boolean[][][] opaque = new boolean[MaterialPalette.EntryType.values().length][maxSize][maxSize];
        final boolean[][] masterOpaque = new boolean[maxSize][maxSize];
        final Material material = key.getFirst();
        final MaterialRenderInfo materialRenderInfo = MaterialRenderInfo.get(Materials.REGISTRY.getId(material));
        final int maskFactor = maxSize > mask.length ? maxSize / mask.length : mask.length / maxSize;
        for (final MaterialPalette.EntryType type : MaterialPalette.EntryType.values()) {
            final Sprite sprite = atlas.apply(partRenderInfo.getTexture(type));
            final int width = sprite.getWidth();
            final int height = sprite.getHeight();
            for (int i = 0; i < maxSize; i++) {
                for (int j = 0; j < maxSize; j++) {
                    final boolean maskTranslucent = sprite.isPixelTransparent(0, i / (maxSize / width), j / (maxSize / height));
                    if (!maskTranslucent && mask[maxSize > mask.length ? i / maskFactor : i * maskFactor][maxSize > mask.length ? j / maskFactor : j * maskFactor]) {
                        opaque[type.ordinal()][i][j] = true;
                        masterOpaque[i][j] = true;
                    }
                }
            }
        }
        final MeshBuilder meshBuilder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
        final QuadEmitter quadEmitter = meshBuilder.getEmitter();
        for (final MaterialPalette.EntryType type : MaterialPalette.EntryType.values()) {
            final Sprite sprite = atlas.apply(partRenderInfo.getTexture(type));
            final MaterialPalette.Entry entry = materialRenderInfo.getPalette().getEntry(type);
            final Colour colour = entry.colour();
            final int alpha = entry.alpha();
            final boolean emissive = entry.emissive();
            final RenderMaterial renderMaterial = RendererAccess.INSTANCE.getRenderer().materialFinder().disableAo(0, true).emissive(0, emissive).blendMode(0, alpha == 255 ? BlendMode.SOLID : BlendMode.TRANSLUCENT).find();
            final int packedColour = colour.pack(alpha);
            //TODO greedy meshing
            for (int i = 0; i < mask.length; i++) {
                for (int j = 0; j < mask.length; j++) {
                    if (mask[i][j]) {
                        final int x = i;
                        final int y = mask.length - 1 - j;
                        final float left = x / (float) mask.length;
                        final float right = (x + 1) / (float) mask.length;
                        final float bottom = y / (float) mask.length;
                        final float top = (y + 1) / (float) mask.length;

                        quadEmitter.material(renderMaterial);
                        quadEmitter.square(Direction.SOUTH, left, bottom, right, top, 0.5f - 1 / 32.0f);
                        quadEmitter.spriteColor(0, packedColour, packedColour, packedColour, packedColour);
                        quadEmitter.sprite(2, 0, right, 1 - bottom);
                        quadEmitter.sprite(1, 0, left, 1 - bottom);
                        quadEmitter.sprite(0, 0, left, 1 - top);
                        quadEmitter.sprite(3, 0, right, 1 - top);
                        quadEmitter.spriteBake(0, sprite, MutableQuadView.BAKE_NORMALIZED);
                        quadEmitter.emit();

                        quadEmitter.material(renderMaterial);
                        quadEmitter.square(Direction.NORTH, 1 - right, bottom, 1 - left, top, 0.5f - 1 / 32.0f);
                        quadEmitter.spriteColor(0, packedColour, packedColour, packedColour, packedColour);
                        quadEmitter.sprite(2, 0, left, 1 - bottom);
                        quadEmitter.sprite(1, 0, right, 1 - bottom);
                        quadEmitter.sprite(0, 0, right, 1 - top);
                        quadEmitter.sprite(3, 0, left, 1 - top);
                        quadEmitter.spriteBake(0, sprite, MutableQuadView.BAKE_NORMALIZED);
                        quadEmitter.emit();
                    }
                }
            }
            final boolean[][] typeMask = opaque[type.ordinal()];
            for (int i = 0; i < maxSize; i++) {
                for (int j = 0; j < maxSize; j++) {
                    if (shouldRenderCube(i, j, -1, 0, maxSize, typeMask, masterOpaque)) {
                        quadEmitter.material(renderMaterial);
                        quadEmitter.square(Direction.WEST, 0.5f - (1 / 32f * thicknessFactor), (maxSize - j - 1) / (float) maxSize, 0.5f + 1 / 32f, (maxSize - j) / (float) maxSize, i / (float) maxSize);
                        quadEmitter.spriteColor(0, packedColour, packedColour, packedColour, packedColour);
                        quadEmitter.sprite(0, 0, (i + 0f) / (float) maxSize, j / (float) maxSize);
                        quadEmitter.sprite(1, 0, (i + 0f) / (float) maxSize, (j + 1) / (float) maxSize);
                        quadEmitter.sprite(2, 0, (i + 0.01f) / (float) maxSize, (j + 1) / (float) maxSize);
                        quadEmitter.sprite(3, 0, (i + 0.01f) / (float) maxSize, j / (float) maxSize);
                        quadEmitter.spriteBake(0, sprite, MutableQuadView.BAKE_NORMALIZED).emit();
                    }
                    if (shouldRenderCube(i, j, 1, 0, maxSize, typeMask, masterOpaque)) {
                        quadEmitter.material(renderMaterial);
                        quadEmitter.square(Direction.EAST, 0.5f - (1 / 32f * thicknessFactor), (maxSize - j - 1) / (float) maxSize, 0.5f + 1 / 32f, (maxSize - j) / (float) maxSize, (maxSize - i - 1) / (float) maxSize);
                        quadEmitter.spriteColor(0, packedColour, packedColour, packedColour, packedColour);
                        quadEmitter.sprite(0, 0, (i + 0f) / (float) maxSize, j / (float) maxSize);
                        quadEmitter.sprite(1, 0, (i + 0f) / (float) maxSize, (j + 1) / (float) maxSize);
                        quadEmitter.sprite(2, 0, (i + 0.01f) / (float) maxSize, (j + 1) / (float) maxSize);
                        quadEmitter.sprite(3, 0, (i + 0.01f) / (float) maxSize, j / (float) maxSize);
                        quadEmitter.spriteBake(0, sprite, MutableQuadView.BAKE_NORMALIZED).emit();
                    }
                    if (shouldRenderCube(i, j, 0, -1, maxSize, typeMask, masterOpaque)) {
                        quadEmitter.material(renderMaterial);
                        quadEmitter.square(Direction.UP, i / (float) maxSize, (0.5f - 1 / 32f * thicknessFactor), (i + 1) / (float) maxSize, 0.5f + 1 / 32f, j / (float) maxSize);
                        quadEmitter.spriteColor(0, packedColour, packedColour, packedColour, packedColour);
                        quadEmitter.sprite(0, 0, (i) / (float) maxSize, (j + 0f) / (float) maxSize);
                        quadEmitter.sprite(1, 0, (i) / (float) maxSize, (j + 0.01f) / (float) maxSize);
                        quadEmitter.sprite(2, 0, (i + 1) / (float) maxSize, (j + 0.01f) / (float) maxSize);
                        quadEmitter.sprite(3, 0, (i + 1) / (float) maxSize, (j + 0f) / (float) maxSize);
                        quadEmitter.spriteBake(0, sprite, MutableQuadView.BAKE_NORMALIZED).emit();
                    }
                    if (shouldRenderCube(i, j, 0, 1, maxSize, typeMask, masterOpaque)) {
                        quadEmitter.material(renderMaterial);
                        quadEmitter.square(Direction.DOWN, i / (float) maxSize, (0.5f - 1 / 32f * thicknessFactor), (i + 1) / (float) maxSize, 0.5f + 1 / 32f, (maxSize - j - 1) / (float) maxSize);
                        quadEmitter.spriteColor(0, packedColour, packedColour, packedColour, packedColour);
                        quadEmitter.sprite(0, 0, (i) / (float) maxSize, (j + 0f) / (float) maxSize);
                        quadEmitter.sprite(1, 0, (i) / (float) maxSize, (j + 0.01f) / (float) maxSize);
                        quadEmitter.sprite(2, 0, (i + 1) / (float) maxSize, (j + 0.01f) / (float) maxSize);
                        quadEmitter.sprite(3, 0, (i + 1) / (float) maxSize, (j + 0f) / (float) maxSize);
                        quadEmitter.spriteBake(0, sprite, MutableQuadView.BAKE_NORMALIZED).emit();
                    }
                }
            }
        }
        return meshBuilder.build();
    }

    private static boolean shouldRenderCube(final int x, final int y, final int xOff, final int yOff, final int max, final boolean[][] mask, final boolean[][] masterMask) {
        if (!mask[x][y]) {
            return false;
        }
        final boolean edge;
        if ((x + xOff) < 0 || (x + xOff) == max) {
            edge = true;
        } else if ((y + yOff) < 0 || (y + yOff) == max) {
            edge = true;
        } else {
            edge = false;
        }
        final boolean offsetTransparent = edge || !masterMask[x + xOff][y + yOff];
        return offsetTransparent;
    }

    private ModelUtil() {
    }

    public static Mesh buildMesh(final Pair<Material, Part> key) {
        return buildMesh(key, new boolean[][]{{true}}, 1);
    }
}
