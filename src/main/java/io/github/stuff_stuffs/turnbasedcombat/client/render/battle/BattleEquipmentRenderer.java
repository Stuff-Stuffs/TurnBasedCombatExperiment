package io.github.stuff_stuffs.turnbasedcombat.client.render.battle;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

public interface BattleEquipmentRenderer {
    void render(final MatrixStack matrices, int slotX, int slotY, int slotWidth, int slotHeight, final int mouseX, final int mouseY, final float delta);

    BattleEquipmentRenderer MISSING = (matrices, slotX, slotY, slotWidth, slotHeight, mouseX, mouseY, delta) -> {
        final Sprite sprite = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(new Identifier("not_a_texture"));
        final Matrix4f model = matrices.peek().getModel();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        final BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(model, slotX, slotY + slotHeight, 0).texture(sprite.getMinU(), sprite.getMaxV()).next();
        bufferBuilder.vertex(model, slotX + slotWidth, slotY + slotHeight, 0).texture(sprite.getMaxU(), sprite.getMaxV()).next();
        bufferBuilder.vertex(model, slotX + slotWidth, slotY, 0).texture(sprite.getMaxU(), sprite.getMinV()).next();
        bufferBuilder.vertex(model, slotX, slotY, 0).texture(sprite.getMinU(), sprite.getMinV()).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    };
}
