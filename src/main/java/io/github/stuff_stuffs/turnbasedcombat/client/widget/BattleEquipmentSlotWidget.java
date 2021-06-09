package io.github.stuff_stuffs.turnbasedcombat.client.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.AbstractSpruceWidget;
import io.github.stuff_stuffs.turnbasedcombat.client.battle.data.ClientBattleWorld;
import io.github.stuff_stuffs.turnbasedcombat.client.render.battle.BattleEquipmentRenderingRegistry;
import io.github.stuff_stuffs.turnbasedcombat.client.render.SpriteLike;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment.BattleEquipment;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment.BattleEquipmentSlot;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BattleEquipmentSlotWidget extends AbstractSpruceWidget {
    private final BattleParticipantHandle handle;
    private final BattleEquipmentSlot slot;

    public BattleEquipmentSlotWidget(@NotNull final Position position, final BattleParticipantHandle handle, final BattleEquipmentSlot slot, final int width, final int height) {
        super(position);
        this.handle = handle;
        this.slot = slot;
        this.width = width;
        this.height = height;
    }

    private @Nullable BattleEquipment getEquipment() {
        final ClientBattleWorld battleWorld = ClientBattleWorld.get(MinecraftClient.getInstance().world);
        final Battle battle = battleWorld.getBattle(handle.battleId());
        if (battle != null) {
            final EntityStateView view = battle.getStateView().getParticipant(handle);
            if (view != null) {
                return view.getEquiped(slot);
            }
        }
        return null;
    }

    @Override
    protected void renderWidget(final MatrixStack matrices, final int mouseX, final int mouseY, final float delta) {

    }

    @Override
    protected void renderBackground(final MatrixStack matrices, final int mouseX, final int mouseY, final float delta) {
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        final SpriteLike sprite = BattleEquipmentRenderingRegistry.getBackgroundSprite(slot.type());
        sprite.bind(0);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        matrices.scale(width, height, 1);
        final Matrix4f model = matrices.peek().getModel();
        final BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(model, 0, 1, 0).texture(sprite.getMinU(), sprite.getMaxV()).next();
        bufferBuilder.vertex(model, 1, 1, 0).texture(sprite.getMaxU(), sprite.getMaxV()).next();
        bufferBuilder.vertex(model, 1, 0, 0).texture(sprite.getMaxU(), sprite.getMinV()).next();
        bufferBuilder.vertex(model, 0, 0, 0).texture(sprite.getMinU(), sprite.getMinV()).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }
}
