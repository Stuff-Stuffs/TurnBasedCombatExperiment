package io.github.stuff_stuffs.tbcexcore.client.render.battle;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemStack;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

import java.util.Iterator;

public interface BattleParticipantItemRenderer {
    void render(BattleParticipantItemStack stack, BattleParticipantStateView battleParticipantState, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float tickDelta);

    class DefaultRenderer implements BattleParticipantItemRenderer {
        public static final DefaultRenderer INSTANCE = new DefaultRenderer();

        private DefaultRenderer() {
        }

        @Override
        public void render(final BattleParticipantItemStack stack, final BattleParticipantStateView battleParticipantState, final MatrixStack matrices, final VertexConsumerProvider vertexConsumers, final float tickDelta) {
            final Iterator<ItemStack> iterator = BattleParticipantItemType.toItemStack(stack).iterator();
            if (iterator.hasNext()) {
                final ItemStack itemStack = iterator.next();
                MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, ModelTransformation.Mode.FIXED, LightmapTextureManager.pack(15, 15), OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, battleParticipantState.getHandle().participantId().hashCode());
            }
        }
    }
}
